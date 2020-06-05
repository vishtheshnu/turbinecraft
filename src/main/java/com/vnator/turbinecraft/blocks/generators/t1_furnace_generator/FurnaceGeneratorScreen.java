package com.vnator.turbinecraft.blocks.generators.t1_furnace_generator;

import com.vnator.turbinecraft.gui.MachineGuiScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class FurnaceGeneratorScreen extends MachineGuiScreen<FurnaceGeneratorContainer> {
    public FurnaceGeneratorScreen(FurnaceGeneratorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }
}
