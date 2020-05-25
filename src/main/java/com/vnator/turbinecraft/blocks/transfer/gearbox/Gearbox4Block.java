package com.vnator.turbinecraft.blocks.transfer.gearbox;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class Gearbox4Block extends GearboxBlock{

    public Gearbox4Block(){
        super();
        setRegistryName("gearbox_4");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world){
        return new GearboxTile(4);
    }
}
