package com.vnator.turbinecraft.blocks.consumers.solid_fuel_boiler;

import com.vnator.turbinecraft.gui.MachineContainer;
import com.vnator.turbinecraft.gui.MachineGuiScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class SolidBoilerScreen extends MachineGuiScreen<SolidBoilerContainer> {
    public SolidBoilerScreen(SolidBoilerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }
}
