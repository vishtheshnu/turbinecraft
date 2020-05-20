package com.vnator.turbinecraft.blocks.generators.t1_furnace_generator;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.vnator.turbinecraft.blocks.consumers.dynamometer.DynamometerBlockRenderer;
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

public class FurnaceGeneratorRenderer extends TileEntityRenderer<FurnaceGeneratorTile> {

    public FurnaceGeneratorRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(FurnaceGeneratorTile tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
        BlockPos blockpos = tile.getPos();
        Direction dir = tile.getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
        BlockState renderState = Registration.ROTOR.getDefaultState().with(BlockStateProperties.FACING, dir);
        float ticks = 0;
        if(tile.getBlockState().get(BlockStateProperties.POWERED))
            ticks = tile.getWorld().getGameTime() + partialTicks;

        int [] angle = new int[]{dir.getXOffset(), dir.getYOffset(), dir.getZOffset()};
        double [] extra = new double[]{
                dir.getXOffset() == -1 ? 0.75 : 0,
                dir.getYOffset() == 1 ? 0.75 : 0,
                dir.getZOffset() == 1 ? 0.75 : 0
        };
        matrix.push();
        matrix.translate(Math.abs(angle[2])*0.5 - extra[0],0.5, Math.abs(angle[0])*0.5 + extra[2]);
        matrix.rotate(new Quaternion(angle[0]*ticks, angle[1]*ticks, angle[2]*ticks, false));
        RenderType renderType = RenderTypeLookup.getRenderType(renderState);
        ForgeHooksClient.setRenderLayer(renderType);

        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        dispatcher.getBlockModelRenderer().renderModel(
                tile.getWorld(), dispatcher.getModelForState(renderState), renderState, blockpos, matrix, buffer.getBuffer(renderType), false,
                new Random(), renderState.getPositionRandom(blockpos), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE
        );

        matrix.pop();
    }

    public static void register(){
        ClientRegistry.bindTileEntityRenderer(Registration.BASIC_FURNACE_GENERATOR_TILE, FurnaceGeneratorRenderer::new);
    }
}
