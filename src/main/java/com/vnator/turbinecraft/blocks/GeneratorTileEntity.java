package com.vnator.turbinecraft.blocks;

import com.vnator.turbinecraft.capabilities.rotational_power.IRotationalAcceptor;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class GeneratorTileEntity extends TileEntity {

    public GeneratorTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    /**
     * Transfer speed and force to the Rotation capability that this TileEntity is facing, if any.
     * @param speed Speed of rotational energy
     * @param force Force of rotational energy
     */
    public void transferRotation(long speed, long force){
        if(!world.isRemote){
            Direction facing = getBlockState().get(BlockStateProperties.FACING);

            TileEntity ent = world.getTileEntity(pos.offset(facing));
            if(ent != null){
                ent.getCapability(RotationProvider.ROTATION_CAPABILITY, facing.getOpposite()).ifPresent(cap -> cap.insertEnergy(speed, force));
            }
        }
    }
}
