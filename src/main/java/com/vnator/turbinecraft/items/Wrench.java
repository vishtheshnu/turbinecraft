package com.vnator.turbinecraft.items;

import com.vnator.turbinecraft.setup.ModSetup;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;

public class Wrench extends Item {
    public Wrench() {
        super(new Item.Properties()
                .maxStackSize(1)
                .group(ModSetup.itemGroup)
                .addToolType(ToolType.get("wrench"), 1)
        );
        setRegistryName("wrench");
    }


}
