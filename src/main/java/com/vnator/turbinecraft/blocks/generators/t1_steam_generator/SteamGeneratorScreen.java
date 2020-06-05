package com.vnator.turbinecraft.blocks.generators.t1_steam_generator;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import com.vnator.turbinecraft.gui.MachineGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class SteamGeneratorScreen extends MachineGuiScreen<SteamGeneratorContainer> {
    public SteamGeneratorScreen(SteamGeneratorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        //long speed = container.getTileEntity().getCapability(RotationProvider.ROTATION_CAPABILITY).map(r -> r.getSpeed()).orElse(1l);
        //long force = container.getTileEntity().getCapability(RotationProvider.ROTATION_CAPABILITY).map(r -> r.getForce()).orElse(1l);
        //drawString(Minecraft.getInstance().fontRenderer, "Speed: "+speed, 7, 85, 0xffffff);
        //drawString(Minecraft.getInstance().fontRenderer, "Force: "+force, 110, 85, 0xffffff);
    }


}
