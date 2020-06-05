package com.vnator.turbinecraft.blocks.consumers.alloyer;

import com.vnator.turbinecraft.blocks.MachineTileEntity;
import com.vnator.turbinecraft.blocks.consumers.furnace.FrictionFurnaceContainer;
import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalAcceptor;
import com.vnator.turbinecraft.recipes.AlloyerRecipe;
import com.vnator.turbinecraft.setup.Registration;
import com.vnator.turbinecraft.util.MachineItemHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AlloyerTile extends MachineTileEntity implements INamedContainerProvider {

    private static final int TIME_REDUCTION_PER_SPEED_TIER = 10;

    public AlloyerRecipe recipe = null;
    public int time, recipeTime = 1;
    public ItemStackHandler inputHandler;

    public AlloyerTile() {
        super(Registration.ALLOYER_TILE);
        inventoryBase = NonNullList.withSize(3, ItemStack.EMPTY);
        inputHandler = new ItemStackHandler(inventoryBase);
        initInventory(new MachineItemHandler(this){

        },new MachineItemHandler(this){

        });
    }

    @Override
    public void tick() {
        super.tick();
        rotation.ifPresent(rot -> {
            boolean isPowered = false;
            if(checkRecipe()){
                rot = getRotationFromWorld(rot, displayRotation);
                if(getTier(rot.getForce()) >= recipe.minForceTier){
                    markDirty();
                    updateClient();
                    isPowered = true;
                    time++;
                    recipeTime = Math.max(1, recipe.time - getTier(rot.getSpeed())*TIME_REDUCTION_PER_SPEED_TIER);
                    while(time >= recipeTime){
                        completeCraftOperation();
                        time -= recipeTime;
                    }
                }
            }
            setBlockState(BlockStateProperties.POWERED, isPowered);

            if(!world.isRemote){
                displayRotation.insertEnergy(rot.getSpeed(), rot.getForce());
                rot.setSpeed(0);
                rot.setForce(0);
            }
        });
    }

    public boolean checkRecipe(){
        if (recipe == null || !recipe.matches(new RecipeWrapper((IItemHandlerModifiable) guiInventoryHandler.orElse(null)), world)) {
            AlloyerRecipe rec = world.getRecipeManager().getRecipe(AlloyerRecipe.ALLOYER, new RecipeWrapper(inputHandler), world).orElse(null);
            if (rec == null) {
                return false;
            }
            recipe = rec;
        }
        return isOutputSpace();
    }

    public boolean isOutputSpace(){
        return inventoryBase.get(2).isItemEqual(recipe.output) ||
                inventoryBase.get(2).isEmpty();
    }

    public void completeCraftOperation(){
        if(inventoryBase.get(2).isEmpty()){
            inventoryBase.set(2, recipe.output.copy());
        }else
            inventoryBase.get(2).grow(recipe.output.getCount());
        inventoryBase.get(0).shrink(recipe.input1Count);
        inventoryBase.get(1).shrink(recipe.input2Count);
    }

    @Override
    protected IRotationalAcceptor createRotationAcceptor() {
        return new RotationalAcceptor();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == RotationProvider.ROTATION_CAPABILITY && side == getBlockState().get(BlockStateProperties.HORIZONTAL_FACING))
            return rotation.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT compound) {
        time = compound.getInt("time");
        recipeTime = compound.getInt("recipeTime");
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("time", time);
        compound.putInt("recipeTime", recipeTime);
        return super.write(compound);
    }

    public float getProgress(){
        return Math.min(1, 1.0f*time/Math.max(1, recipeTime));
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new AlloyerContainer(i, world, pos, playerInventory, playerEntity);
    }
}
