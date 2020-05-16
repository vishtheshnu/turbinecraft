package com.vnator.turbinecraft.capabilities.rotational_power;

public interface IRotationalAcceptor {

    void insertEnergy(long speed, long force);

    long getSpeed();
    long getForce();

    void setSpeed(long speed);
    void setForce(long force);
}
