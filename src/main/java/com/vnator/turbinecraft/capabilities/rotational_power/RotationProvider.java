package com.vnator.turbinecraft.capabilities.rotational_power;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RotationProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IRotationalAcceptor.class)
    public static final Capability<IRotationalAcceptor> ROTATION_CAPABILITY = null;

    private final LazyOptional<IRotationalAcceptor> instance = LazyOptional.of(RotationalAcceptor::new);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ROTATION_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        NonNullSupplier<IRotationalAcceptor> supplier = new NonNullSupplier<IRotationalAcceptor>() {
            @Nonnull
            @Override
            public IRotationalAcceptor get() {
                return null;
            }
        };
        return ROTATION_CAPABILITY.getStorage().writeNBT(ROTATION_CAPABILITY, instance.orElseGet(supplier), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        NonNullSupplier<IRotationalAcceptor> supplier = new NonNullSupplier<IRotationalAcceptor>() {
            @Nonnull
            @Override
            public IRotationalAcceptor get() {
                return null;
            }
        };
        ROTATION_CAPABILITY.getStorage().readNBT(ROTATION_CAPABILITY, instance.orElseGet(supplier), null, nbt);
    }
}
