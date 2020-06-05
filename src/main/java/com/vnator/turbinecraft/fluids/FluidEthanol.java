package com.vnator.turbinecraft.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class FluidEthanol {
    public static final FluidAttributes.Builder ATTR = FluidAttributes.builder(
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_still"),
            new ResourceLocation("turbinecraft:block/fluid/generic_fluid_flow")
    ).color(0x804ddeff).viscosity(2);

    public static final ForgeFlowingFluid.Properties PROPS = new ForgeFlowingFluid.Properties
            (FluidRegistration.ethanol_fluid, FluidRegistration.ethanol_fluid_flowing, ATTR)
            .bucket(FluidRegistration.ethanol_fluid_bucket).block(FluidRegistration.ethanol_fluid_block);
}
