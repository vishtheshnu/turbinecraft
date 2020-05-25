package com.vnator.turbinecraft.blocks.transfer.gearbox;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class Gearbox8Block extends GearboxBlock{

    public Gearbox8Block(){
        super();
        setRegistryName("gearbox_8");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world){
        return new GearboxTile(8);
    }
}
