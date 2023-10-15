package net.zelythia.aequitas;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.zelythia.aequitas.block.ConduitBlock;
import net.zelythia.aequitas.block.*;
import net.zelythia.aequitas.block.entity.CollectionBowlBlockEntity;
import net.zelythia.aequitas.block.entity.CraftingPedestalBlockEntity;
import net.zelythia.aequitas.block.entity.SamplingPedestalBlockEntity;
import net.zelythia.aequitas.item.ArmorMaterials;
import net.zelythia.aequitas.item.EssenceArmorItem;
import net.zelythia.aequitas.item.PortablePedestalItem;
import net.zelythia.aequitas.networking.EssencePacket;
import net.zelythia.aequitas.networking.NetworkingHandler;
import net.zelythia.aequitas.screen.CollectionBowlScreenHandler;
import net.zelythia.aequitas.screen.CraftingPedestalScreenHandler;
import net.zelythia.aequitas.screen.PortablePedestalScreenHandler;
import net.zelythia.aequitas.world.gen.EssencePillarFeature;
import net.zelythia.aequitas.world.gen.EssencePillarFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class Aequitas implements ModInitializer {

    public static final String MOD_ID = "aequitas";
    public static final Logger LOGGER = LogManager.getLogger("Aequitas");

    public static final ItemGroup ITEM_GROUP;
    public static final BooleanProperty ACTIVE_BLOCK_PROPERTY = BooleanProperty.of("active");


    public static final Block CONDUIT_BLOCK;
    public static final BlockItem CONDUIT_BLOCK_ITEM;

    public static final Block CATALYST_BLOCK_I;
    public static final Block CATALYST_BLOCK_II;
    public static final Block CATALYST_BLOCK_III;
    public static final BlockItem CATALYST_BLOCK_ITEM_I;
    public static final BlockItem CATALYST_BLOCK_ITEM_II;
    public static final BlockItem CATALYST_BLOCK_ITEM_III;

    public static final Item SAMPLING_PEDESTAL_CORE;
    public static final Item CRAFTING_PEDESTAL_CORE;
    public static final Item PRIMAL_ESSENCE;
    public static final Item PRIMORDIAL_ESSENCE;
    public static final Item PRISTINE_ESSENCE;

    public static final Block PRIMAL_ESSENCE_BLOCK;
    public static final Block PRIMORDIAL_ESSENCE_BLOCK;
    public static final Block PRISTINE_ESSENCE_BLOCK;
    public static final BlockItem PRIMAL_ESSENCE_BLOCK_ITEM;
    public static final BlockItem PRIMORDIAL_ESSENCE_BLOCK_ITEM;
    public static final BlockItem PRISTINE_ESSENCE_BLOCK_ITEM;


    public static final Identifier PEDESTAL = new Identifier(MOD_ID, "pedestal");
    public static final Block PEDESTAL_BLOCK;
    public static final BlockItem PEDESTAL_BLOCK_ITEM;

    public static final Identifier CRAFTING_PEDESTAL = new Identifier(MOD_ID, "crafting_pedestal");
    public static Block CRAFTING_PEDESTAL_BLOCK;
    public static final BlockItem CRAFTING_PEDESTAL_BLOCK_ITEM;
    public static final BlockEntityType<CraftingPedestalBlockEntity> CRAFTING_PEDESTAL_BLOCK_ENTITY;
    public static final ScreenHandlerType<CraftingPedestalScreenHandler> CRAFTING_PEDESTAL_SCREEN_HANDLER;

    public static final Identifier SAMPLING_PEDESTAL = new Identifier(MOD_ID, "sampling_pedestal");
    public static final Block SAMPLING_PEDESTAL_BLOCK;
    public static final BlockItem SAMPLING_PEDESTAL_BLOCK_ITEM;
    public static final BlockEntityType<SamplingPedestalBlockEntity> SAMPLING_PEDESTAL_BLOCK_ENTITY;

    public static final Block COLLECTION_BOWL_BLOCK_I;
    public static final Block COLLECTION_BOWL_BLOCK_II;
    public static final Block COLLECTION_BOWL_BLOCK_III;
    public static final BlockItem COLLECTION_BOWL_BLOCK_ITEM_I;
    public static final BlockItem COLLECTION_BOWL_BLOCK_ITEM_II;
    public static final BlockItem COLLECTION_BOWL_BLOCK_ITEM_III;

    public static final BlockEntityType<CollectionBowlBlockEntity> COLLECTION_BOWL_BLOCK_ENTITY_I;
    public static final BlockEntityType<CollectionBowlBlockEntity> COLLECTION_BOWL_BLOCK_ENTITY_II;
    public static final BlockEntityType<CollectionBowlBlockEntity> COLLECTION_BOWL_BLOCK_ENTITY_III;

    public static final ScreenHandlerType<CollectionBowlScreenHandler> COLLECTION_BOWL_SCREEN_HANDLER;


    public static final Identifier ESSENCE_PILLAR_FEATURE_ID = new Identifier(MOD_ID, "essence_pillar_feature");
    public static final Feature<EssencePillarFeatureConfig> ESSENCE_PILLAR_FEATURE;
    public static final ConfiguredFeature<EssencePillarFeatureConfig, EssencePillarFeature> CONFIGURED_ESSENCE_PILLAR_FEATURE;


    public static final Item PRIMAL_ESSENCE_HELMET;
    public static final Item PRIMAL_ESSENCE_CHESTPLATE;
    public static final Item PRIMAL_ESSENCE_LEGGINGS;
    public static final Item PRIMAL_ESSENCE_BOOTS;

    public static final Item PRIMORDIAL_ESSENCE_HELMET;
    public static final Item PRIMORDIAL_ESSENCE_CHESTPLATE;
    public static final Item PRIMORDIAL_ESSENCE_LEGGINGS;
    public static final Item PRIMORDIAL_ESSENCE_BOOTS;

    public static final Item PRISTINE_ESSENCE_HELMET;
    public static final Item PRISTINE_ESSENCE_CHESTPLATE;
    public static final Item PRISTINE_ESSENCE_LEGGINGS;
    public static final Item PRISTINE_ESSENCE_BOOTS;


    public static final Identifier PORTABLE_PEDESTAL = new Identifier(MOD_ID, "portable_pedestal");
    public static final Item PORTABLE_PEDESTAL_ITEM;
    public static final ScreenHandlerType<PortablePedestalScreenHandler> PORTABLE_PEDESTAL_SCREEN_HANDLER;


    static {
        ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "aequitas_group"), () -> new ItemStack(CRAFTING_PEDESTAL_BLOCK));

        //Blocks
        PRIMAL_ESSENCE_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "primal_essence_block"), new Block(AbstractBlock.Settings.of(Material.GLASS, MapColor.GREEN).strength(0.3F)));
        PRIMORDIAL_ESSENCE_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "primordial_essence_block"), new Block(AbstractBlock.Settings.of(Material.GLASS, MapColor.BLUE).strength(0.3F)));
        PRISTINE_ESSENCE_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "pristine_essence_block"), new Block(AbstractBlock.Settings.of(Material.GLASS, MapColor.WHITE).strength(0.3F)));
        CONDUIT_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "conduit_block"), new ConduitBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(1.8F).luminance((blockState) -> (Boolean)blockState.get(ACTIVE_BLOCK_PROPERTY) ? 13 : 0)));
        CATALYST_BLOCK_I = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "primal_catalyst"), new CatalystBlock(AbstractBlock.Settings.of(Material.GLASS, MapColor.GREEN).strength(0.3F).luminance((blockState) -> (Boolean)blockState.get(ACTIVE_BLOCK_PROPERTY) ? 15 : 0), 1));
        CATALYST_BLOCK_II = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "primordial_catalyst"), new CatalystBlock(AbstractBlock.Settings.of(Material.GLASS, MapColor.BLUE).strength(0.3F).luminance((blockState) -> (Boolean)blockState.get(ACTIVE_BLOCK_PROPERTY) ? 15 : 0), 2));
        CATALYST_BLOCK_III = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "pristine_catalyst"), new CatalystBlock(AbstractBlock.Settings.of(Material.GLASS, MapColor.WHITE).strength(0.3F).luminance((blockState) -> (Boolean)blockState.get(ACTIVE_BLOCK_PROPERTY) ? 15 : 0), 3));
        COLLECTION_BOWL_BLOCK_I = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "collection_bowl_1"), new CollectionBowlBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(1.8F),1));
        COLLECTION_BOWL_BLOCK_II = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "collection_bowl_2"), new CollectionBowlBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(1.8F),9));
        COLLECTION_BOWL_BLOCK_III = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "collection_bowl_3"), new CollectionBowlBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(1.8F),15));

        PEDESTAL_BLOCK = Registry.register(Registry.BLOCK, PEDESTAL, new PedestalBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(1.25F, 4.2F)));
        CRAFTING_PEDESTAL_BLOCK = Registry.register(Registry.BLOCK, CRAFTING_PEDESTAL, new CraftingPedestalBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(1.25F, 4.2F)));
        SAMPLING_PEDESTAL_BLOCK = Registry.register(Registry.BLOCK, SAMPLING_PEDESTAL, new SamplingPedestalBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(1.25F, 4.2F)));


        //Items
        PRIMAL_ESSENCE = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primal_essence"), new Item(new Item.Settings().group(ITEM_GROUP)));
        PRIMORDIAL_ESSENCE = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primordial_essence"), new Item(new Item.Settings().group(ITEM_GROUP)));
        PRISTINE_ESSENCE = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "pristine_essence"), new Item(new Item.Settings().group(ITEM_GROUP)));
        PRIMAL_ESSENCE_BLOCK_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primal_essence_block"), new BlockItem(PRIMAL_ESSENCE_BLOCK, new Item.Settings().group(ITEM_GROUP)));
        PRIMORDIAL_ESSENCE_BLOCK_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primordial_essence_block"), new BlockItem(PRIMORDIAL_ESSENCE_BLOCK, new Item.Settings().group(ITEM_GROUP)));
        PRISTINE_ESSENCE_BLOCK_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "pristine_essence_block"), new BlockItem(PRISTINE_ESSENCE_BLOCK, new Item.Settings().group(ITEM_GROUP)));

        CONDUIT_BLOCK_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "conduit_block"), new BlockItem(CONDUIT_BLOCK, new Item.Settings().group(ITEM_GROUP)));
        CATALYST_BLOCK_ITEM_I = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primal_catalyst"), new BlockItem(CATALYST_BLOCK_I, new Item.Settings().group(ITEM_GROUP)));
        CATALYST_BLOCK_ITEM_II = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primordial_catalyst"), new BlockItem(CATALYST_BLOCK_II, new Item.Settings().group(ITEM_GROUP)));
        CATALYST_BLOCK_ITEM_III = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "pristine_catalyst"), new BlockItem(CATALYST_BLOCK_III, new Item.Settings().group(ITEM_GROUP)));
        COLLECTION_BOWL_BLOCK_ITEM_I = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "collection_bowl_1"), new BlockItem(COLLECTION_BOWL_BLOCK_I, new Item.Settings().group(ITEM_GROUP)));
        COLLECTION_BOWL_BLOCK_ITEM_II = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "collection_bowl_2"), new BlockItem(COLLECTION_BOWL_BLOCK_II, new Item.Settings().group(ITEM_GROUP)));
        COLLECTION_BOWL_BLOCK_ITEM_III = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "collection_bowl_3"), new BlockItem(COLLECTION_BOWL_BLOCK_III, new Item.Settings().group(ITEM_GROUP)));

        SAMPLING_PEDESTAL_CORE = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "sampling_pedestal_core"), new Item(new Item.Settings().group(ITEM_GROUP)));
        CRAFTING_PEDESTAL_CORE = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "crafting_pedestal_core"), new Item(new Item.Settings().group(ITEM_GROUP)));

        PEDESTAL_BLOCK_ITEM = Registry.register(Registry.ITEM, PEDESTAL, new BlockItem(PEDESTAL_BLOCK, new Item.Settings().group(ITEM_GROUP)));
        CRAFTING_PEDESTAL_BLOCK_ITEM = Registry.register(Registry.ITEM, CRAFTING_PEDESTAL, new BlockItem(CRAFTING_PEDESTAL_BLOCK, new Item.Settings().group(ITEM_GROUP)));
        SAMPLING_PEDESTAL_BLOCK_ITEM = Registry.register(Registry.ITEM, SAMPLING_PEDESTAL, new BlockItem(SAMPLING_PEDESTAL_BLOCK, new Item.Settings().group(ITEM_GROUP)));

        PORTABLE_PEDESTAL_ITEM = Registry.register(Registry.ITEM, PORTABLE_PEDESTAL, new PortablePedestalItem(new Item.Settings().group(ITEM_GROUP).maxCount(1)));

        //Entities
        CRAFTING_PEDESTAL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, CRAFTING_PEDESTAL, BlockEntityType.Builder.create(CraftingPedestalBlockEntity::new, CRAFTING_PEDESTAL_BLOCK).build(null));
        SAMPLING_PEDESTAL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, SAMPLING_PEDESTAL, BlockEntityType.Builder.create(SamplingPedestalBlockEntity::new, SAMPLING_PEDESTAL_BLOCK).build(null));
        COLLECTION_BOWL_BLOCK_ENTITY_I = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("collection_bowl_1"), BlockEntityType.Builder.create(() -> new CollectionBowlBlockEntity(1), COLLECTION_BOWL_BLOCK_I).build(null));
        COLLECTION_BOWL_BLOCK_ENTITY_II = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("collection_bowl_2"), BlockEntityType.Builder.create(() -> new CollectionBowlBlockEntity(9), COLLECTION_BOWL_BLOCK_II).build(null));
        COLLECTION_BOWL_BLOCK_ENTITY_III = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("collection_bowl_3"), BlockEntityType.Builder.create(() -> new CollectionBowlBlockEntity(15), COLLECTION_BOWL_BLOCK_III).build(null));

        //Screens
        CRAFTING_PEDESTAL_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(CRAFTING_PEDESTAL, CraftingPedestalScreenHandler::new);
        COLLECTION_BOWL_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("collection_bowl"), CollectionBowlScreenHandler::new);
        PORTABLE_PEDESTAL_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(PORTABLE_PEDESTAL, PortablePedestalScreenHandler::new);


        ESSENCE_PILLAR_FEATURE = Registry.register(Registry.FEATURE, ESSENCE_PILLAR_FEATURE_ID, new EssencePillarFeature(EssencePillarFeatureConfig.CODEC));
        CONFIGURED_ESSENCE_PILLAR_FEATURE = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, ESSENCE_PILLAR_FEATURE_ID, (ConfiguredFeature)ESSENCE_PILLAR_FEATURE
                .configure(new EssencePillarFeatureConfig(10, Blocks.QUARTZ_BLOCK.getDefaultState()))
                .decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
                .applyChance(80)
        );

        PRIMAL_ESSENCE_HELMET = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primal_essence_helmet"), new ArmorItem(ArmorMaterials.PRIMAL, EquipmentSlot.HEAD, new Item.Settings().group(ITEM_GROUP)));
        PRIMAL_ESSENCE_CHESTPLATE = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primal_essence_chestplate"), new EssenceArmorItem(ArmorMaterials.PRIMAL, EquipmentSlot.CHEST, new Item.Settings().group(ITEM_GROUP)));
        PRIMAL_ESSENCE_LEGGINGS = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primal_essence_leggings"), new ArmorItem(ArmorMaterials.PRIMAL, EquipmentSlot.LEGS, new Item.Settings().group(ITEM_GROUP)));
        PRIMAL_ESSENCE_BOOTS = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primal_essence_boots"), new ArmorItem(ArmorMaterials.PRIMAL, EquipmentSlot.FEET, new Item.Settings().group(ITEM_GROUP)));

        PRIMORDIAL_ESSENCE_HELMET = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primordial_essence_helmet"), new ArmorItem(ArmorMaterials.PRIMORDIAL, EquipmentSlot.HEAD, new Item.Settings().group(ITEM_GROUP)));
        PRIMORDIAL_ESSENCE_CHESTPLATE = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primordial_essence_chestplate"), new EssenceArmorItem(ArmorMaterials.PRIMORDIAL, EquipmentSlot.CHEST, new Item.Settings().group(ITEM_GROUP)));
        PRIMORDIAL_ESSENCE_LEGGINGS = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primordial_essence_leggings"), new ArmorItem(ArmorMaterials.PRIMORDIAL, EquipmentSlot.LEGS, new Item.Settings().group(ITEM_GROUP)));
        PRIMORDIAL_ESSENCE_BOOTS = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "primordial_essence_boots"), new ArmorItem(ArmorMaterials.PRIMORDIAL, EquipmentSlot.FEET, new Item.Settings().group(ITEM_GROUP)));

        PRISTINE_ESSENCE_HELMET = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "pristine_essence_helmet"), new ArmorItem(ArmorMaterials.PRISTINE, EquipmentSlot.HEAD, new Item.Settings().group(ITEM_GROUP)));
        PRISTINE_ESSENCE_CHESTPLATE = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "pristine_essence_chestplate"), new EssenceArmorItem(ArmorMaterials.PRISTINE, EquipmentSlot.CHEST, new Item.Settings().group(ITEM_GROUP)));
        PRISTINE_ESSENCE_LEGGINGS = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "pristine_essence_leggings"), new ArmorItem(ArmorMaterials.PRISTINE, EquipmentSlot.LEGS, new Item.Settings().group(ITEM_GROUP)));
        PRISTINE_ESSENCE_BOOTS = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "pristine_essence_boots"), new ArmorItem(ArmorMaterials.PRISTINE, EquipmentSlot.FEET, new Item.Settings().group(ITEM_GROUP)));
    }

    @Override
    public void onInitialize() {
        NetworkingHandler.onInitialize();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ResourceLoader());

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            NetworkingHandler.setServer(server);
            EssenceHandler.registerRecipeManager(server.getRecipeManager());
            EssenceHandler.reloadEssenceValues(new HashMap<>());
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PacketByteBuf buf = PacketByteBufs.create();
            EssencePacket.encode(buf);

            sender.sendPacket(NetworkingHandler.ESSENCE_UPDATE, buf);
        });

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.SURFACE_STRUCTURES,
                RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, ESSENCE_PILLAR_FEATURE_ID)
        );


        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if(id.toString().startsWith("minecraft:blocks") || id.toString().startsWith("minecraft:entities")) return;
            if(id.equals(LootTables.DESERT_PYRAMID_CHEST) || id.equals(LootTables.SHIPWRECK_TREASURE_CHEST)){
                LootPool.Builder poolBuilder = LootPool.builder()
                    .with(ItemEntry.builder(Aequitas.PRIMAL_ESSENCE).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1, 4))).weight(4))
                    .with(ItemEntry.builder(Aequitas.PRIMORDIAL_ESSENCE).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1, 4))).weight(2))
                    .with(ItemEntry.builder(Aequitas.PRISTINE_ESSENCE).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1, 4))).weight(1))
                    .with(EmptyEntry.Serializer().weight(7));

                supplier.pool(poolBuilder);
            }
        });
    }
}
