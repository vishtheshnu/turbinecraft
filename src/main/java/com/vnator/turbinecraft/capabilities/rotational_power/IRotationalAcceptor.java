package com.vnator.turbinecraft.capabilities.rotational_power;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IRotationalAcceptor extends INBTSerializable<CompoundNBT> {

    void insertEnergy(long speed, long force);

    long getSpeed();
    long getForce();

    void setSpeed(long speed);
    void setForce(long force);

    default CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("speed", getSpeed());
        nbt.putLong("force", getForce());
        return nbt;
    }

    default void deserializeNBT(CompoundNBT nbt) {
        setSpeed(nbt.getLong("speed"));
        setForce(nbt.getLong("force"));
    }
}
