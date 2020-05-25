package com.vnator.turbinecraft.blocks.transfer.bevel;

import com.vnator.turbinecraft.blocks.GeneratorTileEntity;
import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalAcceptor;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BevelTile extends GeneratorTileEntity {

    public Direction otherFacing = Direction.NORTH;
    public boolean isPoweredOnFacing = false;
    private boolean isAccessedFacing = false;

    public BevelTile() {
        super(Registration.BEVEL_TILE);
    }

    @Override
    public void tick() {
        if(world.isRemote)
            return;

        rotation.ifPresent(rot -> {
            //Check if is powered
            if(rot.getSpeed() > 0 || rot.getForce() > 0)
                setBlockState(BlockStateProperties.POWERED, true);
            else
                setBlockState(BlockStateProperties.POWERED, false);

            //Don't transfer 0,0 power
            if(rot.getSpeed() <= 0 || rot.getForce() <= 0) {
                rot.setSpeed(0);
                rot.setForce(0);
                return;
            }

            //Transfer rotation out
            if(getBlockState().get(BlockStateProperties.FACING) != otherFacing) {
                Direction facing = getBlockState().get(BlockStateProperties.FACING);

                if (!isPoweredOnFacing && world.getTileEntity(pos.offset(facing)) != null)
                    world.getTileEntity(pos.offset(facing)).getCapability(RotationProvider.ROTATION_CAPABILITY, facing.getOpposite()).ifPresent(orot -> {
                        orot.insertEnergy(rot.getSpeed(), rot.getForce());
                    });

                if (isPoweredOnFacing && world.getTileEntity(pos.offset(otherFacing)) != null)
                    world.getTileEntity(pos.offset(otherFacing)).getCapability(RotationProvider.ROTATION_CAPABILITY, otherFacing.getOpposite()).ifPresent(orot -> {
                        orot.insertEnergy(rot.getSpeed(), rot.getForce());
                    });
            }
            rot.setSpeed(0);
            rot.setForce(0);
        });
    }

    public void setFace(Direction setDir, boolean isSneaking){
        Direction facing = isSneaking ? otherFacing : getBlockState().get(BlockStateProperties.FACING);
        Direction other = isSneaking ? getBlockState().get(BlockStateProperties.FACING) : otherFacing;
        if(setDir == facing && facing.getOpposite() != other){
            if(isSneaking){
                otherFacing = otherFacing.getOpposite();
                updateClient();
            }else
                setBlockState(BlockStateProperties.FACING, facing.getOpposite());
        }else if(setDir != other){
            if(isSneaking){
                otherFacing = setDir;
                updateClient();
            }else
                setBlockState(BlockStateProperties.FACING, setDir);
        }

    }

    @Override
    public void read(CompoundNBT compound) {
        otherFacing = Direction.byIndex(compound.getInt("otherFacing"));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("otherFacing", otherFacing.getIndex());
        return super.write(compound);
    }

    @Override
    protected CompoundNBT getMinimalUpdateNbt() {
        CompoundNBT nbt = new CompoundNBT();
        rotation.ifPresent(rot -> nbt.put("rotation", rot.serializeNBT()));
        nbt.putInt("otherFacing", otherFacing.getIndex());
        return nbt;
    }

    @Override
    protected void setMinimalUpdateNbt(CompoundNBT nbt) {
        rotation.ifPresent(rot -> rot.deserializeNBT(nbt.getCompound("rotation")));
        otherFacing = Direction.byIndex(nbt.getInt("otherFacing"));
    }

    @Override
    protected IRotationalAcceptor createRotationAcceptor() {
        return new RotationalAcceptor(){
            @Override
            public void insertEnergy(long speed, long force) {
                if(speed > 0 && force > 0)
                    isPoweredOnFacing = isAccessedFacing;
                super.insertEnergy(speed, force);
            }

            @Override
            public void setSpeed(long speed) {
                if(speed > 0)
                    isPoweredOnFacing = isAccessedFacing;
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == RotationProvider.ROTATION_CAPABILITY){
            if(side == otherFacing){
                isAccessedFacing = false;
                return rotation.cast();
            }else if(side == getBlockState().get(BlockStateProperties.FACING)){
                isAccessedFacing = true;
                return rotation.cast();
            }

        }
        return super.getCapability(cap, side);
    }
}
