package net.viniciusaportela.electriclights;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
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
import net.viniciusaportela.electriclights.block.electriclight.ElectricLightBlock;
import net.viniciusaportela.electriclights.block.electriclight.LightBulbBlock;
import net.viniciusaportela.electriclights.block.electriclight.TubularLightBlock;
import net.viniciusaportela.electriclights.config.ServerConfig;
import org.slf4j.Logger;

@Mod(ElectricLights.MODID)
public class ElectricLights {
    public static final String MODID = "electriclights";

    public static final Logger LOGGER = LogUtils.getLogger();

    private static final CreativeModeTab MOD_TAB = new CreativeModeTab("electriclightscreativetab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ELECTRIC_BLOCK.get());
        }
    };

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ElectricLights.MODID);

    public static final RegistryObject<Block> ELECTRIC_BLOCK = BLOCKS.register("electric_block", () -> new ElectricBlock(BlockBehaviour.Properties.of(Material.METAL).noOcclusion()));
    public static final RegistryObject<Item> ELECTRIC_BLOCK_ITEM = ITEMS.register("electric_block", () -> new BlockItem(ELECTRIC_BLOCK.get(), new Item.Properties().tab(MOD_TAB)));

    public static final RegistryObject<Block> LIGHT_BULB_BLOCK = BLOCKS.register("light_bulb", () -> new LightBulbBlock(BlockBehaviour.Properties.of(Material.METAL).lightLevel((state) -> state.getValue(ElectricLightBlock.ACTIVE) ? 15 : 0)));

    public static final RegistryObject<Item> LIGHT_BULB_BLOCK_ITEM = ITEMS.register("light_bulb", () -> new BlockItem(LIGHT_BULB_BLOCK.get(), new Item.Properties().tab(MOD_TAB)));

    public static final RegistryObject<Block> TUBULAR_LIGHT = BLOCKS.register("tubular_light", () -> new TubularLightBlock(BlockBehaviour.Properties.of(Material.METAL).lightLevel((state) -> state.getValue(ElectricLightBlock.ACTIVE) ? 15 : 0)));

    public static final RegistryObject<Item> TUBULAR_LIGHT_ITEM = ITEMS.register("tubular_light", () -> new BlockItem(TUBULAR_LIGHT.get(), new Item.Properties().tab(MOD_TAB)));

    public static final RegistryObject<BlockEntityType<ElectricBlockEntity>> ELECTRIC_BLOCK_ENTITY = TILE_ENTITIES.register("electric_block",
            () -> BlockEntityType.Builder.of(ElectricBlockEntity::new, ELECTRIC_BLOCK.get()).build(null));

    public static final TagKey<Block> ELECTRIC_BLOCK_CONNECTABLES  = BlockTags.create(new ResourceLocation(MODID, "electric_block_connectables"));

    public ElectricLights() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);

        ElectricLightsEventHandler electricLightsEventHandler = new ElectricLightsEventHandler();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(electricLightsEventHandler);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, "electriclights-server.toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info("Client setup");
            // Some client setup code
        }
    }
}
