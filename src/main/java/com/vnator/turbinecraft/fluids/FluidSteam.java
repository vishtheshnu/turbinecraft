package com.vnator.turbinecraft.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class FluidSteam {

    public static final FluidAttributes.Builder ATTR = FluidAttributes.builder(
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_still"),
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_flow")
    ).color(0xF0F0F0F0).gaseous();

    public static final ForgeFlowingFluid.Properties PROPS = new ForgeFlowingFluid.Properties
            (FluidRegistration.steam_fluid, FluidRegistration.steam_fluid_flowing, ATTR)
            .bucket(FluidRegistration.steam_fluid_bucket).block(FluidRegistration.steam_fluid_block);
}
