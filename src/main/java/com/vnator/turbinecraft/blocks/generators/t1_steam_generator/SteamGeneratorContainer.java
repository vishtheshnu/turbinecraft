package com.vnator.turbinecraft.blocks.generators.t1_steam_generator;

import com.vnator.turbinecraft.gui.MachineContainer;
import com.vnator.turbinecraft.setup.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class SteamGeneratorContainer extends MachineContainer {

    public SteamGeneratorContainer(int id, World world, BlockPos pos, PlayerInventory inventory, PlayerEntity playerEntity) {
        super(Registration.BASIC_STEAM_GENERATOR_CONTAINER, id, world, pos, inventory, playerEntity);

        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            addSlot(new SlotItemHandler(inv, 0, 64, 24));
        });

        layoutPlayerInventorySlots(8, 92, playerInventory);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerEntity, Registration.BASIC_STEAM_GENERATOR);
    }
}
