package com.vnator.turbinecraft.blocks.generators.t1_steam_generator;

import com.vnator.turbinecraft.blocks.generators.t1_furnace_generator.FurnaceGeneratorTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class SteamGenerator extends Block {

    private static final AxisAlignedBB aabbEW = new AxisAlignedBB(0, 0, 0.375, 1, 1, 0.625);
    private static final AxisAlignedBB aabbNS = new AxisAlignedBB(0.375, 0, 0, 0.625, 1, 1);

    public SteamGenerator() {
        super(Properties.create(Material.ROCK)
                .sound(SoundType.STONE)
                .hardnessAndResistance(2.0f)
                .harvestTool(ToolType.PICKAXE)
        );
        setRegistryName("basic_steam_generator");
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(worldIn.isRemote)
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);

        ItemStack hand = player.getHeldItem(handIn);

        AtomicBoolean hasInteracted = new AtomicBoolean(false);
        AtomicBoolean hasUsed = new AtomicBoolean(false);
        worldIn.getTileEntity(pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(genTank -> {
            if(FluidUtil.interactWithFluidHandler(player, handIn, genTank))
                hasInteracted.set(true);
        });


        if(hand.isItemEqual(FluidUtil.getFilledBucket(new FluidStack(Fluids.WATER.getFluid(), 1000))) ){
            System.out.println("Bucket of water!");
            worldIn.getTileEntity(pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(genTank -> {
                int filled = genTank.fill(new FluidStack(Fluids.WATER.getFluid(), 1000), IFluidHandler.FluidAction.SIMULATE);
                if(filled == 1000){
                    genTank.fill(new FluidStack(Fluids.WATER.getFluid(), 1000), IFluidHandler.FluidAction.EXECUTE);
                    player.setHeldItem(handIn, new ItemStack(Items.BUCKET));
                    hasUsed.set(true);
                }
                hasInteracted.set(true);
            });
        }

        if(hasInteracted.get()){
            if(hasUsed.get())
                return ActionResultType.CONSUME;
            else
                return ActionResultType.FAIL;
        }



        //Open GUI
        TileEntity te = worldIn.getTileEntity(pos);
        if(te instanceof INamedContainerProvider){
            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, te.getPos());
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SteamGeneratorTile();
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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        if(state.get(BlockStateProperties.HORIZONTAL_FACING).getXOffset() != 0)
            return VoxelShapes.create(aabbEW);
        else
            return VoxelShapes.create(aabbNS);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if(state.get(BlockStateProperties.HORIZONTAL_FACING).getXOffset() != 0)
            return VoxelShapes.create(aabbEW);
        else
            return VoxelShapes.create(aabbNS);
    }
}
