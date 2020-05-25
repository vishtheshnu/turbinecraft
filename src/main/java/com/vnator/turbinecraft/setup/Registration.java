package com.vnator.turbinecraft.setup;

import com.vnator.turbinecraft.TurbineCraft;
import com.vnator.turbinecraft.blocks.FirstBlock;
import com.vnator.turbinecraft.blocks.FirstBlockTile;
import com.vnator.turbinecraft.blocks.RotorBlock;
import com.vnator.turbinecraft.blocks.ShaftLongBlock;
import com.vnator.turbinecraft.blocks.consumers.dynamometer.DynamometerBlock;
import com.vnator.turbinecraft.blocks.consumers.dynamometer.DynamometerBlockTile;
import com.vnator.turbinecraft.blocks.consumers.furnace.FrictionFurnace;
import com.vnator.turbinecraft.blocks.consumers.furnace.FrictionFurnaceTile;
import com.vnator.turbinecraft.blocks.generators.t1_furnace_generator.FurnaceGenerator;
import com.vnator.turbinecraft.blocks.generators.t1_furnace_generator.FurnaceGeneratorTile;
import com.vnator.turbinecraft.blocks.generators.t1_steam_generator.SteamGenerator;
import com.vnator.turbinecraft.blocks.generators.t1_steam_generator.SteamGeneratorContainer;
import com.vnator.turbinecraft.blocks.generators.t1_steam_generator.SteamGeneratorTile;
import com.vnator.turbinecraft.blocks.transfer.bevel.BevelBlock;
import com.vnator.turbinecraft.blocks.transfer.bevel.BevelFaceBlock;
import com.vnator.turbinecraft.blocks.transfer.bevel.BevelTile;
import com.vnator.turbinecraft.blocks.transfer.bevel.RodGearBlock;
import com.vnator.turbinecraft.blocks.transfer.gearbox.*;
import com.vnator.turbinecraft.blocks.transfer.junction.JunctionBlock;
import com.vnator.turbinecraft.blocks.transfer.junction.JunctionTile;
import com.vnator.turbinecraft.blocks.transfer.shaft.ShaftBlock;
import com.vnator.turbinecraft.blocks.transfer.shaft.ShaftBlockTile;
import com.vnator.turbinecraft.items.FirstItem;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
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

    @ObjectHolder("turbinecraft:rotor")
    public static RotorBlock ROTOR;
    @ObjectHolder("turbinecraft:shaft_long")
    public static ShaftLongBlock SHAFT;
    @ObjectHolder("turbinecraft:bevel_face")
    public static BevelFaceBlock BEVEL_FACE;
    @ObjectHolder("turbinecraft:rod_gear")
    public static RodGearBlock ROD_GEAR;
    @ObjectHolder("turbinecraft:gear_model")
    public static GearBlock GEAR_MODEL;

    //Generators
    @ObjectHolder("turbinecraft:basic_furnace_generator")
    public static FurnaceGenerator BASIC_FURNACE_GENERATOR;
    @ObjectHolder("turbinecraft:basic_furnace_generator")
    public static TileEntityType<FurnaceGeneratorTile> BASIC_FURNACE_GENERATOR_TILE;

    @ObjectHolder("turbinecraft:basic_steam_generator")
    public static SteamGenerator BASIC_STEAM_GENERATOR;
    @ObjectHolder("turbinecraft:basic_steam_generator")
    public static TileEntityType<SteamGeneratorTile> BASIC_STEAM_GENERATOR_TILE;
    @ObjectHolder("turbinecraft:basic_steam_generator")
    public static ContainerType<SteamGeneratorContainer> BASIC_STEAM_GENERATOR_CONTAINER;


    //Consumers
    @ObjectHolder("turbinecraft:dynamometer_block")
    public static DynamometerBlock DYNAMOMETER_BLOCK;
    @ObjectHolder("turbinecraft:dynamometer_block")
    public static TileEntityType<DynamometerBlockTile> DYNAMOMETER_BLOCK_TILE;

    @ObjectHolder("turbinecraft:friction_furnace")
    public static FrictionFurnace FRICTION_FURNACE;
    @ObjectHolder("turbinecraft:friction_furnace")
    public static TileEntityType<FrictionFurnaceTile> FRICTION_FURNACE_TILE;


    //Transfer
    @ObjectHolder("turbinecraft:shaft")
    public static ShaftBlock SHAFT_BLOCK;
    @ObjectHolder("turbinecraft:shaft")
    public static TileEntityType<ShaftBlockTile> SHAFT_BLOCK_TILE;

    @ObjectHolder("turbinecraft:bevel_gears")
    public static BevelBlock BEVEL_BLOCK;
    @ObjectHolder("turbinecraft:bevel_gears")
    public static TileEntityType<BevelTile> BEVEL_TILE;

    @ObjectHolder("turbinecraft:junction")
    public static JunctionBlock JUNCTION_BLOCK;
    @ObjectHolder("turbinecraft:junction")
    public static TileEntityType<JunctionTile> JUNCTION_TILE;

    @ObjectHolder("turbinecraft:gearbox_2")
    public static Gearbox2Block GEARBOX_2_BLOCK;
    @ObjectHolder("turbinecraft:gearbox_4")
    public static Gearbox4Block GEARBOX_4_BLOCK;
    @ObjectHolder("turbinecraft:gearbox_8")
    public static Gearbox8Block GEARBOX_8_BLOCK;
    @ObjectHolder("turbinecraft:gearbox_2")
    public static TileEntityType<GearboxTile> GEARBOX_TILE;


    //Item Holders
    @ObjectHolder("turbinecraft:firstitem")
    public static FirstItem FIRSTITEM;

    public static void init(){
        //Check ModSetup to register TileEntity/Block Renderers!
    }

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new FirstBlock());
        event.getRegistry().register(new FurnaceGenerator());
        event.getRegistry().register(new SteamGenerator());
        event.getRegistry().register(new DynamometerBlock());
        event.getRegistry().register(new FrictionFurnace());
        event.getRegistry().register(new RotorBlock());
        event.getRegistry().register(new ShaftLongBlock());
        event.getRegistry().register(new BevelFaceBlock());
        event.getRegistry().register(new RodGearBlock());
        event.getRegistry().register(new GearBlock());
        event.getRegistry().register(new ShaftBlock());
        event.getRegistry().register(new BevelBlock());
        event.getRegistry().register(new JunctionBlock());
        event.getRegistry().register(new Gearbox2Block());
        event.getRegistry().register(new Gearbox4Block());
        event.getRegistry().register(new Gearbox8Block());
    }

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
        Item.Properties properties = new Item.Properties().group(ModSetup.itemGroup);
        //Register items from blocks
        event.getRegistry().register(new BlockItem(FIRSTBLOCK, properties).setRegistryName("firstblock"));
        event.getRegistry().register(new BlockItem(BASIC_FURNACE_GENERATOR, properties).setRegistryName("basic_furnace_generator"));
        event.getRegistry().register(new BlockItem(BASIC_STEAM_GENERATOR, properties).setRegistryName("basic_steam_generator"));

        event.getRegistry().register(new BlockItem(DYNAMOMETER_BLOCK, properties).setRegistryName("dynamometer_block"));
        event.getRegistry().register(new BlockItem(FRICTION_FURNACE, properties).setRegistryName("friction_furnace"));

        event.getRegistry().register(new BlockItem(SHAFT_BLOCK, properties).setRegistryName("shaft"));
        event.getRegistry().register(new BlockItem(BEVEL_BLOCK, properties).setRegistryName("bevel_gears"));
        event.getRegistry().register(new BlockItem(JUNCTION_BLOCK, properties).setRegistryName("junction"));
        event.getRegistry().register(new BlockItem(GEARBOX_2_BLOCK, properties).setRegistryName("gearbox_2"));
        event.getRegistry().register(new BlockItem(GEARBOX_4_BLOCK, properties).setRegistryName("gearbox_4"));
        event.getRegistry().register(new BlockItem(GEARBOX_8_BLOCK, properties).setRegistryName("gearbox_8"));

        //Register items
        event.getRegistry().register(new FirstItem());
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event){
        //Register tile entity to block (can list multiple blocks to register the same tile entity to
        event.getRegistry().register(TileEntityType.Builder.create(FirstBlockTile::new, FIRSTBLOCK).build(null).setRegistryName("firstblock"));
        //Generators
        event.getRegistry().register(TileEntityType.Builder.create(FurnaceGeneratorTile::new, BASIC_FURNACE_GENERATOR).build(null).setRegistryName("basic_furnace_generator"));
        event.getRegistry().register(TileEntityType.Builder.create(SteamGeneratorTile::new, BASIC_STEAM_GENERATOR).build(null).setRegistryName("basic_steam_generator"));
        //Consumers
        event.getRegistry().register(TileEntityType.Builder.create(DynamometerBlockTile::new, DYNAMOMETER_BLOCK).build(null).setRegistryName("dynamometer_block"));
        event.getRegistry().register(TileEntityType.Builder.create(FrictionFurnaceTile::new, FRICTION_FURNACE).build(null).setRegistryName("friction_furnace"));
        //Transfer
        event.getRegistry().register(TileEntityType.Builder.create(ShaftBlockTile::new, SHAFT_BLOCK).build(null).setRegistryName("shaft"));
        event.getRegistry().register(TileEntityType.Builder.create(BevelTile::new, BEVEL_BLOCK).build(null).setRegistryName("bevel_gears"));
        event.getRegistry().register(TileEntityType.Builder.create(JunctionTile::new, JUNCTION_BLOCK).build(null).setRegistryName("junction"));
        event.getRegistry().register(TileEntityType.Builder.create(GearboxTile::new, GEARBOX_2_BLOCK, GEARBOX_4_BLOCK, GEARBOX_8_BLOCK).build(null).setRegistryName("gearbox_2"));
    }

    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event){
        /**DON'T FORGET TO REGISTER IN CLIENTPROXY'S INIT METHOD!*/
        event.getRegistry().register(IForgeContainerType.create(((windowId, inv, data) -> new SteamGeneratorContainer(windowId, TurbineCraft.proxy.getClientWorld(), data.readBlockPos(), inv, TurbineCraft.proxy.getClientPlayer()))).setRegistryName("basic_steam_generator"));
    }
}
