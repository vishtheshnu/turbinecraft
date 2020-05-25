package com.vnator.turbinecraft.blocks.transfer.shaft;

import com.vnator.turbinecraft.blocks.ConsumerTileEntity;
import com.vnator.turbinecraft.blocks.GeneratorTileEntity;
import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalAcceptor;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShaftBlockTile extends GeneratorTileEntity {

    public boolean isPoweredOnFacing = false;
    private boolean isAccessedFacing = false;

    public ShaftBlockTile() {
        super(Registration.SHAFT_BLOCK_TILE);
    }

    @Override
    public void tick() {
        super.tick();
        if(world.isRemote)
            return;
        TileEntity ent = world.getTileEntity(pos.offset(getBlockState().get(BlockStateProperties.FACING).getOpposite()));
        TileEntity ent2 = world.getTileEntity(pos.offset(getBlockState().get(BlockStateProperties.FACING)));
        if(ent != null && isPoweredOnFacing)
            ent.getCapability(RotationProvider.ROTATION_CAPABILITY, getBlockState().get(BlockStateProperties.FACING)).ifPresent(rot -> {
                rotation.ifPresent(myrot -> {
                    if(myrot.getSpeed() <= 0 || myrot.getForce() <= 0)
                        return;
                    rot.insertEnergy(myrot.getSpeed(), myrot.getForce());
                });
            });
        else if(ent2 != null && !isPoweredOnFacing)
            ent2.getCapability(RotationProvider.ROTATION_CAPABILITY, getBlockState().get(BlockStateProperties.FACING).getOpposite()).ifPresent(rot -> {
                rotation.ifPresent(myrot -> {
                    if(myrot.getSpeed() <= 0 || myrot.getForce() <= 0)
                        return;
                    rot.insertEnergy(myrot.getSpeed(), myrot.getForce());
                });
            });


        rotation.ifPresent(rot -> {
            if(rot.getSpeed() > 0 || rot.getForce() > 0)
                setBlockState(BlockStateProperties.POWERED, true);
            else
                setBlockState(BlockStateProperties.POWERED, false);
            rot.setSpeed(0);
            rot.setForce(0);
        });
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == RotationProvider.ROTATION_CAPABILITY){
            if(side == null || (side == getBlockState().get(BlockStateProperties.FACING))) {
                isAccessedFacing = true;
                return rotation.cast();
            }else if(side == getBlockState().get(BlockStateProperties.FACING).getOpposite()) {
                isAccessedFacing = false;
                return rotation.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected CompoundNBT getMinimalUpdateNbt() {
        CompoundNBT nbt = new CompoundNBT();
        rotation.ifPresent(rot -> nbt.put("rotation", rot.serializeNBT()));
        nbt.putBoolean("powerOnFacing", isPoweredOnFacing);
        return nbt;
    }

    @Override
    protected void setMinimalUpdateNbt(CompoundNBT nbt) {
        rotation.ifPresent(rot -> rot.deserializeNBT(nbt.getCompound("rotation")));
        isPoweredOnFacing = nbt.getBoolean("powerOnFacing");
    }

    @Override
    protected IRotationalAcceptor createRotationAcceptor() {
        return new RotationalAcceptor(){

            @Override
            public void insertEnergy(long speed, long force) {
                if((speed > 0 || force > 0))
                    isPoweredOnFacing = isAccessedFacing;
                super.insertEnergy(speed, force);
            }

            @Override
            public void setSpeed(long speed) {
                if(speed > 0){
                    isPoweredOnFacing = isAccessedFacing;
                }
                super.setSpeed(speed);
            }

            @Override
            public void setForce(long force) {
                if(force > 0)
                    isPoweredOnFacing = isAccessedFacing;
                super.setForce(force);
            }
        };
    }
}
