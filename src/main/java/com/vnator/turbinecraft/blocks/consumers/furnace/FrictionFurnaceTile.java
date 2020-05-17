package com.vnator.turbinecraft.blocks.consumers.furnace;

import com.vnator.turbinecraft.blocks.ConsumerTileEntity;
import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalAcceptor;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class FrictionFurnaceTile extends ConsumerTileEntity implements ITickableTileEntity {

    private static final int MAX_PROGRESS = 200; //Progress to smelt a single item

    LazyOptional<IRotationalAcceptor> rotationAcceptor = LazyOptional.of(this::createRotationalAcceptor);
    LazyOptional<IItemHandler> inventory = LazyOptional.of(this::createInventory);

    ItemStack itemToProcess = ItemStack.EMPTY;
    ItemStack itemToCreate = ItemStack.EMPTY;

    private float progress;

    public FrictionFurnaceTile() {
        super(Registration.FRICTION_FURNACE_TILE);
    }

    @Override
    public void tick() {
        if(!world.isRemote) {
            rotationAcceptor.ifPresent(rotation -> {
                if (rotation.getForce() == 0 || rotation.getSpeed() == 0)
                    return;
                int temperature = 50 + 5 * getLog2(rotation.getForce());
                float speed = 1 + 0.1f * rotation.getSpeed();

                if (canSmelt())
                    progress += speed;

                while (progress > MAX_PROGRESS) {
                    completeSmelt();
                    progress -= MAX_PROGRESS;
                }

                rotation.setForce(0);
                rotation.setSpeed(0);

            });
        }
    }

    /**
     * @return Whether or not the current item is smeltable.
     */
    private boolean canSmelt(){
        AtomicBoolean canSmelt = new AtomicBoolean(false);
        inventory.ifPresent(inv -> {
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
        inventory.ifPresent(inv -> {
            if(inv.getStackInSlot(0).isItemEqual(itemToProcess)){
                if(inv.getStackInSlot(1).isEmpty())
                    ((ItemStackHandler) inv).setStackInSlot(1, itemToCreate.copy());
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

    @Override
    protected IRotationalAcceptor createRotationalAcceptor() {
        return new RotationalAcceptor();
    }

    private IItemHandler createInventory(){
        return new ItemStackHandler(2){
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

                AtomicBoolean isValid = new AtomicBoolean(false);
                inventory.ifPresent(inv -> {
                    Collection<IRecipe<IInventory>> recipes = world.getRecipeManager().getRecipes(IRecipeType.SMELTING).values();
                    for(IRecipe<IInventory> recipe : recipes){
                        for(ItemStack match : recipe.getIngredients().get(0).getMatchingStacks()) {
                            if (match.isItemEqual(stack)) {
                                isValid.set(true);
                                return;
                            }
                        }
                    }
                });

                if(!isValid.get())
                    return stack;
                else {
                    return super.insertItem(slot, stack, simulate);
                }
            }

            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if(slot == 0)
                    return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    @Override
    public void read(CompoundNBT compound) {
        CompoundNBT invTag = compound.getCompound("inventory");
        inventory.ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));

        CompoundNBT rotTag = compound.getCompound("rotation");
        rotationAcceptor.ifPresent(rot -> ((INBTSerializable<CompoundNBT>) rot).deserializeNBT(rotTag));
        progress = compound.getFloat("progress");
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        inventory.ifPresent(inv -> {
            CompoundNBT nbt = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            compound.put("inventory", nbt);
        });

        inventory.ifPresent(inv -> {
            CompoundNBT nbt = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            compound.put("rotation", nbt);
        });

        compound.putFloat("progress", progress);
        return super.write(compound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventory.cast();
        }
        else if(cap == RotationProvider.ROTATION_CAPABILITY){
            if(side == getBlockState().get(BlockStateProperties.FACING)){
                return rotationAcceptor.cast();
            }
        }
        return super.getCapability(cap, side);
    }
}
