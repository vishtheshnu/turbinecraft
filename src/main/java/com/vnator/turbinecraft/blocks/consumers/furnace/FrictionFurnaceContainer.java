package com.vnator.turbinecraft.blocks.consumers.furnace;

import com.vnator.turbinecraft.blocks.MachineTileEntity;
import com.vnator.turbinecraft.gui.MachineContainer;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.Collection;

public class FrictionFurnaceContainer extends MachineContainer {
    public FrictionFurnaceContainer(int id, World world, BlockPos pos, PlayerInventory inventory, PlayerEntity playerEntity) {
        super(Registration.FRICTION_FURNACE_CONTAINER, id, world, pos, inventory, playerEntity);

        layoutPlayerInventorySlots(8, 92, playerInventory);
        ((MachineTileEntity) tileEntity).guiInventoryHandler.ifPresent(inv -> {
            addInventories(Position.LEFT, inv, 0);
            addInventories(Position.RIGHT, inv,1);
        });

        addProgressIcon(true, 1, ((FrictionFurnaceTile) tileEntity)::getProgressRatio);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerEntity, Registration.FRICTION_FURNACE);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            if (index == 36 || index == 37) {
                if (!this.mergeItemStack(stack, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, itemstack);
            } else {

                boolean isValid = false;
                Collection<IRecipe<IInventory>> recipes = tileEntity.getWorld().getRecipeManager().getRecipes(IRecipeType.SMELTING).values();
                for(IRecipe<IInventory> recipe : recipes){
                    for(ItemStack match : recipe.getIngredients().get(0).getMatchingStacks()) {
                        if (match.isItemEqual(stack)) {
                            isValid = true;
                            break;
                        }
                    }
                }

                if (isValid) {
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
