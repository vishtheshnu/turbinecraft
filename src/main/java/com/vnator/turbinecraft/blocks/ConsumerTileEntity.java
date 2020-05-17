package com.vnator.turbinecraft.blocks;

import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ConsumerTileEntity extends TileEntity {

    protected LazyOptional<IRotationalAcceptor> rotationAcceptor = LazyOptional.of(this::createRotationalAcceptor);

    public ConsumerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    protected abstract IRotationalAcceptor createRotationalAcceptor();

}
