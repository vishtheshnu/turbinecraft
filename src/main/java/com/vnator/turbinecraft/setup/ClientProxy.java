package com.vnator.turbinecraft.setup;

import com.vnator.turbinecraft.blocks.generators.t1_steam_generator.SteamGeneratorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy{

    @Override
    public void init(){
        ScreenManager.registerFactory(Registration.BASIC_STEAM_GENERATOR_CONTAINER, SteamGeneratorScreen::new);
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
