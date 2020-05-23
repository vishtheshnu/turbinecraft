package com.vnator.turbinecraft.blocks.transfer.shaft;

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

public class ShaftBlockTile extends ConsumerTileEntity implements ITickableTileEntity {

    public ShaftBlockTile() {
        super(Registration.SHAFT_BLOCK_TILE);
    }

    @Override
    protected IRotationalAcceptor createRotationalAcceptor() {
        return new RotationalAcceptor();
    }

    @Override
    public void tick() {
        TileEntity ent = world.getTileEntity(pos.offset(getBlockState().get(BlockStateProperties.FACING).getOpposite()));
        if(ent != null)
            ent.getCapability(RotationProvider.ROTATION_CAPABILITY, getBlockState().get(BlockStateProperties.FACING)).ifPresent(rot -> {
                rotationAcceptor.ifPresent(myrot -> {
                    rot.setSpeed(myrot.getSpeed());
                    rot.setForce(myrot.getForce());
                });
            });

        rotationAcceptor.ifPresent(rot -> {
            rot.setSpeed(0);
            rot.setForce(0);
        });
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == RotationProvider.ROTATION_CAPABILITY){
            if(side == null || (side == getBlockState().get(BlockStateProperties.FACING)))
                return rotationAcceptor.cast();
            else
                return super.getCapability(cap, side);
        }
        return super.getCapability(cap, side);
    }
}
