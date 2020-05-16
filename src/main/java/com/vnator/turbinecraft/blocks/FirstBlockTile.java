package com.vnator.turbinecraft.blocks;

import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FirstBlockTile extends TileEntity implements ITickableTileEntity {

    public ItemStackHandler inventory;

    public FirstBlockTile(){
        super(Registration.FIRSTBLOCK_TILE);
    }

    @Override
    public void tick() {

    }

    public void read(CompoundNBT tag){
        CompoundNBT comp = tag.getCompound("inventory");
        getInventory().deserializeNBT(comp);
        super.read(tag);
    }

    public CompoundNBT write(CompoundNBT tag){
        CompoundNBT compound = getInventory().serializeNBT();
        tag.put("inventory", compound);
        return super.write(tag);
    }

    private ItemStackHandler getInventory(){
        if(inventory == null)
            inventory = new ItemStackHandler(1){

                @Override
                protected void onContentsChanged(int slot){
                    markDirty();
                }

                @Override
                public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                    if(stack.getItem() == Items.DIAMOND)
                        return super.isItemValid(slot, stack);
                    return false;
                }

                @Nonnull
                @Override
                public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                    if(stack.getItem() != Items.DIAMOND)
                        return stack;

                    return super.insertItem(slot, stack, simulate);
                }
            };
        return inventory;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return LazyOptional.of(() -> (T) getInventory());
        }
        return super.getCapability(cap, side);
    }

}
