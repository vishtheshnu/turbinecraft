package com.vnator.turbinecraft.blocks.generators.t1_furnace_generator;

import com.vnator.turbinecraft.util.MachineItemHandler;
import com.vnator.turbinecraft.blocks.MachineTileEntity;
import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalAcceptor;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FurnaceGeneratorTile extends MachineTileEntity implements INamedContainerProvider {

    private static final long SPEED = 1, FORCE = 1;
    private static final int BURN_TIME_MULTIPLIER = 4;

    private int burnTime = 0;
    private int burnTotal = 1;
    public int spinTime = 0;

    public FurnaceGeneratorTile() {
        super(Registration.BASIC_FURNACE_GENERATOR_TILE);
        inventoryBase = NonNullList.withSize(1, ItemStack.EMPTY);
        initInventory(new MachineItemHandler(this){
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return ForgeHooks.getBurnTime(stack) > 0;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(ForgeHooks.getBurnTime(stack) <= 0)
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }
        }, new MachineItemHandler(this));
    }

    @Override
    public void tick() {
        super.tick();

        boolean hasBurned = burnTime > 0;
        if(burnTime > 0){
            burnTime--;
            spinTime++;
            transferRotation(SPEED, FORCE);
            displayRotation.insertEnergy(SPEED, FORCE);
            markDirty();
        }else{
            transferRotation(0, 0);
            displayRotation.insertEnergy(0, 0);
        }

        if(burnTime == 0) {
            guiInventoryHandler.ifPresent(inv -> {
                ItemStack burnable = inv.extractItem(0, 1, false);
                if (burnable != null && !burnable.isEmpty()) {
                    burnTime += ForgeHooks.getBurnTime(burnable) * BURN_TIME_MULTIPLIER;
                    burnTotal = burnTime;
                    setBlockState(BlockStateProperties.POWERED, true);
                } else if (hasBurned){
                    setBlockState(BlockStateProperties.POWERED, false);
                }
            });
        }
    }

    public float getBurnRatio(){
        if(burnTotal == 0)
            return 0;
        return 1.0f*burnTime/burnTotal;
    }

    private IItemHandler createInventory(){
        return new ItemStackHandler(1){
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return ForgeHooks.getBurnTime(stack) > 0;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(ForgeHooks.getBurnTime(stack) <= 0)
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Override
    public void read(CompoundNBT compound) {
        CompoundNBT invTag = compound.getCompound("inventory");
        burnTime = compound.getInt("burnTimeLeft");
        burnTotal = compound.getInt("burnTimeTotal");
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("burnTimeLeft", burnTime);
        compound.putInt("burnTimeTotal", burnTotal);
        return super.write(compound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side == null){
            return autoInventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected IRotationalAcceptor createRotationAcceptor() {
        return new RotationalAcceptor();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new FurnaceGeneratorContainer(i, world, pos, playerInventory, playerEntity);
    }
}