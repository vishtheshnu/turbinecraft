package com.vnator.turbinecraft.setup;

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
    }
}
