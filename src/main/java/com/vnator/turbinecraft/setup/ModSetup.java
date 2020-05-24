package com.vnator.turbinecraft.setup;

import com.vnator.turbinecraft.blocks.consumers.dynamometer.DynamometerBlockRenderer;
import com.vnator.turbinecraft.blocks.generators.t1_furnace_generator.FurnaceGeneratorRenderer;
import com.vnator.turbinecraft.blocks.generators.t1_steam_generator.SteamGeneratorRenderer;
import com.vnator.turbinecraft.blocks.transfer.bevel.BevelRenderer;
import com.vnator.turbinecraft.blocks.transfer.shaft.ShaftRenderer;
import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalAcceptorStorage;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalMerger;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ModSetup {

    public static ItemGroup itemGroup = new ItemGroup("turbinecraft") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Registration.FIRSTBLOCK);
        }
    };

    public void init(){
        CapabilityManager.INSTANCE.register(IRotationalAcceptor.class, new RotationalAcceptorStorage(), RotationalAcceptor::new);

        //Register Renderers
        FurnaceGeneratorRenderer.register();
        SteamGeneratorRenderer.register();

        DynamometerBlockRenderer.register();

        ShaftRenderer.register();
        BevelRenderer.register();
    }
}
