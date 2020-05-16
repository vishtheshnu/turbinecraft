package com.vnator.turbinecraft.blocks.generators.t1_furnace_generator;

import com.vnator.turbinecraft.blocks.GeneratorTileEntity;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FurnaceGeneratorTile extends GeneratorTileEntity {
    private static final long SPEED = 1, FORCE = 1;

    private LazyOptional<IItemHandler> inventory = LazyOptional.of(this::createInventory);

    private int burnTime = 0;

    public FurnaceGeneratorTile() {
        super(Registration.BASIC_FURNACE_GENERATOR_TILE);
    }

    private IItemHandler createInventory(){
        return new ItemStackHandler(1){
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                System.out.println("Checking insertability of "+stack.getItem().getName());
                System.out.println(stack.getBurnTime());
                return stack.getBurnTime() > 0;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                System.out.println("Attempting to insert "+stack.getItem().getName());
                System.out.println("Burn Time: "+stack.getBurnTime());

                if(stack.getBurnTime() <= 0)
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Override
    public void tick() {
        /*
        if(burnTime > 0){
            burnTime--;
            transferRotation(SPEED, FORCE);
            System.out.println("Transferring Rotational Power!");
            System.out.println("Burn Time Left: "+burnTime);
        }

        if(burnTime == 0)
            inventory.ifPresent(inv -> {
                ItemStack burnable = inv.extractItem(0, 1, false);
                if(burnable != null && !burnable.isEmpty()) {
                    burnTime += burnable.getBurnTime();
                }
            });

         */
    }

    @Override
    public void read(CompoundNBT compound) {
        System.out.println("Reading");
        CompoundNBT invTag = compound.getCompound("inventory");
        inventory.ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));
        burnTime = compound.getInt("burnTimeLeft");
        System.out.println("Finished reading FurnaceGen");
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