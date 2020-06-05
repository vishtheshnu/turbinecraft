package com.vnator.turbinecraft.blocks.misc.water_source;

import com.vnator.turbinecraft.blocks.generators.t1_furnace_generator.FurnaceGeneratorTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class InfiniteWaterBlock extends Block {
    public InfiniteWaterBlock() {
        super(Properties.create(Material.IRON)
        .hardnessAndResistance(.2f)
        .sound(SoundType.METAL));

        setRegistryName("infinite_water");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new InfiniteWaterTile();
    }
}
