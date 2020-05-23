package com.vnator.turbinecraft.blocks;

import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.storage.loot.conditions.BlockStateProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class GeneratorTileEntity extends TileEntity implements ITickableTileEntity {

    public static final int TICKS_PER_CLIENT_UPDATE = 80;
    protected LazyOptional<IRotationalAcceptor> rotation = LazyOptional.of(this::createRotationAcceptor);

    public GeneratorTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    protected int ticksToClientUpdate;

    /**
     * Transfer speed and force to the Rotation capability that this TileEntity is facing, if any.
     * @param speed Speed of rotational energy
     * @param force Force of rotational energy
     */
    public void transferRotation(long speed, long force){
        if(!world.isRemote){
            rotation.ifPresent(rot -> {
                if(rot.getSpeed() != speed || rot.getForce() != force)
                    markDirty();
                rot.setSpeed(speed);
                rot.setForce(force);
            });
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

    public void updateClient(){
        if(!world.isRemote)
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == RotationProvider.ROTATION_CAPABILITY && side == null){
            return rotation.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT compound) {
        CompoundNBT rotTag = compound.getCompound("rotation");
        rotation.ifPresent(rot -> ((INBTSerializable<CompoundNBT>) rot).deserializeNBT(rotTag));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        rotation.ifPresent(rot -> {
            CompoundNBT nbt = rot.serializeNBT();
            compound.put("rotation", nbt);
        });
        return super.write(compound);
    }

    public <T extends Comparable<T>, V extends T> void setBlockState(net.minecraft.state.IProperty<T> property,
                                                    V value){
        if(!getBlockState().get(property).equals(value))
            world.setBlockState(pos, getBlockState().with(property, value));
    }

    abstract protected CompoundNBT getMinimalUpdateNbt();
    abstract protected void setMinimalUpdateNbt(CompoundNBT nbt);

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 108, getMinimalUpdateNbt());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        setMinimalUpdateNbt(pkt.getNbtCompound());
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

    protected abstract IRotationalAcceptor createRotationAcceptor();
}
