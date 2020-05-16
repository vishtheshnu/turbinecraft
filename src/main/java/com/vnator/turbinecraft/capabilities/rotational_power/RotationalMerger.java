package com.vnator.turbinecraft.capabilities.rotational_power;

public class RotationalMerger implements IRotationalAcceptor{

    private long speed;
    private long force;

    @Override
    public void insertEnergy(long speed, long force) {
        if(this.speed == 0 && this.force == 0){
            this.speed = speed;
            this.force = force;
        }
        //Same or average speed, with sum of force
        else{
            if(this.speed != speed){
                this.speed = (this.speed + speed) / 2;
            }

            this.force += force;
        }
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
}
