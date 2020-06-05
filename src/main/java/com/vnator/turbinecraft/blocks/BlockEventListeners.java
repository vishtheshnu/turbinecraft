package com.vnator.turbinecraft.blocks;

import com.vnator.turbinecraft.blocks.transfer.gearbox.GearboxBlock;
import com.vnator.turbinecraft.blocks.transfer.gearbox.GearboxTile;
import com.vnator.turbinecraft.blocks.transfer.junction.JunctionTile;
import com.vnator.turbinecraft.blocks.transfer.shaft.ShaftBlock;
import com.vnator.turbinecraft.blocks.transfer.shaft.ShaftBlockTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

@Mod.EventBusSubscriber
public class BlockEventListeners {

    @SubscribeEvent
    public static void onItemUse(RightClickBlock event){
        //Wrench used on supporting blocks
        if(event.getItemStack().getToolTypes().contains(ToolType.get("wrench"))){
            TileEntity entity = event.getWorld().getTileEntity(event.getPos());
            if(entity != null){
                if(entity instanceof GearboxTile)
                    ((GearboxTile) entity).onWrench(event.getFace(), event.getPlayer().isSneaking(), event.getPlayer());
                else if(entity instanceof ShaftBlockTile)
                    ((ShaftBlockTile) entity).onWrench(event.getFace(), event.getPlayer());
                else if(entity instanceof JunctionTile)
                    ((JunctionTile) entity).onWrench(event.getFace(), event.getPlayer());
            }
        }
    }
}
