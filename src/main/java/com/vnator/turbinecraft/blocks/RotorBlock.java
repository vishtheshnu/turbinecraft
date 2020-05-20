package com.vnator.turbinecraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class RotorBlock extends DirectionalBlock {

    public RotorBlock() {
        super(Properties.create(Material.IRON));
        setRegistryName("rotor");
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));
    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    //Below code shamelessly copied from Commoble's Tubes Reloaded
    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If
     * inapplicable, returns the passed blockstate.
     *
     */
    @Deprecated
    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, net.minecraft.world.IWorld world, BlockPos pos, Rotation direction)
    {
        return super.rotate(state, world, pos, direction);
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If
     * inapplicable, returns the passed blockstate.
     */
    @Deprecated
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }
}
