package com.vnator.turbinecraft.fluids;

import com.vnator.turbinecraft.TurbineCraft;
import com.vnator.turbinecraft.setup.ModSetup;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidRegistration {

    public static DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, TurbineCraft.MOD_ID);
    public static DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, TurbineCraft.MOD_ID);
    public static DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, TurbineCraft.MOD_ID);

    public static RegistryObject<FlowingFluid> steam_fluid = FLUIDS.register("steam", () -> new ForgeFlowingFluid.Source(FluidSteam.PROPS));
    public static RegistryObject<FlowingFluid> steam_fluid_flowing = FLUIDS.register("steam_flowing", () -> new ForgeFlowingFluid.Flowing(FluidSteam.PROPS));
    public static RegistryObject<FlowingFluidBlock> steam_fluid_block = BLOCKS.register("steam_block",
            () -> new FlowingFluidBlock(steam_fluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
    public static RegistryObject<Item> steam_fluid_bucket = ITEMS.register("steam_bucket",
            () -> new BucketItem(steam_fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ModSetup.itemGroup)));

    public static RegistryObject<FlowingFluid> ethanol_fluid = FLUIDS.register("ethanol", () -> new ForgeFlowingFluid.Source(FluidEthanol.PROPS));
    public static RegistryObject<FlowingFluid> ethanol_fluid_flowing = FLUIDS.register("ethanol_flowing", () -> new ForgeFlowingFluid.Flowing(FluidEthanol.PROPS));
    public static RegistryObject<FlowingFluidBlock> ethanol_fluid_block = BLOCKS.register("ethanol_block",
            () -> new FlowingFluidBlock(ethanol_fluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
    public static RegistryObject<Item> ethanol_fluid_bucket = ITEMS.register("ethanol_bucket",
            () -> new BucketItem(ethanol_fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ModSetup.itemGroup)));

    public static RegistryObject<FlowingFluid> oil_fluid = FLUIDS.register("oil", () -> new ForgeFlowingFluid.Source(FluidOilProducts.OIL_PROPS));
    public static RegistryObject<FlowingFluid> oil_fluid_flowing = FLUIDS.register("oil_flowing", () -> new ForgeFlowingFluid.Flowing(FluidOilProducts.OIL_PROPS));
    public static RegistryObject<FlowingFluidBlock> oil_fluid_block = BLOCKS.register("oil_block",
            () -> new FlowingFluidBlock(oil_fluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
    public static RegistryObject<Item> oil_fluid_bucket = ITEMS.register("oil_bucket",
            () -> new BucketItem(oil_fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ModSetup.itemGroup)));

    public static RegistryObject<FlowingFluid> gasoline_fluid = FLUIDS.register("gasoline", () -> new ForgeFlowingFluid.Source(FluidOilProducts.GASOLINE_PROPS));
    public static RegistryObject<FlowingFluid> gasoline_fluid_flowing = FLUIDS.register("gasoline_flowing", () -> new ForgeFlowingFluid.Flowing(FluidOilProducts.GASOLINE_PROPS));
    public static RegistryObject<FlowingFluidBlock> gasoline_fluid_block = BLOCKS.register("gasoline_block",
            () -> new FlowingFluidBlock(gasoline_fluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
    public static RegistryObject<Item> gasoline_fluid_bucket = ITEMS.register("gasoline_bucket",
            () -> new BucketItem(gasoline_fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ModSetup.itemGroup)));

    public static RegistryObject<FlowingFluid> diesel_fluid = FLUIDS.register("diesel", () -> new ForgeFlowingFluid.Source(FluidOilProducts.DIESEL_PROPS));
    public static RegistryObject<FlowingFluid> diesel_fluid_flowing = FLUIDS.register("diesel_flowing", () -> new ForgeFlowingFluid.Flowing(FluidOilProducts.DIESEL_PROPS));
    public static RegistryObject<FlowingFluidBlock> diesel_fluid_block = BLOCKS.register("diesel_block",
            () -> new FlowingFluidBlock(diesel_fluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
    public static RegistryObject<Item> diesel_fluid_bucket = ITEMS.register("diesel_bucket",
            () -> new BucketItem(diesel_fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ModSetup.itemGroup)));

    public static RegistryObject<FlowingFluid> naphtha_fluid = FLUIDS.register("naphtha", () -> new ForgeFlowingFluid.Source(FluidOilProducts.NAPHTHA_PROPS));
    public static RegistryObject<FlowingFluid> naphtha_fluid_flowing = FLUIDS.register("naphtha_flowing", () -> new ForgeFlowingFluid.Flowing(FluidOilProducts.NAPHTHA_PROPS));
    public static RegistryObject<FlowingFluidBlock> naphtha_fluid_block = BLOCKS.register("naphtha_block",
            () -> new FlowingFluidBlock(naphtha_fluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
    public static RegistryObject<Item> naphtha_fluid_bucket = ITEMS.register("naphtha_bucket",
            () -> new BucketItem(naphtha_fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ModSetup.itemGroup)));

    public static RegistryObject<FlowingFluid> e50_fluid = FLUIDS.register("e50", () -> new ForgeFlowingFluid.Source(FluidOilProducts.E50_PROPS));
    public static RegistryObject<FlowingFluid> e50_fluid_flowing = FLUIDS.register("e50_flowing", () -> new ForgeFlowingFluid.Flowing(FluidOilProducts.E50_PROPS));
    public static RegistryObject<FlowingFluidBlock> e50_fluid_block = BLOCKS.register("e50_block",
            () -> new FlowingFluidBlock(e50_fluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
    public static RegistryObject<Item> e50_fluid_bucket = ITEMS.register("e50_bucket",
            () -> new BucketItem(e50_fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ModSetup.itemGroup)));

    public static void init(IEventBus eventBus){
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        FLUIDS.register(eventBus);
    }

    public static void createAndRegisterFluid(String name, ResourceLocation stillBlock, ResourceLocation flowingBlock, int color){
        /*
        ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties();

        RegistryObject<FlowingFluid> fluid = FLUIDS.register(name, () -> new ForgeFlowingFluid.Source(properties));
        RegistryObject<FlowingFluid> fluid_flowing = FLUIDS.register(name+"_flowing", () -> new ForgeFlowingFluid.Flowing(properties));
        RegistryObject<FlowingFluidBlock> fluid_block = BLOCKS.register(name+"_block",
                () -> new FlowingFluidBlock(fluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100f)));
        RegistryObject<Item> fluid_bucket = ITEMS.register(name+"_bucket",
                () -> new BucketItem(fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ModSetup.itemGroup)));

         */
    }
}
