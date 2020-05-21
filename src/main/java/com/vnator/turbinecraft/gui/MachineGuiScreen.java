package com.vnator.turbinecraft.gui;

import com.vnator.turbinecraft.TurbineCraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class MachineGuiScreen<T extends Container> extends ContainerScreen<T> {

    protected ResourceLocation GUI = new ResourceLocation(TurbineCraft.MOD_ID, "textures/gui/guisheet.png");

    public MachineGuiScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }
}
