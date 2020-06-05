package com.vnator.turbinecraft.blocks;

import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalAcceptor;
import com.vnator.turbinecraft.util.MachineItemHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.world.storage.loot.conditions.BlockStateProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class MachineTileEntity extends TileEntity implements ITickableTileEntity {

    public static final int TICKS_PER_CLIENT_UPDATE = 80;
    protected LazyOptional<IRotationalAcceptor> rotation = LazyOptional.of(this::createRotationAcceptor);
    public IRotationalAcceptor displayRotation;
    public LazyOptional<MachineItemHandler> autoInventoryHandler, guiInventoryHandler; //ItemHandlers for automation AND gui access
    public NonNullList<ItemStack> inventoryBase;
    public boolean hasRotation;

    public MachineTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        displayRotation = new RotationalAcceptor(){
            @Override
            public void insertEnergy(long speed, long force) {
                if(speed != getSpeed() || force != getForce()) {
                    ticksToClientUpdate = TICKS_PER_CLIENT_UPDATE;
                    updateClient();
                }
                super.insertEnergy(speed, force);
            }
        };
        hasRotation = createRotationAcceptor() != null;
    }

    protected int ticksToClientUpdate;

    /**
     * Transfer speed and force to the Rotation capability that this TileEntity is facing, if any.
     * @param speed Speed of rotational energy
     * @param force Force of rotational energy
     */
    public void transferRotation(long speed, long force){
        rotation.ifPresent(rot -> {
            if(!world.isRemote && (rot.getSpeed() != speed || rot.getForce() != force))
                markDirty();
            rot.setSpeed(speed);
            rot.setForce(force);
        });

        if(!world.isRemote){
            if(speed <= 0 || force <= 0)
                return;
            Direction facing = getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
            TileEntity ent = world.getTileEntity(pos.offset(facing));
            if(ent != null){
                ent.getCapability(RotationProvider.ROTATION_CAPABILITY, facing.getOpposite()).ifPresent(cap -> cap.insertEnergy(speed, force));
            }
        }
    }

    @Override
    public void tick() {
        if(!world.isRemote) {
            if (ticksToClientUpdate <= 0) {
                ticksToClientUpdate = TICKS_PER_CLIENT_UPDATE;
                updateClient();
            }
            ticksToClientUpdate--;
        }
    }

    protected void initInventory(MachineItemHandler autoHandler, MachineItemHandler guiHandler){

        autoInventoryHandler = LazyOptional.of(() -> autoHandler);
        guiInventoryHandler = LazyOptional.of(() -> guiHandler);
    }

    public void updateClient(){
        if(!world.isRemote)
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
    }

    public IRotationalAcceptor getRotationFromWorld(IRotationalAcceptor actual, IRotationalAcceptor display){
        return world.isRemote ? display : actual;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == RotationProvider.ROTATION_CAPABILITY && side == null && hasRotation){
            return rotation.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT compound) {
        if(hasRotation) {
            CompoundNBT rotTag = compound.getCompound("rotation");
            rotation.ifPresent(rot -> rot.deserializeNBT(rotTag));
            displayRotation.deserializeNBT(compound.getCompound("displayRotation"));
        }
        if(compound.contains("inventory")) {
            CompoundNBT inventory = compound.getCompound("inventory");
            //if(inventoryBase == null)
            //    inventoryBase = NonNullList.withSize(inventory.getInt("size"), ItemStack.EMPTY);
            for (int i = 0; i < inventory.getInt("size"); i++) {
                CompoundNBT itemNbt = inventory.getCompound("item" + i);
                ItemStack stack = ItemStack.read(itemNbt);
                inventoryBase.set(i, stack);
            }
        }
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if(hasRotation) {
            rotation.ifPresent(rot -> {
                CompoundNBT nbt = rot.serializeNBT();
                compound.put("rotation", nbt);
            });
            compound.put("displayRotation", displayRotation.serializeNBT());
        }
        if(inventoryBase != null){
            CompoundNBT inventory = new CompoundNBT();
            for(int i = 0; i < inventoryBase.size(); i++) {
                CompoundNBT itemNbt = inventoryBase.get(i).serializeNBT();
                inventory.put("item" + i, itemNbt);
            }
            inventory.putInt("size", inventoryBase.size());
            compound.put("inventory", inventory);
        }
        return super.write(compound);
    }

    public <T extends Comparable<T>, V extends T> void setBlockState(net.minecraft.state.IProperty<T> property,
                                                    V value){
        if(!getBlockState().get(property).equals(value)){
            world.setBlockState(pos, getBlockState().with(property, value), 3);
            markDirty();
        }
    }

    //abstract protected CompoundNBT getMinimalUpdateNbt();
    //abstract protected void setMinimalUpdateNbt(CompoundNBT nbt);

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 108, write(new CompoundNBT()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        read(pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag(); //Avoid: id, x, y, z, ForgeData, ForgeCaps
        write(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        super.handleUpdateTag(tag);
        read(tag);
    }

    public int getTier(long value){
        int tier = 0;
        while(value > 0){
            value >>= 1;
            tier++;
        }
        return tier;
    }

    protected abstract IRotationalAcceptor createRotationAcceptor();
}
