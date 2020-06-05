package com.vnator.turbinecraft.blocks.misc.water_source;

import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InfiniteWaterTile extends TileEntity {

    private static final int FILL_TIME = 20;
    LazyOptional<IFluidHandler> tank;
    int tickTime = 0;

    public InfiniteWaterTile() {
        super(Registration.INF_WATER_TILE);
        tank = LazyOptional.of(() -> new FluidTank(1000){
            @Override
            public int fill(FluidStack resource, FluidAction action) {
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                return false;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                return new FluidStack(Fluids.WATER, 1000);
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                return new FluidStack(Fluids.WATER, Math.min(maxDrain, 1000));
            }
        });
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return tank.cast();
        return super.getCapability(cap, side);
    }
}
