package com.vnator.turbinecraft.blocks.consumers.furnace;

import com.vnator.turbinecraft.util.MachineItemHandler;
import com.vnator.turbinecraft.blocks.MachineTileEntity;
import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalAcceptor;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class FrictionFurnaceTile extends MachineTileEntity implements INamedContainerProvider {

    private static final int MAX_PROGRESS = 200; //Progress to smelt a single item

    ItemStack itemToProcess = ItemStack.EMPTY;
    ItemStack itemToCreate = ItemStack.EMPTY;

    private float progress;
    public float temperature;

    public FrictionFurnaceTile() {
        super(Registration.FRICTION_FURNACE_TILE);
        inventoryBase = NonNullList.withSize(2, ItemStack.EMPTY);
        initInventory(new MachineItemHandler(this){
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
                super.onContentsChanged(slot);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if(slot == 1)
                    return false;

                Collection<IRecipe<IInventory>> recipes = world.getRecipeManager().getRecipes(IRecipeType.SMELTING).values();
                for(IRecipe<IInventory> recipe : recipes){
                    for(ItemStack match : recipe.getIngredients().get(0).getMatchingStacks()) {
                        if (match.isItemEqual(stack)) {
                            return super.isItemValid(slot, stack);
                        }
                    }
                }

                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(slot == 1)
                    return stack;

                Collection<IRecipe<IInventory>> recipes = world.getRecipeManager().getRecipes(IRecipeType.SMELTING).values();
                for(IRecipe<IInventory> recipe : recipes){
                    for(ItemStack match : recipe.getIngredients().get(0).getMatchingStacks()) {
                        if (match.isItemEqual(stack)) {
                            return super.insertItem(slot, stack, simulate);
                        }
                    }
                }

                return stack;
            }

            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if(slot == 0)
                    return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        }, new MachineItemHandler(this){
            protected void onContentsChanged(int slot) {
                markDirty();
                super.onContentsChanged(slot);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if(slot == 1)
                    return false;

                Collection<IRecipe<IInventory>> recipes = world.getRecipeManager().getRecipes(IRecipeType.SMELTING).values();
                for(IRecipe<IInventory> recipe : recipes){
                    for(ItemStack match : recipe.getIngredients().get(0).getMatchingStacks()) {
                        if (match.isItemEqual(stack)) {
                            return super.isItemValid(slot, stack);
                        }
                    }
                }

                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(slot == 1)
                    return stack;

                Collection<IRecipe<IInventory>> recipes = world.getRecipeManager().getRecipes(IRecipeType.SMELTING).values();
                for(IRecipe<IInventory> recipe : recipes){
                    for(ItemStack match : recipe.getIngredients().get(0).getMatchingStacks()) {
                        if (match.isItemEqual(stack)) {
                            return super.insertItem(slot, stack, simulate);
                        }
                    }
                }

                return stack;
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        rotation.ifPresent(rotation -> {
            IRotationalAcceptor rot = getRotationFromWorld(rotation, displayRotation);
            temperature = 50 + 5 * getLog2(rot.getForce());
            float speed = 1 + 0.1f * rot.getSpeed();
            System.out.println(rot.getSpeed()+ " - "+rot.getForce());
            if (canSmelt() && (rot.getForce() > 0 && rot.getSpeed() > 0)) {
                markDirty();
                progress += speed;
                world.setBlockState(pos, getBlockState().with(BlockStateProperties.POWERED, true));
            }else{
                world.setBlockState(pos, getBlockState().with(BlockStateProperties.POWERED, false));
            }

            while (progress > MAX_PROGRESS) {
                completeSmelt();
                progress -= MAX_PROGRESS;
            }

            if(!world.isRemote) {
                displayRotation.insertEnergy(rotation.getSpeed(), rotation.getForce());
                rotation.setForce(0);
                rotation.setSpeed(0);
            }
        });
    }


    /**
     * @return Whether or not the current item is smeltable.
     */
    private boolean canSmelt(){
        AtomicBoolean canSmelt = new AtomicBoolean(false);
        autoInventoryHandler.ifPresent(inv -> {
            if(inv.getStackInSlot(0).isEmpty()) {
                //Reset stored recipe
                canSmelt.set(false);
                itemToProcess = ItemStack.EMPTY;
                itemToCreate = ItemStack.EMPTY;
            }
            else if(itemToProcess.isItemEqual(inv.getStackInSlot(0))){
                //If current item is already determined to be smeltable and there is room to output it
                ItemStack output = inv.getStackInSlot(1);
                canSmelt.set(output.isEmpty() ||
                        (itemToCreate.isItemEqual(output) && output.getCount() < output.getMaxStackSize()) );
            }else{
                //Update saved recipe
                Collection<IRecipe<IInventory>> recipes = world.getRecipeManager().getRecipes(IRecipeType.SMELTING).values();
                for(IRecipe<IInventory> recipe : recipes){
                    for(ItemStack match : recipe.getIngredients().get(0).getMatchingStacks()) {
                        if (match.isItemEqual(inv.getStackInSlot(0))) {
                            itemToProcess = inv.getStackInSlot(0);
                            itemToCreate = recipe.getRecipeOutput().copy();
                            canSmelt.set(true);
                            return;
                        }
                    }
                }
            }
        });
        return canSmelt.get();
    }

    private void completeSmelt(){
        markDirty();
        autoInventoryHandler.ifPresent(inv -> {
            if(inv.getStackInSlot(0).isItemEqual(itemToProcess)){
                if(inv.getStackInSlot(1).isEmpty())
                    ((MachineItemHandler) inv).setStackInSlot(1, itemToCreate.copy());
                else
                    inv.getStackInSlot(1).grow(itemToCreate.getCount());

                inv.getStackInSlot(0).shrink(1);
            }
        });
    }

    public int getLog2(long value){
        if(value <= 0)
            return -1;
        int pow = 0;
        while(value != 0){
            value >>= 1;
            pow++;
        }
        return pow-1;
    }

    public float getProgress(){
        return progress;
    }

    public float getProgressRatio(){
        return Math.min(1.0f, progress/MAX_PROGRESS);
    }

    @Override
    public void read(CompoundNBT compound) {
        System.out.println("FrictionFurnace hasRotation: "+hasRotation);
        progress = compound.getFloat("progress");
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putFloat("progress", progress);
        return super.write(compound);
    }

    @Override
    protected IRotationalAcceptor createRotationAcceptor() {
        return new RotationalAcceptor();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return autoInventoryHandler.cast();
        }
        else if(cap == RotationProvider.ROTATION_CAPABILITY){
            if(side == getBlockState().get(BlockStateProperties.HORIZONTAL_FACING)){
                return rotation.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new FrictionFurnaceContainer(i, world, pos, playerInventory, playerEntity);
    }
}
