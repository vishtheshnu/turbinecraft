package com.vnator.turbinecraft.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vnator.turbinecraft.TurbineCraft;
import com.vnator.turbinecraft.capabilities.rotational_power.RotationProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class MachineGuiScreen<T extends MachineContainer> extends ContainerScreen<T> {

    protected static final float[][] TIER_COLORS = new float[][]{
            new float[]{0.75f, 0, 0, 1},        //red
            new float[]{1, 0, 0, 1},        //red
            new float[]{1, .5f, 0, 1},      //orange
            new float[]{1, 1, 0, 1},        //yellow
            new float[]{.5f, 1, 0, 1},      //yellow-green
            new float[]{0, 1, 0, 1},        //green
            new float[]{.4f, 1, .7f, 1},    //blue-green
            new float[]{.4f, 1, 1, 1},      //cyan
            new float[]{0, .5f, 1, 1},      //blue
            new float[]{.5f, 0, 1, 1},      //violet
            new float[]{1, 0, 1, 1},        //purple
            new float[]{1, 0, .5f, 1}       //magenta
    };
    protected ResourceLocation GUI = new ResourceLocation(TurbineCraft.MOD_ID, "textures/gui/guisheet.png");
    protected int relX, relY;
    protected long timeSinceLastUpdate = System.currentTimeMillis();
    protected int prevSpeedPixels, prevForcePixels;

    public MachineGuiScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        //Block Name
        int width = Minecraft.getInstance().fontRenderer.getStringWidth(title.getFormattedText());
        drawString(Minecraft.getInstance().fontRenderer, title.getFormattedText(), (relX+width)/2, relY+5, 0xffffff);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int deltaMilli = (int)(System.currentTimeMillis() - timeSinceLastUpdate);
        timeSinceLastUpdate = System.currentTimeMillis();

        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(GUI);
        relX = (width - xSize)/2;
        relY = (height - ySize)/2;
        blit(relX, relY, 0, 0, xSize, ySize);

        //Items
        for(MachineContainer.ItemHandler item : container.itemHandlers){
            blit(item.x+relX, item.y+relY, 0, 174, 18, 18);
        }

        //Fluids
        container.tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluids -> {
            for (MachineContainer.TankHandler tank : container.tankHandlers) {
                blit(tank.x+relX, 5+relY, 18, 174, 20, 62);
                FluidStack fluid = fluids.getFluidInTank(tank.id);
                if(fluid != null && fluid != FluidStack.EMPTY && fluid.getAmount() != 0) {
                    ResourceLocation ftexture = fluids.getFluidInTank(tank.id).getFluid().getAttributes().getStillTexture(fluid);
                    TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(ftexture);
                    ftexture = sprite.getName();
                    int startY = 60 - (int)(60.0 * fluids.getFluidInTank(tank.id).getAmount() / fluids.getTankCapacity(tank.id));
                    //minecraft.getTextureManager().bindTexture(ftexture);
                    minecraft.getTextureManager().bindTexture(new ResourceLocation(ftexture.getNamespace(), "textures/" + ftexture.getPath() + ".png"));
                    int col = fluid.getFluid().getAttributes().getColor();
                    RenderSystem.color4f(((col >> 16) & 255)/255f, ((col >> 8) & 255)/255f, (col & 255)/255f, ((col >> 24) & 255)/255f);
                    blit(tank.x+relX + 1, 6+relY+ startY, 0, startY, 16, 60-startY, 16, 256);
                    blit(tank.x+relX + 17, 6+relY+ startY, 0, startY, 2, 60-startY, 16, 256);
                    //blit(tank.x+relX + 1, 6+relY+ startY, 0, 0, 16, 60-startY);
                    //blit(tank.x+relX + 1, 6+relY+ startY, 0, 16,  60 - startY, sprite);
                    RenderSystem.color4f(1, 1, 1, 1);
                    minecraft.getTextureManager().bindTexture(GUI);
                }
                blit(tank.x+relX+2, 5+relY, 40, 174, 5, 62);
            }
        });

        //Energy
        for(MachineContainer.FEHandler battery : container.feHandlers){
            container.tileEntity.getCapability(CapabilityEnergy.ENERGY, battery.side).ifPresent(pow -> {
                blit(battery.x, 5, 45, 174, 20, 62);
                int startY = 60-(int)(60.0*pow.getEnergyStored()/pow.getMaxEnergyStored());
                blit(battery.x+1, 6+startY, 67, 176+startY, 20, 60-startY);
            });
        }

        //Icons
        for(MachineContainer.ProgressHandler prog : container.progressHandlers){
            blit(prog.x+relX, prog.y+relY, 176, prog.id*16, 24, 16);
            int xstart = 0, xend = 0;
            int ystart = 0, yend = 0;
            if(prog.id == 0){
                xend = 24;
                ystart = (int)(16*(1.0-prog.ratioFunc.getRatio()));
                yend = 16;
            }else if(prog.id == 1){
                xend = (int)(24*(1.0-prog.ratioFunc.getRatio()));
                yend = 16;
            }
            if(prog.ratioFunc.getRatio() > 0)
                blit(prog.x+xstart+relX, prog.y+ystart+relY, 201+xstart, prog.id*16+ystart, xend-xstart, yend-ystart);
        }

        //Rotational Power
        container.tileEntity.getCapability(RotationProvider.ROTATION_CAPABILITY).ifPresent(rot -> {
            blit(relX+5, relY+70, 87, 174, 62, 12);
            double speed = rot.getSpeed() == 0 ? 0 : (Math.log(rot.getSpeed()) / Math.log(2));
            int tier = (int)speed;
            int pixels = (int)(60.0*(speed - Math.floor(speed)));
            if(pixels == 0 && speed != 0)
                pixels = 60;
            else if(speed == 0)
                pixels = 0;
            //Rotational change animation
            if(prevSpeedPixels > pixels)
                prevSpeedPixels -= 1;
            else if(prevSpeedPixels < pixels)
                prevSpeedPixels += 1;

            RenderSystem.color4f(TIER_COLORS[tier][0], TIER_COLORS[tier][1], TIER_COLORS[tier][2], TIER_COLORS[tier][3]);
            blit(relX+6, relY+71, 88+((int)System.currentTimeMillis()/30%3), 199, prevSpeedPixels, 10);
            RenderSystem.color4f(1, 1, 1,1);

            blit(relX+xSize-5-62, relY+70, 87, 174, 62, 12);
            double force = rot.getForce() == 0 ? 0 : (Math.log(rot.getForce()) / Math.log(2));
            tier = (int)force;
            pixels = (int)(60.0*(force - Math.floor(force)));
            if(pixels == 0 && force != 0)
                pixels = 60;
            else if(force == 0)
                pixels = 0;
            //Rotational change animation
            if(prevForcePixels > pixels)
                prevForcePixels -= 1;
            else if(prevForcePixels < pixels)
                prevForcePixels += 1;
            RenderSystem.color4f(TIER_COLORS[tier][0], TIER_COLORS[tier][1], TIER_COLORS[tier][2], TIER_COLORS[tier][3]);
            blit(relX+xSize-5-61+60-prevForcePixels, relY+71, 91-((int)System.currentTimeMillis()/30%3), 199, prevForcePixels, 10);
            RenderSystem.color4f(1, 1, 1,1);
        });
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        //Tanks
        for(MachineContainer.TankHandler tank : container.tankHandlers){
            if(mouseX-relX > tank.x && mouseX - relX < tank.x + 20 &&
                mouseY-relY > 5 && mouseY-relY < 65)
                container.tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluid -> {

                    List<String> tooltip = new ArrayList<>();
                    tooltip.add(fluid.getFluidInTank(tank.id).getDisplayName().getFormattedText());
                    tooltip.add(new StringTextComponent(fluid.getFluidInTank(tank.id).getAmount()+" mb")
                            .applyTextStyle(TextFormatting.GRAY).getFormattedText());

                    renderTooltip(tooltip, mouseX, mouseY);
                });
        }

        //Rotational Power
        if(mouseY-relY > 70 && mouseY-relY < 82){
            container.tileEntity.getCapability(RotationProvider.ROTATION_CAPABILITY).ifPresent(rot -> {
                if(mouseX-relX > 5 && mouseX-relX < 67){
                    List<String> tooltip = new ArrayList<>();
                    tooltip.add(new StringTextComponent("Speed").applyTextStyle(TextFormatting.BOLD).getFormattedText());
                    tooltip.add(new StringTextComponent("Tier: "+getTier(rot.getSpeed()))
                            .applyTextStyle(getTierFormat(getTier(rot.getSpeed()))).getFormattedText());
                    tooltip.add(new StringTextComponent(rot.getSpeed()+" rps").applyTextStyle(TextFormatting.GRAY).getFormattedText());
                    renderTooltip(tooltip, mouseX, mouseY);
                }else if(mouseX-relX > xSize-5-62 && mouseX-relX < xSize-5){
                    List<String> tooltip = new ArrayList<>();
                    tooltip.add(new StringTextComponent("Force").applyTextStyle(TextFormatting.BOLD).getFormattedText());
                    tooltip.add(new StringTextComponent("Tier: "+getTier(rot.getForce()))
                            .applyTextStyle(getTierFormat(getTier(rot.getForce()))).getFormattedText());
                    tooltip.add(new StringTextComponent(rot.getForce()+" psi").applyTextStyle(TextFormatting.GRAY).getFormattedText());
                    renderTooltip(tooltip, mouseX, mouseY);
                }
            });

        }

        super.renderHoveredToolTip(mouseX, mouseY);
    }

    protected int getTier(long val){
        int tier = 0;
        while(val != 0){
            val >>= 1;
            tier++;
        }
        return tier;
    }

    protected TextFormatting getTierFormat(int tier){
        switch (tier){
            case 0: return TextFormatting.WHITE;
            case 1: return TextFormatting.DARK_RED;
            case 2: return TextFormatting.RED;
            case 3: return TextFormatting.YELLOW;
            case 4: return TextFormatting.GREEN;
            case 5: return TextFormatting.DARK_GREEN;
            case 6: return TextFormatting.GREEN;
            case 7: return TextFormatting.AQUA;
            case 8: return TextFormatting.BLUE;
            case 9: return TextFormatting.DARK_PURPLE;
            case 10: return TextFormatting.LIGHT_PURPLE;
            case 11: return TextFormatting.RED;
            default: return TextFormatting.WHITE;
        }
    }
}
