package com.vnator.turbinecraft.blocks.consumers.dynamometer;

import com.vnator.turbinecraft.blocks.consumers.furnace.FrictionFurnaceTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DynamometerBlock extends Block {
    public DynamometerBlock() {
        super(Properties.create(Material.IRON)
            .sound(SoundType.METAL)
                .hardnessAndResistance(2.0f)
        );
        setRegistryName("dynamometer_block");
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote && handIn == Hand.MAIN_HAND) {
            ((DynamometerBlockTile) worldIn.getTileEntity(pos)).rotationAcceptor.ifPresent(rotation -> {
                player.sendMessage(new StringTextComponent("Speed: " + rotation.getSpeed() + " , Force: " + rotation.getForce()));
            });
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public boolean hasTileEntity(BlockState state){
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world){
        return new DynamometerBlockTile();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            //For machines with rotational input, facing should be on the input side. Can output from opposite in TileEntity
            world.setBlockState(pos, state.with(BlockStateProperties.HORIZONTAL_FACING, getFacingFromEntity(pos, entity).getOpposite())
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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
