package com.vnator.turbinecraft.blocks.consumers.solid_fuel_boiler;

import com.vnator.turbinecraft.blocks.MachineBlock;
import com.vnator.turbinecraft.blocks.generators.t1_furnace_generator.FurnaceGeneratorTile;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class SolidBoilerBlock extends MachineBlock {
    public SolidBoilerBlock() {
        super(Properties.create(Material.ROCK)
        .hardnessAndResistance(.2f)
        .sound(SoundType.STONE));
        setRegistryName("boiler_solid_fuel");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SolidBoilerTile();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(player.isSneaking())
            return ActionResultType.PASS;

        if(worldIn.isRemote)
            return ActionResultType.SUCCESS;

        //Open GUI
        TileEntity te = worldIn.getTileEntity(pos);
        if(te instanceof INamedContainerProvider){
            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, te.getPos());
            return ActionResultType.SUCCESS;
        }

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }
}
