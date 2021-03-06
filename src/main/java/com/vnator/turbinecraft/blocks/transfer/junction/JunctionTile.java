package com.vnator.turbinecraft.blocks.transfer.junction;

import com.vnator.turbinecraft.blocks.MachineTileEntity;
import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationalMerger;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

//POWERED = Combine power, UNPOWERED = Split power
public class JunctionTile extends MachineTileEntity {
    public JunctionTile() {
        super(Registration.JUNCTION_TILE);
    }

    @Override
    public void tick() {
        if(world.isRemote)
            return;
        if(getBlockState().get(BlockStateProperties.POWERED)){
            rotation.ifPresent(myrot -> {
                displayRotation.insertEnergy(myrot.getSpeed(), myrot.getForce());
                Direction facing = getBlockState().get(BlockStateProperties.FACING);
                TileEntity ent = world.getTileEntity(pos.offset(facing));
                if(ent != null)
                    ent.getCapability(RotationProvider.ROTATION_CAPABILITY, facing.getOpposite()).ifPresent(rot -> {
                        rot.insertEnergy(myrot.getSpeed(), myrot.getForce());
                    });
                myrot.setSpeed(0);
                myrot.setForce(0);
            });
        }else{
            AtomicInteger numValidSides = new AtomicInteger(0);
            for(int i = 0; i < 6; i++){
                Direction dir = Direction.byIndex(i);
                if(dir != getBlockState().get(BlockStateProperties.FACING)){
                    TileEntity ent = world.getTileEntity(pos.offset(dir));
                    if(ent != null)
                        ent.getCapability(RotationProvider.ROTATION_CAPABILITY, dir.getOpposite()).ifPresent(rot -> numValidSides.incrementAndGet());
                }
            }

            if(numValidSides.get() > 0) {
                rotation.ifPresent(myrot -> {
                    long speed = myrot.getSpeed();
                    long force = myrot.getForce() / numValidSides.get();
                    for (int i = 0; i < 6; i++) {
                        Direction dir = Direction.byIndex(i);
                        if (dir != getBlockState().get(BlockStateProperties.FACING)) {
                            TileEntity ent = world.getTileEntity(pos.offset(dir));
                            if (ent != null)
                                ent.getCapability(RotationProvider.ROTATION_CAPABILITY, dir.getOpposite()).ifPresent(rot -> {
                                    rot.insertEnergy(speed, force);
                                });
                        }
                    }
                    myrot.setSpeed(0);
                    myrot.setForce(0);
                });
            }
        }
    }

    public void onWrench(Direction dir, PlayerEntity player){
        if(player.isSneaking()){
            setBlockState(BlockStateProperties.POWERED, !getBlockState().get(BlockStateProperties.POWERED));
            player.sendStatusMessage(new StringTextComponent(I18n.format("junction_powermode_"
                    +getBlockState().get(BlockStateProperties.POWERED))), true);
        }else{
            int bsIndex = getBlockState().get(BlockStateProperties.FACING).getIndex();
            setBlockState(BlockStateProperties.FACING, Direction.byIndex(bsIndex+1));
        }
    }

    @Override
    protected IRotationalAcceptor createRotationAcceptor() {
        return new RotationalMerger();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == RotationProvider.ROTATION_CAPABILITY){
            if(getBlockState().get(BlockStateProperties.POWERED)){
                if(side != getBlockState().get(BlockStateProperties.FACING))
                    return rotation.cast();
            }else{
                if(side == getBlockState().get(BlockStateProperties.FACING))
                    return rotation.cast();
            }
        }

        return super.getCapability(cap, side);
    }
}
