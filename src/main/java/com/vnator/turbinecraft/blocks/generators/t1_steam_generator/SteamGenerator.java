package com.vnator.turbinecraft.blocks.generators.t1_steam_generator;

import com.vnator.turbinecraft.blocks.generators.t1_furnace_generator.FurnaceGeneratorTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class SteamGenerator extends Block {
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
        /*
        hand.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(tank -> {
            worldIn.getTileEntity(pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(genTank -> {
                FluidUtil.tryFluidTransfer(genTank, tank, Integer.MAX_VALUE, true);
                return;
            });
        });
        hand.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(tank -> {
            worldIn.getTileEntity(pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(genTank -> {
                FluidUtil.tryFluidTransfer(genTank, tank, Integer.MAX_VALUE, true);
                return;
            });
        });
        */
        AtomicBoolean hasInteracted = new AtomicBoolean(false);
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
                }
                hasInteracted.set(true);
            });
        }

        if(hasInteracted.get())
            return ActionResultType.CONSUME;


        //Open GUI

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
            world.setBlockState(pos, state.with(BlockStateProperties.FACING, getFacingFromEntity(pos, entity))
                    .with(BlockStateProperties.POWERED, false), 2);
        }
    }

    public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity) {
        Vec3d vec = entity.getPositionVec();
        return Direction.getFacingFromVector((float) (vec.x - clickedBlock.getX()), (float) (vec.y - clickedBlock.getY()), (float) (vec.z - clickedBlock.getZ()));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
        builder.add(BlockStateProperties.FACING);
        builder.add(BlockStateProperties.POWERED);
    }
}
