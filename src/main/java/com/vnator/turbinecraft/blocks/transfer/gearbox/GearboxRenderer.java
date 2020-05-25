package com.vnator.turbinecraft.blocks.transfer.gearbox;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vnator.turbinecraft.blocks.transfer.shaft.ShaftRenderer;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.Random;

public class GearboxRenderer extends TileEntityRenderer<GearboxTile> {
    public GearboxRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(GearboxTile tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        BlockPos blockpos = tile.getPos();
        Direction dir = tile.getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
        BlockState renderState = Registration.SHAFT.getDefaultState().with(BlockStateProperties.FACING, dir);

        float ticks = 0;
        if(tile.getBlockState().get(BlockStateProperties.POWERED))
            ticks = tile.getWorld().getGameTime() + partialTicks;
        ticks *= tile.isPoweredOnFacing ? -1 : 1; //To rotate in the correct direction

        double xTransform = dir.getXOffset() == 0 ? .5 : 0;
        double yTransform = dir.getYOffset() == 0 ? .5 : 0;
        double zTransform = dir.getZOffset() == 0 ? .5 : 0;

        float xRot = (dir.getXOffset()*ticks) ;
        float yRot = (dir.getYOffset()*ticks) ;
        float zRot = (dir.getZOffset()*ticks) ;

        matrix.push();
        matrix.translate(xTransform, yTransform, zTransform);
        matrix.rotate(new Quaternion(xRot, yRot, zRot, false));

        RenderType renderType = RenderTypeLookup.getRenderType(renderState);
        ForgeHooksClient.setRenderLayer(renderType);

        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        dispatcher.getBlockModelRenderer().renderModel(
                tile.getWorld(), dispatcher.getModelForState(renderState), renderState, blockpos, matrix, buffer.getBuffer(renderType), false,
                new Random(), renderState.getPositionRandom(blockpos), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );
        matrix.pop();

        matrix.push();

        renderState = Registration.GEAR_MODEL.getDefaultState().with(BlockStateProperties.FACING, dir);
        renderType = RenderTypeLookup.getRenderType(renderState);
        ForgeHooksClient.setRenderLayer(renderType);
        dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();

        float xtrans = dir.getXOffset() != 0 ? -.5f : .5f;
        matrix.translate(xtrans, .5, .5);
        matrix.rotate(new Quaternion(xRot, yRot, zRot, false));
        dispatcher.getBlockModelRenderer().renderModel(
                tile.getWorld(), dispatcher.getModelForState(renderState), renderState, blockpos, matrix, buffer.getBuffer(renderType), false,
                new Random(), renderState.getPositionRandom(blockpos), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );

        matrix.translate(dir.getXOffset()*.06251, dir.getYOffset()*.06251, dir.getZOffset()*.06251);
        matrix.scale(1+Math.abs(dir.getYOffset() + dir.getZOffset())/4f, 1+Math.abs(dir.getXOffset() + dir.getZOffset())/4f,
                1+Math.abs(dir.getXOffset() + dir.getYOffset())/4f);
        matrix.rotate(new Quaternion(-.6f*xRot, -.6f*yRot, -.6f*zRot, false));
        dispatcher.getBlockModelRenderer().renderModel(
                tile.getWorld(), dispatcher.getModelForState(renderState), renderState, blockpos, matrix, buffer.getBuffer(renderType), false,
                new Random(), renderState.getPositionRandom(blockpos), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );

        if(tile.factor > 2) {
            matrix.translate(dir.getXOffset() * .06251, dir.getYOffset() * .06251, dir.getZOffset() * .06251);
            matrix.scale(1 + Math.abs(dir.getYOffset() + dir.getZOffset()) / 4f, 1 + Math.abs(dir.getXOffset() + dir.getZOffset()) / 4f,
                    1 + Math.abs(dir.getXOffset() + dir.getYOffset()) / 4f);
            matrix.rotate(new Quaternion(-.2f * xRot, -.2f * yRot, -.2f * zRot, false));
            dispatcher.getBlockModelRenderer().renderModel(
                    tile.getWorld(), dispatcher.getModelForState(renderState), renderState, blockpos, matrix, buffer.getBuffer(renderType), false,
                    new Random(), renderState.getPositionRandom(blockpos), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
            );
        }

        if(tile.factor > 4) {
            matrix.translate(dir.getXOffset() * .06251, dir.getYOffset() * .06251, dir.getZOffset() * .06251);
            matrix.scale(1 + Math.abs(dir.getYOffset() + dir.getZOffset()) / 8f, 1 + Math.abs(dir.getXOffset() + dir.getZOffset()) / 8f,
                    1 + Math.abs(dir.getXOffset() + dir.getYOffset()) / 8f);
            matrix.rotate(new Quaternion(-.1f * xRot, -.1f * yRot, -.1f * zRot, false));
            dispatcher.getBlockModelRenderer().renderModel(
                    tile.getWorld(), dispatcher.getModelForState(renderState), renderState, blockpos, matrix, buffer.getBuffer(renderType), false,
                    new Random(), renderState.getPositionRandom(blockpos), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
            );
        }
        matrix.pop();
    }

    public static void register(){
        ClientRegistry.bindTileEntityRenderer(Registration.GEARBOX_TILE, GearboxRenderer::new);
    }
}
