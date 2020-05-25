package com.vnator.turbinecraft.blocks.transfer.bevel;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.vnator.turbinecraft.blocks.generators.t1_steam_generator.SteamGeneratorRenderer;
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

public class BevelRenderer extends TileEntityRenderer<BevelTile> {
    public BevelRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(BevelTile tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        RenderSystem.color4f(.7f, .2f, .2f, .2f);
        renderGear(0, tile.otherFacing, tile, partialTicks, matrix, buffer, combinedLight, combinedOverlay);
        RenderSystem.color4f(1, 1, 1, 1);
        renderGear(45/2, tile.getBlockState().get(BlockStateProperties.FACING), tile, partialTicks, matrix, buffer, combinedLight, combinedOverlay);

    }

    public void renderGear(float offset, Direction dir, BevelTile tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
        BlockPos blockpos = tile.getPos();
        BlockState renderState = Registration.ROD_GEAR.getDefaultState();//.with(BlockStateProperties.FACING, dir);
        float ticks = 0;
        if(tile.getBlockState().get(BlockStateProperties.POWERED))
            ticks = tile.getWorld().getGameTime() + partialTicks;

        int [] angle = new int[]{dir.getXOffset(), dir.getYOffset(), dir.getZOffset()};
        double [] extra = new double[]{
                dir.getXOffset() == -1 ? 0.75 : 0,
                dir.getYOffset() == 1 ? 0.75 : 0,
                dir.getZOffset() == 1 ? 0.75 : 0
        };
        double [] rotations = new double[]{
                dir.getXOffset() == -1 ? 180 : 0,
                dir.getYOffset() == -1 ? 180 : 0,
                dir.getZOffset() == -1 ? 180 : 0
        };
        matrix.push();
        float zrot = dir.getZOffset() == 1 ? 180 : 0;
        if(dir == Direction.WEST)
            matrix.translate(0, .5, .5);
        else if(dir == Direction.EAST)
            matrix.translate(1, .5, .5);
        else if(dir == Direction.NORTH)
            matrix.translate(.5, .5, 0);
        else if(dir == Direction.SOUTH)
            matrix.translate(.5, .5, 1);
        else if(dir == Direction.UP)
            matrix.translate(.5, 1, .5);
        else
            matrix.translate(.5, 0, .5);

        ticks *= offset > 0 ? 1 : -1;
        matrix.rotate(new Quaternion(90*dir.getYOffset()+180, 90*dir.getXOffset()+zrot+180, (float)(ticks*180/Math.PI)+offset, true));

        //matrix.translate(-.5-dir.getXOffset()/2, -.5-dir.getYOffset()/2, -.5-dir.getZOffset()/2);
        //matrix.translate(Math.abs(angle[2])*0.5 - extra[0],0.5, Math.abs(angle[0])*0.5 + extra[2]);
        //matrix.rotate(new Quaternion(angle[0]*ticks, angle[1]*ticks, angle[2]*ticks, false));
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
        ClientRegistry.bindTileEntityRenderer(Registration.BEVEL_TILE, BevelRenderer::new);
    }
}
