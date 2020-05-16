package com.vnator.turbinecraft.setup;

import com.vnator.turbinecraft.TurbineCraft;
import com.vnator.turbinecraft.blocks.FirstBlock;
import com.vnator.turbinecraft.blocks.FirstBlockTile;
import com.vnator.turbinecraft.blocks.generators.t1_furnace_generator.FurnaceGenerator;
import com.vnator.turbinecraft.blocks.generators.t1_furnace_generator.FurnaceGeneratorTile;
import com.vnator.turbinecraft.items.FirstItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = TurbineCraft.MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class Registration {

    //Block Holders
    @ObjectHolder("turbinecraft:firstblock")
    public static FirstBlock FIRSTBLOCK;
    @ObjectHolder("turbinecraft:firstblock")
    public static TileEntityType<FirstBlockTile> FIRSTBLOCK_TILE;


    @ObjectHolder("turbinecraft:basic_furnace_generator")
    public static FurnaceGenerator BASIC_FURNACE_GENERATOR;
    @ObjectHolder("turbinecraft:basic_furnace_generator")
    public static TileEntityType<FurnaceGeneratorTile> BASIC_FURNACE_GENERATOR_TILE;

    //Item Holders
    @ObjectHolder("turbinecraft:firstitem")
    public static FirstItem FIRSTITEM;

    public static void init(){

    }

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new FirstBlock());
        event.getRegistry().register(new FurnaceGenerator());
    }

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
        Item.Properties properties = new Item.Properties().group(ModSetup.itemGroup);
        //Register items from blocks
        event.getRegistry().register(new BlockItem(FIRSTBLOCK, properties).setRegistryName("firstblock"));
        event.getRegistry().register(new BlockItem(BASIC_FURNACE_GENERATOR, properties).setRegistryName("basic_furnace_generator"));

        //Register items
        event.getRegistry().register(new FirstItem());
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event){
        //Register tile entity to block (can list multiple blocks to register the same tile entity to
        event.getRegistry().register(TileEntityType.Builder.create(FirstBlockTile::new, FIRSTBLOCK).build(null).setRegistryName("firstblock"));
        event.getRegistry().register(TileEntityType.Builder.create(FurnaceGeneratorTile::new, BASIC_FURNACE_GENERATOR).build(null).setRegistryName("basic_furnace_generator"));
    }
}
