package com.vnator.turbinecraft.blocks.generators.t1_furnace_generator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class FurnaceGenerator extends Block {
    private static final AxisAlignedBB base = new AxisAlignedBB(0, 0, 0, 1, 0.15, 1);
    private static final AxisAlignedBB body = new AxisAlignedBB(0.1, 0.15, 0.1, .95, 0.9, 0.9);
    private static final AxisAlignedBB shaft = new AxisAlignedBB(0.4, 0.4, 0.9, 0.6, 1, 0.6);

    private VoxelShape shape;

    public FurnaceGenerator() {
        super(Properties.create(Material.ROCK)
                .sound(SoundType.STONE)
                .hardnessAndResistance(2.0f)
                .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("basic_furnace_generator");
        VoxelShape s1 = VoxelShapes.create(base);
        VoxelShape s2 = VoxelShapes.create(body);
        VoxelShape s3 = VoxelShapes.create(shaft);

        VoxelShape s4 = VoxelShapes.combine(s1, s2, IBooleanFunction.OR);
        shape = VoxelShapes.combine(s3, s4, IBooleanFunction.OR);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FurnaceGeneratorTile();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            world.setBlockState(pos, state.with(BlockStateProperties.HORIZONTAL_FACING, getFacingFromEntity(pos, entity))
                    .with(BlockStateProperties.POWERED, false), 2);
        }
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        Vec3d vec = entity.getPositionVec();
        return Direction.getFacingFromVector((float) (vec.x - clickedBlock.getX()), 0, (float) (vec.z - clickedBlock.getZ()));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
        builder.add(BlockStateProperties.POWERED);
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return shape;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

}
