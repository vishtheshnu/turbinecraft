package com.vnator.turbinecraft.blocks.consumers.alloyer;

import com.vnator.turbinecraft.blocks.MachineTileEntity;
import com.vnator.turbinecraft.blocks.generators.t1_steam_generator.SteamGeneratorTile;
import com.vnator.turbinecraft.gui.MachineContainer;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class AlloyerContainer extends MachineContainer {
    public AlloyerContainer(int id, World world, BlockPos pos, PlayerInventory inventory, PlayerEntity playerEntity) {
        super(Registration.ALLOYER_CONTAINER, id, world, pos, inventory, playerEntity);
        layoutPlayerInventorySlots(8, 92, playerInventory);
        ((MachineTileEntity) tileEntity).guiInventoryHandler.ifPresent(inv -> {
            addInventories(Position.LEFT, inv, 0, 1);
            addInventories(Position.RIGHT, inv, 2);
        });
        addProgressIcon(true, 1, ((AlloyerTile)world.getTileEntity(pos))::getProgress);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerEntity, Registration.ALLOYER_BLOCK);
    }
}
