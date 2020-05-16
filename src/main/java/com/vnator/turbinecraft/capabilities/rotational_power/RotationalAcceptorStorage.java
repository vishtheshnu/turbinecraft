package com.vnator.turbinecraft.capabilities.rotational_power;

import net.minecraft.command.arguments.NBTTagArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

import javax.annotation.Nullable;

public class RotationalAcceptorStorage implements IStorage<IRotationalAcceptor> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<IRotationalAcceptor> capability, IRotationalAcceptor instance, Direction side) {
        CompoundNBT data = new CompoundNBT();
        data.putLong("speed", instance.getSpeed());
        data.putLong("force", instance.getForce());
        return data;
    }

    @Override
    public void readNBT(Capability<IRotationalAcceptor> capability, IRotationalAcceptor instance, Direction side, INBT nbt) {
        if(nbt instanceof CompoundNBT){
            CompoundNBT cnbt = (CompoundNBT) nbt;
            instance.setForce(cnbt.getLong("force"));
            instance.setSpeed(cnbt.getLong("speed"));

        }
    }
}
