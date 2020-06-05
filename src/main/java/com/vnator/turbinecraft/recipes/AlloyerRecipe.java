package com.vnator.turbinecraft.recipes;

import com.google.gson.JsonObject;
import com.vnator.turbinecraft.TurbineCraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class AlloyerRecipe implements IRecipe<IInventory> {

    public static final IRecipeType<AlloyerRecipe> ALLOYER = IRecipeType.register(TurbineCraft.MOD_ID+":alloyer");

    public final ResourceLocation id;
    public final Ingredient input1, input2;
    public final int input1Count, input2Count;
    public final int minForceTier;
    public final ItemStack output;
    public final int time;

    public AlloyerRecipe(ResourceLocation id, Ingredient input1, Ingredient input2, ItemStack output, int minForceTier, int time){
        this.id = id;
        this.input1 = input1;
        this.input2 = input2;
        this.minForceTier = minForceTier;
        this.output = output;
        this.time = time;
        input1Count = input1 instanceof NBTIngredient ? ((NBTIngredient) input1).getMatchingStacks()[0].getCount() : 1;
        input2Count = input2 instanceof NBTIngredient ? ((NBTIngredient) input2).getMatchingStacks()[0].getCount() : 1;
    }


    @Override
    public boolean matches(IInventory inv, World worldIn) {
        boolean i1Match = input1.test(inv.getStackInSlot(0));
        boolean i2Match = input2.test(inv.getStackInSlot(1));
        if(input1 instanceof NBTIngredient){
            i1Match = i1Match && inv.getStackInSlot(0).getCount() >= ((NBTIngredient) input1).getMatchingStacks()[0].getCount();
        }
        if(input2 instanceof NBTIngredient){
            i2Match = i2Match && inv.getStackInSlot(1).getCount() >= ((NBTIngredient) input2).getMatchingStacks()[0].getCount();
        }
        return i1Match & i2Match;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return output;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return TurbineCraft.Recipes.alloyer;
    }

    @Override
    public IRecipeType<?> getType() {
        return ALLOYER;
    }

    public static class Serializer<T extends AlloyerRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>{

        final IRecipeFactory<T> factory;

        public Serializer(IRecipeFactory<T> factory){
            this.factory = factory;
        }

        @Override
        public T read(ResourceLocation recipeId, JsonObject json) {
            Ingredient i1, i2;
            i1 = Ingredient.deserialize(json.getAsJsonObject("ingredient1"));
            i2 = Ingredient.deserialize(json.getAsJsonObject("ingredient2"));

            ItemStack output;
            String itemName = json.get("output").getAsString();
            int count = JSONUtils.getInt(json, "count", 1);
            output = new ItemStack(Registry.ITEM.getOrDefault(new ResourceLocation(itemName)), count);

            int minForce, time;
            minForce = JSONUtils.getInt(json, "minForceTier", 1);
            time = JSONUtils.getInt(json, "time", 1);

            return factory.create(recipeId, i1, i2, output, minForce, time);
        }

        @Nullable
        @Override
        public T read(ResourceLocation recipeId, PacketBuffer buffer) {
            Ingredient i1, i2;
            i1 = NBTIngredient.read(buffer);
            i2 = NBTIngredient.read(buffer);

            ItemStack output = buffer.readItemStack();

            int minForce = buffer.readInt();
            int time = buffer.readInt();

            return factory.create(recipeId, i1, i2, output, minForce, time);
        }

        @Override
        public void write(PacketBuffer buffer, T recipe) {
            recipe.input1.write(buffer);
            recipe.input2.write(buffer);
            buffer.writeItemStack(recipe.output);
            buffer.writeInt(recipe.minForceTier);
            buffer.writeInt(recipe.time);
        }

        public interface IRecipeFactory<T extends AlloyerRecipe> {
            T create(ResourceLocation id, Ingredient input1, Ingredient input2, ItemStack output, int minForceTier, int time);
        }
    }
}
