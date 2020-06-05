package com.vnator.turbinecraft.blocks.generators.t1_furnace_generator;

import com.vnator.turbinecraft.gui.MachineContainer;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class FurnaceGeneratorContainer extends MachineContainer {
    public FurnaceGeneratorContainer(int id, World world, BlockPos pos, PlayerInventory inventory, PlayerEntity playerEntity) {
        super(Registration.BASIC_FURNACE_GENERATOR_CONTAINER, id, world, pos, inventory, playerEntity);

        layoutPlayerInventorySlots(8, 92, playerInventory);
        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            addInventories(Position.CENTER, inv, 0);
        });

        addProgressIcon(false, 0, ((FurnaceGeneratorTile)tileEntity)::getBurnRatio);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerEntity, Registration.BASIC_FURNACE_GENERATOR);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            if (index == 36) {
                if (!this.mergeItemStack(stack, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, itemstack);
            } else {
                if (ForgeHooks.getBurnTime(stack) > 0) {
                    if (!this.mergeItemStack(stack, 36, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 27) {
                    if (!this.mergeItemStack(stack, 27, 36, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 36 && !this.mergeItemStack(stack, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;

    }
}
