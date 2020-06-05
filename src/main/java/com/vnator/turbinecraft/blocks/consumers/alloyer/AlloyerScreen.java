package com.vnator.turbinecraft.blocks.consumers.alloyer;

import com.vnator.turbinecraft.gui.MachineGuiScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class AlloyerScreen extends MachineGuiScreen<AlloyerContainer> {
    public AlloyerScreen(AlloyerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }
}
