package com.vnator.turbinecraft.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class MachineContainer extends Container {

    protected TileEntity tileEntity;
    protected PlayerEntity playerEntity;
    protected IItemHandler playerInventory;

    private int occupiedLeft = 0, occupiedRight = 0; //Space occupied by tanks on left and right

    protected List<ItemHandler> itemHandlers;
    protected List<TankHandler> tankHandlers;
    protected List<FEHandler> feHandlers;
    protected List<ProgressHandler> progressHandlers;

    public enum Position {
        LEFT,
        CENTER,
        RIGHT
    }

    protected MachineContainer(@Nullable ContainerType<?> type, int id, World world, BlockPos pos, PlayerInventory inventory, PlayerEntity playerEntity) {
        super(type, id);
        tileEntity = world.getTileEntity(pos);
        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        this.playerInventory = new InvWrapper(inventory);
        this.playerEntity = playerEntity;

        itemHandlers = new ArrayList<>();
        tankHandlers = new ArrayList<>();
        feHandlers = new ArrayList<>();
        progressHandlers = new ArrayList<>();
        //new ProgressHandler(0, 0, 0, () -> 1);
    }

    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    protected int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    protected void layoutPlayerInventorySlots(int leftCol, int topRow, IItemHandler playerInventory) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        /*
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            if (index == 0) {
                if (!this.mergeItemStack(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, itemstack);
            } else {
                if (stack.getItem() == Items.DIAMOND) {
                    if (!this.mergeItemStack(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 28) {
                    if (!this.mergeItemStack(stack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37 && !this.mergeItemStack(stack, 1, 28, false)) {
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

         */
        return ItemStack.EMPTY;
    }

    public TileEntity getTileEntity(){
        return tileEntity;
    }

    //Methods for accessing and displaying capabilities of the TileEntity

    protected void addTanks(Position position, int ... tankIds){
        if(tankIds.length == 0)
            return;

        int start, space;
        if(position == Position.LEFT){
            start = 5;
            space = 22;
            occupiedLeft += tankIds.length;
        }else if(position == Position.RIGHT){
            start = 170-22;
            space = -22;
            occupiedRight += tankIds.length;
        }else{
            start = 88 - tankIds.length*22/2;
            space = 22;
        }
        for(int id : tankIds){
            TankHandler th = new TankHandler(start, id);
            tankHandlers.add(th);
            start += space;
        }
    }

    protected void addInventories(Position position, IItemHandler inventorySlots, int ... slots){
        if(slots.length == 0)
            return;

        int x, y, xspace, yspace, lineWidth;
        if(position == Position.LEFT){
            lineWidth = 3 - occupiedLeft;
            x = 5 + 22*occupiedLeft;
            xspace = 20;
            yspace = 20;
            y = (31) - (yspace * slots.length/lineWidth)/2;
        }else if(position == Position.RIGHT){
            lineWidth = 3 - occupiedRight;
            x = 150 - 22*occupiedLeft;
            xspace = -20;
            yspace = 20;
            y = (31) - (yspace * slots.length/lineWidth)/2;
        }else{
            lineWidth = 1;
            x = 88 - 9;
            xspace = 20;
            yspace = 20;
            y = (31) - yspace*slots.length/2;
        }

        int xcount = 0, ycount = 0;
        for (int slot : slots) {
            MachineSlotHandler handler = new MachineSlotHandler(inventorySlots, slot, x+1 + xcount * xspace, y+1 + ycount * yspace);
            addSlot(handler);
            itemHandlers.add(new ItemHandler(x + xcount * xspace, y + ycount * yspace, slot, handler.slotNumber));
            xcount++;
            if (xcount >= lineWidth) {
                xcount = 0;
                ycount++;
            }
        }
    }

    protected void addEnergyContainers(Position position, Direction side){

        int start, space;
        if(position == Position.LEFT){
            start = 5;
            space = 22;
            occupiedLeft ++;
        }else if(position == Position.RIGHT){
            start = 170-22;
            space = -22;
            occupiedRight ++;
        }else{
            start = 77;
            space = 22;
        }

        feHandlers.add(new FEHandler(start, side));
        start += space;

    }

    //index = index of transformation icon in guisheet.png

    /**
     * Creates and adds handler object for progress icons, eg. furnace burning icon
     * @param isCenter True if icon is drawn at center height, otherwise is drawn at bottom between rotation elements
     * @param index index of icon to use. In order of what's in the sprite sheet, from top to bottom
     * @param ratioMethod Method that returns a value of 0 to 1 for how much progress has been made
     */
    protected void addProgressIcon(boolean isCenter, int index, ProgressHandler.RatioObtainer ratioMethod){
        ProgressHandler handler = new ProgressHandler(76, isCenter ? 32 : 68, index, ratioMethod);
        progressHandlers.add(handler);
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        itemHandlers.clear();
        tankHandlers.clear();
        feHandlers.clear();
        for(ProgressHandler h : progressHandlers)
            h.ratioFunc = null;
        progressHandlers.clear();
        super.onContainerClosed(playerIn);
    }

    public static class TankHandler{
        public int x, id;
        public TankHandler(int x, int id){
            this.x = x;
            this.id = id;
        }
    }

    public static class ItemHandler{
        public int x, y, id;
        public int slotIndex;
        public ItemHandler(int x, int y, int id, int index){
            this.x = x;
            this.y = y;
            this.id = id;
            this.slotIndex = index;
        }
    }

    public static class FEHandler{
        public int x;
        public Direction side;
        public FEHandler(int x, Direction side){
            this.x = x;
            this.side = side;
        }
    }

    public static class ProgressHandler{
        public int x, y, id;
        public RatioObtainer ratioFunc;
        public ProgressHandler(int x, int y, int id, RatioObtainer ratio){
            this.x = x;
            this.y = y;
            this.id = id;
            this.ratioFunc = ratio;
        }

        public interface RatioObtainer{
            public float getRatio();
        }
    }
}
