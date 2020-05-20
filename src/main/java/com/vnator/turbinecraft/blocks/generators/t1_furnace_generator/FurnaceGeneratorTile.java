package com.vnator.turbinecraft.blocks.generators.t1_furnace_generator;

import com.vnator.turbinecraft.blocks.GeneratorTileEntity;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.animation.TimeValues;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FurnaceGeneratorTile extends GeneratorTileEntity implements ITickableTileEntity {

    private static final long SPEED = 1, FORCE = 1;

    private LazyOptional<IItemHandler> inventory = LazyOptional.of(this::createInventory);

    private int burnTime = 0;
    public int spinTime = 0;

    public FurnaceGeneratorTile() {
        super(Registration.BASIC_FURNACE_GENERATOR_TILE);
    }

    @Override
    public void tick() {
        if(world.isRemote)
            return;

        boolean hasBurned = burnTime > 0;
        if(burnTime > 0){
            burnTime--;
            spinTime++;
            transferRotation(SPEED, FORCE);
            markDirty();
        }

        if(burnTime == 0) {
            inventory.ifPresent(inv -> {
                ItemStack burnable = inv.extractItem(0, 1, false);
                if (burnable != null && !burnable.isEmpty()) {
                    burnTime += ForgeHooks.getBurnTime(burnable);
                    world.setBlockState(getPos(), getBlockState().with(BlockStateProperties.POWERED, true));
                } else if (hasBurned){
                    world.setBlockState(getPos(), getBlockState().with(BlockStateProperties.POWERED, false));
                }
            });
        }
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
        inventory.ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));
        burnTime = compound.getInt("burnTimeLeft");
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        inventory.ifPresent(inv -> {
            CompoundNBT nbt = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            compound.put("inventory", nbt);
        });

        compound.putInt("burnTimeLeft", burnTime);
        return super.write(compound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return inventory.cast();
        }
        return super.getCapability(cap, side);
    }

}