package com.vnator.turbinecraft.items;

import com.vnator.turbinecraft.setup.ModSetup;
import net.minecraft.item.Item;

public class FirstItem extends Item {

    public FirstItem(){
        super(new Item.Properties()
                .maxStackSize(16)
                .group(ModSetup.itemGroup));
        setRegistryName("firstitem");
    }
}
