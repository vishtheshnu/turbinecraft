package com.vnator.turbinecraft.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class OutputItemStackHandler extends ItemStackHandler {

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return stack;
    }

    /**
     * Raw Setting of the ItemStack at a particular slot.
     * @param slot Slot of inventory to modify
     * @param stack New ItemStack to set it as
     */
    public void setStackInSlot(int slot, ItemStack stack){

    }
}
