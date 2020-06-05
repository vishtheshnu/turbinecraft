package com.vnator.turbinecraft.setup;

import com.vnator.turbinecraft.blocks.consumers.alloyer.AlloyerScreen;
import com.vnator.turbinecraft.blocks.consumers.furnace.FrictionFurnaceScreen;
import com.vnator.turbinecraft.blocks.consumers.solid_fuel_boiler.SolidBoilerScreen;
import com.vnator.turbinecraft.blocks.generators.t1_furnace_generator.FurnaceGeneratorScreen;
import com.vnator.turbinecraft.blocks.generators.t1_steam_generator.SteamGeneratorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy{

    @Override
    public void init(){
        ScreenManager.registerFactory(Registration.BASIC_STEAM_GENERATOR_CONTAINER, SteamGeneratorScreen::new);
        ScreenManager.registerFactory(Registration.BASIC_FURNACE_GENERATOR_CONTAINER, FurnaceGeneratorScreen::new);
        ScreenManager.registerFactory(Registration.FRICTION_FURNACE_CONTAINER, FrictionFurnaceScreen::new);
        ScreenManager.registerFactory(Registration.SOLID_BOILER_CONTAINER, SolidBoilerScreen::new);
        ScreenManager.registerFactory(Registration.ALLOYER_CONTAINER, AlloyerScreen::new);
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
