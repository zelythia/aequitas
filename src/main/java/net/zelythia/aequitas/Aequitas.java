package net.zelythia.aequitas;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.Feature;
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
import net.zelythia.aequitas.world.gen.PlacedFeatures;
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
    public static final Item PORTABLE_PEDESTAL_CORE;
    public static final Item PRIMAL_ESSENCE;
    public static final Item PRIMORDIAL_ESSENCE;
    public static final Item PRISTINE_ESSENCE;

    public static final Block PRIMAL_ESSENCE_BLOCK;
    public static final Block PRIMORDIAL_ESSENCE_BLOCK;
    public static final Block PRISTINE_ESSENCE_BLOCK;
    public static final BlockItem PRIMAL_ESSENCE_BLOCK_ITEM;
    public static final BlockItem PRIMORDIAL_ESSENCE_BLOCK_ITEM;
    public static final BlockItem PRISTINE_ESSENCE_BLOCK_ITEM;

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

    public static final Identifier PORTABLE_PEDESTAL = new Identifier(MOD_ID, "portable_pedestal");
    public static final Item PORTABLE_PEDESTAL_ITEM;
    public static final ScreenHandlerType<PortablePedestalScreenHandler> PORTABLE_PEDESTAL_SCREEN_HANDLER;

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


    static {

        //Blocks
        PRIMAL_ESSENCE_BLOCK = Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "primal_essence_block"), new Block(AbstractBlock.Settings.create().mapColor(MapColor.GREEN).sounds(BlockSoundGroup.GLASS).strength(0.3F)));
        PRIMORDIAL_ESSENCE_BLOCK = Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "primordial_essence_block"), new Block(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).sounds(BlockSoundGroup.GLASS).strength(0.3F)));
        PRISTINE_ESSENCE_BLOCK = Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "pristine_essence_block"), new Block(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).sounds(BlockSoundGroup.GLASS).strength(0.3F)));
        CONDUIT_BLOCK = Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "conduit_block"), new ConduitBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).sounds(BlockSoundGroup.STONE).requiresTool().strength(1.8F).luminance((blockState) -> (Boolean) blockState.get(ACTIVE_BLOCK_PROPERTY) ? 13 : 0)));
        CATALYST_BLOCK_I = Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "primal_catalyst"), new CatalystBlock(AbstractBlock.Settings.create().mapColor(MapColor.GREEN).sounds(BlockSoundGroup.GLASS).strength(0.3F).luminance((blockState) -> (Boolean) blockState.get(ACTIVE_BLOCK_PROPERTY) ? 15 : 0), 1));
        CATALYST_BLOCK_II = Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "primordial_catalyst"), new CatalystBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).sounds(BlockSoundGroup.GLASS).luminance((blockState) -> (Boolean) blockState.get(ACTIVE_BLOCK_PROPERTY) ? 15 : 0), 2));
        CATALYST_BLOCK_III = Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "pristine_catalyst"), new CatalystBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).sounds(BlockSoundGroup.GLASS).luminance((blockState) -> (Boolean) blockState.get(ACTIVE_BLOCK_PROPERTY) ? 15 : 0), 3));
        COLLECTION_BOWL_BLOCK_I = Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "collection_bowl_1"), new CollectionBowlBlock(AbstractBlock.Settings.create().requiresTool().strength(1.8F), 1));
        COLLECTION_BOWL_BLOCK_II = Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "collection_bowl_2"), new CollectionBowlBlock(AbstractBlock.Settings.create().requiresTool().strength(1.8F), 9));
        COLLECTION_BOWL_BLOCK_III = Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "collection_bowl_3"), new CollectionBowlBlock(AbstractBlock.Settings.create().requiresTool().strength(1.8F), 15));

        PEDESTAL_BLOCK = Registry.register(Registries.BLOCK, PEDESTAL, new PedestalBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(1.25F, 4.2F)));
        CRAFTING_PEDESTAL_BLOCK = Registry.register(Registries.BLOCK, CRAFTING_PEDESTAL, new CraftingPedestalBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(1.25F, 4.2F)));
        SAMPLING_PEDESTAL_BLOCK = Registry.register(Registries.BLOCK, SAMPLING_PEDESTAL, new SamplingPedestalBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(1.25F, 4.2F)));


        //Items
        PRIMAL_ESSENCE = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primal_essence"), new Item(new Item.Settings()));
        PRIMORDIAL_ESSENCE = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primordial_essence"), new Item(new Item.Settings()));
        PRISTINE_ESSENCE = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "pristine_essence"), new Item(new Item.Settings()));
        PRIMAL_ESSENCE_BLOCK_ITEM = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primal_essence_block"), new BlockItem(PRIMAL_ESSENCE_BLOCK, new Item.Settings()));
        PRIMORDIAL_ESSENCE_BLOCK_ITEM = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primordial_essence_block"), new BlockItem(PRIMORDIAL_ESSENCE_BLOCK, new Item.Settings()));
        PRISTINE_ESSENCE_BLOCK_ITEM = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "pristine_essence_block"), new BlockItem(PRISTINE_ESSENCE_BLOCK, new Item.Settings()));

        CONDUIT_BLOCK_ITEM = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "conduit_block"), new BlockItem(CONDUIT_BLOCK, new Item.Settings()));
        CATALYST_BLOCK_ITEM_I = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primal_catalyst"), new BlockItem(CATALYST_BLOCK_I, new Item.Settings()));
        CATALYST_BLOCK_ITEM_II = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primordial_catalyst"), new BlockItem(CATALYST_BLOCK_II, new Item.Settings()));
        CATALYST_BLOCK_ITEM_III = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "pristine_catalyst"), new BlockItem(CATALYST_BLOCK_III, new Item.Settings()));
        COLLECTION_BOWL_BLOCK_ITEM_I = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "collection_bowl_1"), new BlockItem(COLLECTION_BOWL_BLOCK_I, new Item.Settings()));
        COLLECTION_BOWL_BLOCK_ITEM_II = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "collection_bowl_2"), new BlockItem(COLLECTION_BOWL_BLOCK_II, new Item.Settings()));
        COLLECTION_BOWL_BLOCK_ITEM_III = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "collection_bowl_3"), new BlockItem(COLLECTION_BOWL_BLOCK_III, new Item.Settings()));

        SAMPLING_PEDESTAL_CORE = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "sampling_pedestal_core"), new Item(new Item.Settings()));
        CRAFTING_PEDESTAL_CORE = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "crafting_pedestal_core"), new Item(new Item.Settings()));
        PORTABLE_PEDESTAL_CORE = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "portable_pedestal_core"), new Item(new Item.Settings()));

        PEDESTAL_BLOCK_ITEM = Registry.register(Registries.ITEM, PEDESTAL, new BlockItem(PEDESTAL_BLOCK, new Item.Settings()));
        CRAFTING_PEDESTAL_BLOCK_ITEM = Registry.register(Registries.ITEM, CRAFTING_PEDESTAL, new BlockItem(CRAFTING_PEDESTAL_BLOCK, new Item.Settings()));
        SAMPLING_PEDESTAL_BLOCK_ITEM = Registry.register(Registries.ITEM, SAMPLING_PEDESTAL, new BlockItem(SAMPLING_PEDESTAL_BLOCK, new Item.Settings()));
        PORTABLE_PEDESTAL_ITEM = Registry.register(Registries.ITEM, PORTABLE_PEDESTAL, new PortablePedestalItem(new Item.Settings().maxCount(1)));

        PRIMAL_ESSENCE_HELMET = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primal_essence_helmet"), new EssenceArmorItem(ArmorMaterials.PRIMAL, ArmorItem.Type.HELMET, new Item.Settings()));
        PRIMAL_ESSENCE_CHESTPLATE = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primal_essence_chestplate"), new EssenceArmorItem(ArmorMaterials.PRIMAL, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
        PRIMAL_ESSENCE_LEGGINGS = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primal_essence_leggings"), new ArmorItem(ArmorMaterials.PRIMAL, ArmorItem.Type.LEGGINGS, new Item.Settings()));
        PRIMAL_ESSENCE_BOOTS = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primal_essence_boots"), new ArmorItem(ArmorMaterials.PRIMAL, ArmorItem.Type.BOOTS, new Item.Settings()));

        PRIMORDIAL_ESSENCE_HELMET = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primordial_essence_helmet"), new EssenceArmorItem(ArmorMaterials.PRIMORDIAL, ArmorItem.Type.HELMET, new Item.Settings()));
        PRIMORDIAL_ESSENCE_CHESTPLATE = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primordial_essence_chestplate"), new EssenceArmorItem(ArmorMaterials.PRIMORDIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
        PRIMORDIAL_ESSENCE_LEGGINGS = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primordial_essence_leggings"), new ArmorItem(ArmorMaterials.PRIMORDIAL, ArmorItem.Type.LEGGINGS, new Item.Settings()));
        PRIMORDIAL_ESSENCE_BOOTS = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "primordial_essence_boots"), new ArmorItem(ArmorMaterials.PRIMORDIAL, ArmorItem.Type.BOOTS, new Item.Settings()));

        PRISTINE_ESSENCE_HELMET = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "pristine_essence_helmet"), new EssenceArmorItem(ArmorMaterials.PRISTINE, ArmorItem.Type.HELMET, new Item.Settings()));
        PRISTINE_ESSENCE_CHESTPLATE = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "pristine_essence_chestplate"), new EssenceArmorItem(ArmorMaterials.PRISTINE, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
        PRISTINE_ESSENCE_LEGGINGS = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "pristine_essence_leggings"), new ArmorItem(ArmorMaterials.PRISTINE, ArmorItem.Type.LEGGINGS, new Item.Settings()));
        PRISTINE_ESSENCE_BOOTS = Registry.register(Registries.ITEM, new Identifier(MOD_ID, "pristine_essence_boots"), new ArmorItem(ArmorMaterials.PRISTINE, ArmorItem.Type.BOOTS, new Item.Settings()));


        //Entities
        CRAFTING_PEDESTAL_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, CRAFTING_PEDESTAL, FabricBlockEntityTypeBuilder.create(CraftingPedestalBlockEntity::new, CRAFTING_PEDESTAL_BLOCK).build());
        SAMPLING_PEDESTAL_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, SAMPLING_PEDESTAL, FabricBlockEntityTypeBuilder.create(SamplingPedestalBlockEntity::new, SAMPLING_PEDESTAL_BLOCK).build());
        COLLECTION_BOWL_BLOCK_ENTITY_I = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("collection_bowl_1"), FabricBlockEntityTypeBuilder.create((pos, state) -> new CollectionBowlBlockEntity(pos, state, 1), COLLECTION_BOWL_BLOCK_I).build());
        COLLECTION_BOWL_BLOCK_ENTITY_II = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("collection_bowl_2"), FabricBlockEntityTypeBuilder.create((pos, state) -> new CollectionBowlBlockEntity(pos, state, 9), COLLECTION_BOWL_BLOCK_II).build());
        COLLECTION_BOWL_BLOCK_ENTITY_III = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("collection_bowl_3"), FabricBlockEntityTypeBuilder.create((pos, state) -> new CollectionBowlBlockEntity(pos, state, 15), COLLECTION_BOWL_BLOCK_III).build());

        //Screens
        CRAFTING_PEDESTAL_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(CRAFTING_PEDESTAL, CraftingPedestalScreenHandler::new);
        COLLECTION_BOWL_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier("collection_bowl"), CollectionBowlScreenHandler::new);
        PORTABLE_PEDESTAL_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(PORTABLE_PEDESTAL, PortablePedestalScreenHandler::new);


        ESSENCE_PILLAR_FEATURE = Registry.register(Registries.FEATURE, ESSENCE_PILLAR_FEATURE_ID, new EssencePillarFeature(EssencePillarFeatureConfig.CODEC));


        ITEM_GROUP = FabricItemGroup.builder()
                .icon(() -> new ItemStack(CRAFTING_PEDESTAL_BLOCK))
                .displayName(Text.translatable("itemGroup.aequitas.item_group"))
                .entries((context, entries) -> {
                    entries.add(PRIMAL_ESSENCE);
                    entries.add(PRIMORDIAL_ESSENCE);
                    entries.add(PRISTINE_ESSENCE);
                    entries.add(PRIMAL_ESSENCE_BLOCK_ITEM);
                    entries.add(PRIMORDIAL_ESSENCE_BLOCK_ITEM);
                    entries.add(PRISTINE_ESSENCE_BLOCK_ITEM);

                    entries.add(CONDUIT_BLOCK_ITEM);
                    entries.add(CATALYST_BLOCK_ITEM_I);
                    entries.add(CATALYST_BLOCK_ITEM_II);
                    entries.add(CATALYST_BLOCK_ITEM_III);
                    entries.add(COLLECTION_BOWL_BLOCK_ITEM_I);
                    entries.add(COLLECTION_BOWL_BLOCK_ITEM_II);
                    entries.add(COLLECTION_BOWL_BLOCK_ITEM_III);

                    entries.add(SAMPLING_PEDESTAL_CORE);
                    entries.add(CRAFTING_PEDESTAL_CORE);
                    entries.add(PORTABLE_PEDESTAL_CORE);

                    entries.add(PEDESTAL_BLOCK_ITEM);
                    entries.add(CRAFTING_PEDESTAL_BLOCK_ITEM);
                    entries.add(SAMPLING_PEDESTAL_BLOCK_ITEM);
                    entries.add(PORTABLE_PEDESTAL_ITEM);

                    entries.add(PRIMAL_ESSENCE_HELMET);
                    entries.add(PRIMAL_ESSENCE_CHESTPLATE);
                    entries.add(PRIMAL_ESSENCE_LEGGINGS);
                    entries.add(PRIMAL_ESSENCE_BOOTS);
                    entries.add(PRIMORDIAL_ESSENCE_HELMET);
                    entries.add(PRIMORDIAL_ESSENCE_CHESTPLATE);
                    entries.add(PRIMORDIAL_ESSENCE_LEGGINGS);
                    entries.add(PRIMORDIAL_ESSENCE_BOOTS);
                    entries.add(PRISTINE_ESSENCE_HELMET);
                    entries.add(PRISTINE_ESSENCE_CHESTPLATE);
                    entries.add(PRISTINE_ESSENCE_LEGGINGS);
                    entries.add(PRISTINE_ESSENCE_BOOTS);
                }).build();
    }

    @Override
    public void onInitialize() {
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

        Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, "aequitas_item_group"), ITEM_GROUP);

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.SURFACE_STRUCTURES, PlacedFeatures.ESSENCE_PILLAR);

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (id.toString().startsWith("minecraft:blocks") || id.toString().startsWith("minecraft:entities")) return;
            if (id.equals(LootTables.DESERT_PYRAMID_CHEST) || id.equals(LootTables.SHIPWRECK_TREASURE_CHEST)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(Aequitas.PRIMAL_ESSENCE).apply(SetCountLootFunction.builder(new UniformLootNumberProvider(new ConstantLootNumberProvider(1),new ConstantLootNumberProvider(4)))).weight(4))
                        .with(ItemEntry.builder(Aequitas.PRIMORDIAL_ESSENCE).apply(SetCountLootFunction.builder(new UniformLootNumberProvider(new ConstantLootNumberProvider(1),new ConstantLootNumberProvider(4)))).weight(2))
                        .with(ItemEntry.builder(Aequitas.PRISTINE_ESSENCE).apply(SetCountLootFunction.builder(new UniformLootNumberProvider(new ConstantLootNumberProvider(1),new ConstantLootNumberProvider(4)))).weight(1))
                        .with(EmptyEntry.builder().weight(7));

                tableBuilder.pool(poolBuilder);
            }
        });
    }
}
