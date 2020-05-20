package com.vnator.turbinecraft.blocks.consumers.dynamometer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class DynamometerBlockRenderer extends TileEntityRenderer<DynamometerBlockTile> {
    public DynamometerBlockRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(DynamometerBlockTile tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        //bufferIn.getBuffer(RenderType.getText())
    }

    public static void register(){
        ClientRegistry.bindTileEntityRenderer(Registration.DYNAMOMETER_BLOCK_TILE, DynamometerBlockRenderer::new);
    }
}
