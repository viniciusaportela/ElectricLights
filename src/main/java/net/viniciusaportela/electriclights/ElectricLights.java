package net.viniciusaportela.electriclights;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.viniciusaportela.electriclights.block.electricblock.ElectricBlock;
import net.viniciusaportela.electriclights.block.electricblock.ElectricBlockEntity;
import net.viniciusaportela.electriclights.block.electricblock.ElectricBlockEventHandler;
import net.viniciusaportela.electriclights.block.electriclight.ElectricLightBlock;
import net.viniciusaportela.electriclights.config.ServerConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ElectricLights.MODID)
public class ElectricLights {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "electriclights";
    // Directly reference a slf4j logger

    public static final Logger LOGGER = LogUtils.getLogger();

    private static final CreativeModeTab MOD_TAB = new CreativeModeTab("creativemodtab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ELECTRIC_BLOCK.get());
        }
    };

    // Create a Deferred Register to hold Blocks which will all be registered under the "electric_lights" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "electric_lights" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ElectricLights.MODID);

    // Creates a new Block with the id "electric_lights:example_block", combining the namespace and path
    public static final RegistryObject<Block> ELECTRIC_BLOCK = BLOCKS.register("electric_block", () -> new ElectricBlock(BlockBehaviour.Properties.of(Material.METAL)));
    // Creates a new BlockItem with the id "electric_lights:example_block", combining the namespace and path
    public static final RegistryObject<Item> ELECTRIC_BLOCK_ITEM = ITEMS.register("electric_block", () -> new BlockItem(ELECTRIC_BLOCK.get(), new Item.Properties().tab(MOD_TAB)));

    public static final RegistryObject<Block> ELECTRIC_LIGHT_BLOCK = BLOCKS.register("electric_light", () -> new ElectricLightBlock(BlockBehaviour.Properties.of(Material.METAL).lightLevel((state) -> state.getValue(ElectricLightBlock.ACTIVE) ? 15 : 0)));

    public static final RegistryObject<Item> ELECTRIC_LIGHT_ITEM = ITEMS.register("electric_light", () -> new BlockItem(ELECTRIC_LIGHT_BLOCK.get(), new Item.Properties().tab(MOD_TAB)));

    public static final RegistryObject<BlockEntityType<ElectricBlockEntity>> ELECTRIC_BLOCK_ENTITY = TILE_ENTITIES.register("electric_block",
            () -> BlockEntityType.Builder.of(ElectricBlockEntity::new, ELECTRIC_BLOCK.get()).build(null));

    public ElectricLights() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);

        ElectricBlockEventHandler electricBlockEventHandler = new ElectricBlockEventHandler();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(electricBlockEventHandler);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, "electriclights-server.toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
        }
    }
}
