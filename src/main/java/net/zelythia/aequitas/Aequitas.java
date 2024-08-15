package net.zelythia.aequitas;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.Feature;
import net.zelythia.aequitas.advancement.PlayerStatistics;
import net.zelythia.aequitas.block.AequitasBlocks;
import net.zelythia.aequitas.item.AequitasItems;
import net.zelythia.aequitas.networking.EssencePacket;
import net.zelythia.aequitas.networking.NetworkingHandler;
import net.zelythia.aequitas.screen.CollectionBowlScreenHandler;
import net.zelythia.aequitas.screen.CraftingPedestalScreenHandler;
import net.zelythia.aequitas.screen.PortablePedestalScreenHandler;
import net.zelythia.aequitas.world.gen.EssencePillarFeature;
import net.zelythia.aequitas.world.gen.EssencePillarFeatureConfig;
import net.zelythia.aequitas.world.gen.PlacedFeatures;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class Aequitas implements ModInitializer {

    public static final String MOD_ID = "aequitas";
    public static final Logger LOGGER = LogManager.getLogger("Aequitas");


    public static final ScreenHandlerType<CraftingPedestalScreenHandler> CRAFTING_PEDESTAL_SCREEN_HANDLER;
    public static final ScreenHandlerType<PortablePedestalScreenHandler> PORTABLE_PEDESTAL_SCREEN_HANDLER;
    public static final ScreenHandlerType<CollectionBowlScreenHandler> COLLECTION_BOWL_SCREEN_HANDLER;

    public static final Feature<EssencePillarFeatureConfig> ESSENCE_PILLAR_FEATURE;

    static {
        //Screens
        CRAFTING_PEDESTAL_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, "crafting_pedestal"), new ScreenHandlerType<>(CraftingPedestalScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
        PORTABLE_PEDESTAL_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, "portable_pedestal"), new ScreenHandlerType<>(PortablePedestalScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
        COLLECTION_BOWL_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, "collection_bowl"), new ExtendedScreenHandlerType<>(CollectionBowlScreenHandler::new));

        ESSENCE_PILLAR_FEATURE = Registry.register(Registries.FEATURE, new Identifier(MOD_ID, "essence_pillar_feature"), new EssencePillarFeature(EssencePillarFeatureConfig.CODEC));
    }


    @Override
    public void onInitialize() {
        new AequitasItems();
        new AequitasBlocks();

        PlayerStatistics.register();
        NetworkingHandler.onInitialize();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ResourceLoader());

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            NetworkingHandler.setServer(server);
            EssenceHandler.registerRecipeManager(server.getRecipeManager());
            EssenceHandler.registerRegistryManager(server.getRegistryManager());
            EssenceHandler.reloadEssenceValues(new HashMap<>());
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PacketByteBuf buf = PacketByteBufs.create();
            EssencePacket.encode(buf);

            sender.sendPacket(NetworkingHandler.ESSENCE_UPDATE, buf);
        });

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.SURFACE_STRUCTURES, PlacedFeatures.ESSENCE_PILLAR);

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (id.toString().startsWith("minecraft:blocks") || id.toString().startsWith("minecraft:entities")) return;
            if (id.equals(LootTables.DESERT_PYRAMID_CHEST) || id.equals(LootTables.SHIPWRECK_TREASURE_CHEST)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(AequitasItems.PRIMAL_ESSENCE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))).weight(4))
                        .with(ItemEntry.builder(AequitasItems.PRIMORDIAL_ESSENCE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))).weight(2))
                        .with(ItemEntry.builder(AequitasItems.PRISTINE_ESSENCE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))).weight(1))
                        .with(EmptyEntry.builder().weight(7));

                tableBuilder.pool(poolBuilder);
            }

            //TODO: Add mod items to collection bowl loot tables
        });
    }
}
