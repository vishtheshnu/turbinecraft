package com.vnator.turbinecraft.blocks.consumers.dynamometer;

import com.vnator.turbinecraft.blocks.ConsumerTileEntity;
import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalAcceptor;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;

public class DynamometerBlockTile extends TileEntity implements ITickableTileEntity {

    protected LazyOptional<IRotationalAcceptor> rotationAcceptor = LazyOptional.of(this::createRotationalAcceptor);

    public DynamometerBlockTile() {
        super(Registration.DYNAMOMETER_BLOCK_TILE);
    }

    protected IRotationalAcceptor createRotationalAcceptor() {
        return new RotationalAcceptor();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == RotationProvider.ROTATION_CAPABILITY){
            if(side == null || (side == getBlockState().get(BlockStateProperties.HORIZONTAL_FACING)))
                return rotationAcceptor.cast();
            else
                return super.getCapability(cap, side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void tick() {
        if(world.isRemote)
            return;
    }
}
