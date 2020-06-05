package com.vnator.turbinecraft.recipes;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class IngredientWithCount extends Ingredient {

    private int count;
    private CompoundNBT tag;
    private Ingredient baseIngredient;

    public IngredientWithCount(Stream<? extends IItemList> itemLists) {
        super(itemLists);
        count = 1;
        tag = null;
    }

    public IngredientWithCount(Stream<? extends IItemList> itemLists, int count, @Nullable CompoundNBT tag){
        super(itemLists);
        this.count = count;
        this.tag = tag;
    }

}
