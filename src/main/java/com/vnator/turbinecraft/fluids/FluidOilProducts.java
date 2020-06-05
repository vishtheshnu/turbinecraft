package com.vnator.turbinecraft.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class FluidOilProducts {
    //Crude Oil
    public static final FluidAttributes.Builder OIL_ATTR = FluidAttributes.builder(
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_still"),
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_flow")
    ).color(0x050F0F0F).viscosity(10);

    public static final ForgeFlowingFluid.Properties OIL_PROPS = new ForgeFlowingFluid.Properties
            (FluidRegistration.oil_fluid, FluidRegistration.oil_fluid_flowing, OIL_ATTR)
            .bucket(FluidRegistration.oil_fluid_bucket).block(FluidRegistration.oil_fluid_block);


    //Gasoline
    public static final FluidAttributes.Builder GASOLINE_ATTR = FluidAttributes.builder(
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_still"),
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_flow")
    ).color(0x0ff2e3bb).viscosity(1);

    public static final ForgeFlowingFluid.Properties GASOLINE_PROPS = new ForgeFlowingFluid.Properties
            (FluidRegistration.gasoline_fluid, FluidRegistration.gasoline_fluid_flowing, GASOLINE_ATTR)
            .bucket(FluidRegistration.gasoline_fluid_bucket).block(FluidRegistration.gasoline_fluid_block);


    //Diesel
    public static final FluidAttributes.Builder DIESEL_ATTR = FluidAttributes.builder(
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_still"),
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_flow")
    ).color(0x0ff2d37e);

    public static final ForgeFlowingFluid.Properties DIESEL_PROPS = new ForgeFlowingFluid.Properties
            (FluidRegistration.diesel_fluid, FluidRegistration.diesel_fluid_flowing, DIESEL_ATTR)
            .bucket(FluidRegistration.diesel_fluid_bucket).block(FluidRegistration.diesel_fluid_block);


    //Naphtha
    public static final FluidAttributes.Builder NAPHTHA_ATTR = FluidAttributes.builder(
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_still"),
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_flow")
    ).color(0x80fff83b);

    public static final ForgeFlowingFluid.Properties NAPHTHA_PROPS = new ForgeFlowingFluid.Properties
            (FluidRegistration.naphtha_fluid, FluidRegistration.naphtha_fluid_flowing, NAPHTHA_ATTR)
            .bucket(FluidRegistration.naphtha_fluid_bucket).block(FluidRegistration.naphtha_fluid_block);


    //E50
    public static final FluidAttributes.Builder E50_ATTR = FluidAttributes.builder(
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_still"),
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_flow")
    ).color(0x80c3f748);

    public static final ForgeFlowingFluid.Properties E50_PROPS = new ForgeFlowingFluid.Properties
            (FluidRegistration.e50_fluid, FluidRegistration.e50_fluid_flowing, E50_ATTR)
            .bucket(FluidRegistration.e50_fluid_bucket).block(FluidRegistration.e50_fluid_block);
}
