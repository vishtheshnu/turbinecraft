package com.vnator.turbinecraft.blocks.consumers.furnace;

import com.vnator.turbinecraft.gui.MachineGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class FrictionFurnaceScreen extends MachineGuiScreen<FrictionFurnaceContainer> {

    public FrictionFurnaceScreen(FrictionFurnaceContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        FontRenderer fr = Minecraft.getInstance().fontRenderer;
        String temp = ((FrictionFurnaceTile)container.getTileEntity()).temperature+" C";
        drawString(fr, temp, (xSize-fr.getStringWidth(temp))/2, 72, 0xffffff);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}
