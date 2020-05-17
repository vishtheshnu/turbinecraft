package com.vnator.turbinecraft.capabilities.rotational_power;

import net.minecraft.nbt.CompoundNBT;

public class RotationalAcceptor implements IRotationalAcceptor {

    private long speed;
    private long force;

    @Override
    public void insertEnergy(long speed, long force) {
        this.speed = speed;
        this.force = force;
    }

    @Override
    public long getSpeed() {
        return speed;
    }

    @Override
    public long getForce() {
        return force;
    }

    @Override
    public void setSpeed(long speed) {
        this.speed = speed;
    }

    @Override
    public void setForce(long force) {
        this.force = force;
    }

    @Override
    public String toString(){
        return "Speed: "+speed+", Force: "+force;
    }
}
