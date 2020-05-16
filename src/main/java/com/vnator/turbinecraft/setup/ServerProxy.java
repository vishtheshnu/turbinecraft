package com.vnator.turbinecraft.setup;

import net.minecraft.world.World;

public class ServerProxy implements IProxy{

    @Override
    public void init(){

    }

    @Override
    public World getClientWorld() {
        return null;
    }
}
