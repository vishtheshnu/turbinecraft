package com.vnator.turbinecraft;

import com.vnator.turbinecraft.blocks.FirstBlock;
import com.vnator.turbinecraft.fluids.FluidRegistration;
import com.vnator.turbinecraft.recipes.AlloyerRecipe;
import com.vnator.turbinecraft.setup.*;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

import static com.vnator.turbinecraft.TurbineCraft.MOD_ID;

@Mod("turbinecraft")
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TurbineCraft {

    public static final String MOD_ID = "turbinecraft";
    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    public static ModSetup setup = new ModSetup();

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public TurbineCraft() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FluidRegistration.init(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event) {
        setup.init();
        proxy.init();
    }

    @SubscribeEvent
    public static void registerRecipeSerializer(@Nonnull final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        AlloyerRecipe.Serializer serializer = new AlloyerRecipe.Serializer(AlloyerRecipe::new);
        serializer.setRegistryName("alloyer");
        event.getRegistry().register(serializer);
    }

    @ObjectHolder(MOD_ID)
    public static class Recipes{
        public static final IRecipeSerializer<AlloyerRecipe> alloyer = null;
    }
}
