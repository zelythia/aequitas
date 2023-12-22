package net.zelythia.aequitas.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionOptions;

import java.util.function.BiConsumer;

public class LootTableProvider extends FabricBlockLootTableProvider {
    public LootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {

    }


    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {

        biConsumer.accept(new Identifier("aequitas", "gameplay/nether"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))


                )
        );

        //Dimension loot tables
        biConsumer.accept(new Identifier("aequitas", "gameplay/nether"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.NETHERRACK).weight(50).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))))
                        .with(ItemEntry.builder(Items.NETHER_QUARTZ_ORE).weight(10))
                        .with(ItemEntry.builder(Items.NETHER_GOLD_ORE).weight(10))
                        .with(ItemEntry.builder(Items.SOUL_SAND).weight(20))
                        .with(ItemEntry.builder(Items.SOUL_SOIL).weight(15))
                        .with(ItemEntry.builder(Items.BASALT).weight(20))
                        .with(ItemEntry.builder(Items.NETHER_BRICKS).weight(15))
                        .with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(1))
                        .with(ItemEntry.builder(Items.CRYING_OBSIDIAN).weight(5))
                        .with(ItemEntry.builder(Items.BLACKSTONE).weight(15))
                        .with(ItemEntry.builder(Items.GILDED_BLACKSTONE).weight(10))
                        .with(ItemEntry.builder(Items.WARPED_STEM).weight(20))
                        .with(ItemEntry.builder(Items.CRIMSON_STEM).weight(20))
                        .with(ItemEntry.builder(Items.CRIMSON_FUNGUS).weight(10))
                        .with(ItemEntry.builder(Items.WARPED_FUNGUS).weight(10))
                        .with(ItemEntry.builder(Items.WEEPING_VINES).weight(10))
                        .with(ItemEntry.builder(Items.TWISTING_VINES).weight(10))
                        .with(ItemEntry.builder(Items.SHROOMLIGHT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2))))
                        .with(ItemEntry.builder(Items.CRIMSON_NYLIUM).weight(10))
                        .with(ItemEntry.builder(Items.WARPED_NYLIUM).weight(10))
                        .with(ItemEntry.builder(Items.MAGMA_BLOCK).weight(10))
                        .with(ItemEntry.builder(Items.NETHER_WART_BLOCK).weight(10))
                        .with(ItemEntry.builder(Items.WARPED_WART_BLOCK).weight(10))
                        .with(ItemEntry.builder(Items.NETHER_WART).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 8))))
                        .with(ItemEntry.builder(Items.WITHER_SKELETON_SKULL).weight(1))
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/end"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.END_STONE).weight(30))
                        .with(ItemEntry.builder(Items.ENDER_PEARL).weight(10))
                        .with(ItemEntry.builder(Items.END_CRYSTAL).weight(1))
                        .with(ItemEntry.builder(Items.OBSIDIAN).weight(5))
                        .with(ItemEntry.builder(Items.CHORUS_PLANT).weight(7))
                        .with(ItemEntry.builder(Items.CHORUS_FRUIT).weight(10))
                        .with(ItemEntry.builder(Items.PURPUR_BLOCK).weight(20))
                        .with(ItemEntry.builder(Items.SHULKER_SHELL).weight(3))
                )
        );

        //Will always generate an item //FIXME
        biConsumer.accept(new Identifier("aequitas", "gameplay/overworld"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        //FIXME
                        .with(EmptyEntry.builder().weight(50))
                        .with(ItemEntry.builder(Items.DIRT).weight(1)) //Only the dirt that is found underground
                        .with(ItemEntry.builder(Items.STONE).weight(30))
                        .with(ItemEntry.builder(Items.ANDESITE).weight(1))
                        .with(ItemEntry.builder(Items.DIORITE).weight(1))
                        .with(ItemEntry.builder(Items.GRANITE).weight(1))
                        .with(ItemEntry.builder(Items.GRAVEL).weight(1))
                        .with(ItemEntry.builder(Items.FLINT).weight(1))


                        .with(ItemEntry.builder(Items.COAL).weight(8).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))

                        .with(ItemEntry.builder(Items.COPPER_ORE).weight(4))
                        .with(ItemEntry.builder(Items.DEEPSLATE_COPPER_ORE).weight(4))

                        .with(ItemEntry.builder(Items.IRON_ORE).weight(4))
                        .with(ItemEntry.builder(Items.DEEPSLATE_IRON_ORE).weight(4))

                        .with(ItemEntry.builder(Items.GOLD_ORE).weight(4))
                        .with(ItemEntry.builder(Items.DEEPSLATE_GOLD_ORE).weight(4))

                        .with(ItemEntry.builder(Items.DIAMOND_ORE).weight(1))
                        .with(ItemEntry.builder(Items.DEEPSLATE_DIAMOND_ORE).weight(1))

                        .with(ItemEntry.builder(Items.REDSTONE_ORE).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,5))))
                        .with(ItemEntry.builder(Items.DEEPSLATE_REDSTONE_ORE).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,5))))

                        .with(ItemEntry.builder(Items.LAPIS_ORE).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,5))))
                        .with(ItemEntry.builder(Items.DEEPSLATE_LAPIS_ORE).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,5))))

                        .with(ItemEntry.builder(Items.AMETHYST_CLUSTER).weight(1))


//                        .conditionally(InvertedLootCondition.builder(LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.DEEP_DARK))))
                )
        );




        //Biome loot tables
        biConsumer.accept(new Identifier("aequitas", "gameplay/ocean"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.SEAGRASS).weight(30))
                        .with(ItemEntry.builder(Items.KELP).weight(30).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,5))))
                        .with(ItemEntry.builder(Items.SEA_PICKLE).weight(20))
                        .with(ItemEntry.builder(Items.PRISMARINE_SHARD).weight(30).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.PRISMARINE_CRYSTALS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3))))
                        .with(ItemEntry.builder(Items.SPONGE).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3))))
                        .with(ItemEntry.builder(Items.NAUTILUS_SHELL).weight(10))
                        .with(ItemEntry.builder(Items.GOLD_INGOT).weight(10))
                        .with(ItemEntry.builder(Items.SCUTE).weight(5))
                        .with(ItemEntry.builder(Items.TURTLE_EGG).weight(5))
                        .with(ItemEntry.builder(Items.CLAY_BALL).weight(30).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.PUFFERFISH_BUCKET).weight(1))
                        .with(ItemEntry.builder(Items.SALMON_BUCKET).weight(1))
                        .with(ItemEntry.builder(Items.COD_BUCKET).weight(1))
                        .with(ItemEntry.builder(Items.TROPICAL_FISH_BUCKET).weight(1))
                        .with(ItemEntry.builder(Items.TRIDENT).weight(1))
                        .with(ItemEntry.builder(Items.HEART_OF_THE_SEA).weight(1))

                        .with(ItemEntry.builder(Items.SAND).weight(30))
                        .with(ItemEntry.builder(Items.GRAVEL).weight(30))
                        .with(ItemEntry.builder(Items.DIRT).weight(20))
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/desert"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.SAND).weight(100).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.SANDSTONE).weight(70))
                        .with(ItemEntry.builder(Items.CACTUS).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3))))
                        .with(ItemEntry.builder(Items.DEAD_BUSH).weight(20))
                        .with(ItemEntry.builder(Items.BONE_BLOCK).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.RABBIT_HIDE).weight(5))
                        .with(ItemEntry.builder(Items.RABBIT_FOOT).weight(5))
                        .with(ItemEntry.builder(Items.RABBIT).weight(5))

//                        .with(ItemEntry.builder(Items.STONE).weight(50))
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/swamp"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.VINE).weight(3))
                        .with(ItemEntry.builder(Items.LILY_PAD).weight(3))
                        .with(ItemEntry.builder(Items.BLUE_ORCHID).weight(1))
                        .with(ItemEntry.builder(Items.SLIME_BALL).weight(10))
                        .with(ItemEntry.builder(Items.CLAY).weight(10))
                        .with(ItemEntry.builder(Items.RED_MUSHROOM).weight(1))
                        .with(ItemEntry.builder(Items.BROWN_MUSHROOM).weight(1))
                        //TODO oak_sapling, grass_block
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/mangrove"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.MANGROVE_LOG).weight(20))
                        .with(ItemEntry.builder(Items.MANGROVE_LEAVES).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.MANGROVE_PROPAGULE).weight(5))
                        .with(ItemEntry.builder(Items.MOSS_CARPET).weight(3))
                        .with(ItemEntry.builder(Items.MUDDY_MANGROVE_ROOTS).weight(5))
                        .with(ItemEntry.builder(Items.VINE).weight(3))
                        .with(ItemEntry.builder(Items.BEE_NEST).weight(1))
                        .with(ItemEntry.builder(Items.MUD).weight(10))
                        .with(ItemEntry.builder(Items.SEAGRASS).weight(3))

                        .with(ItemEntry.builder(Items.GRASS_BLOCK).weight(10))
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/mushroom"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.BROWN_MUSHROOM).weight(50))
                        .with(ItemEntry.builder(Items.RED_MUSHROOM).weight(50))
                        .with(ItemEntry.builder(Items.MYCELIUM).weight(30))
                        .with(ItemEntry.builder(Items.MUSHROOM_STEW).weight(10))
                        .with(ItemEntry.builder(Items.MOOSHROOM_SPAWN_EGG).weight(1))
                        .with(ItemEntry.builder(Items.RED_MUSHROOM_BLOCK).weight(10))
                        .with(ItemEntry.builder(Items.BROWN_MUSHROOM_BLOCK).weight(10))
                        .with(ItemEntry.builder(Items.MUSHROOM_STEM).weight(10))
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/jungle"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.BAMBOO).weight(20))
                        .with(ItemEntry.builder(Items.JUNGLE_SAPLING).weight(10))
                        .with(ItemEntry.builder(Items.JUNGLE_LOG).weight(40).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.JUNGLE_LEAVES).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.MELON).weight(10))
                        .with(ItemEntry.builder(Items.MOSSY_COBBLESTONE).weight(10))
                        .with(ItemEntry.builder(Items.COCOA_BEANS).weight(10))
                        .with(ItemEntry.builder(Items.PANDA_SPAWN_EGG).weight(1))
                        .with(ItemEntry.builder(Items.PARROT_SPAWN_EGG).weight(1))

                        .with(ItemEntry.builder(Items.GRASS_BLOCK).weight(30))
                        .with(ItemEntry.builder(Items.DIRT).weight(30))
                        //TODO 30dirt, 20stone
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/forest"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.OAK_LOG).weight(30).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.BIRCH_LOG).weight(30).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.OAK_LEAVES).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.BIRCH_LEAVES).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.OAK_SAPLING).weight(5))
                        .with(ItemEntry.builder(Items.BIRCH_SAPLING).weight(5))
                        .with(ItemEntry.builder(Items.BEE_NEST).weight(1))
                        .with(ItemEntry.builder(Items.ROSE_BUSH).weight(1))
                        .with(ItemEntry.builder(Items.LILAC).weight(1))
                        .with(ItemEntry.builder(Items.PEONY).weight(1))
                        .with(ItemEntry.builder(Items.LILY_OF_THE_VALLEY).weight(1))

                        .with(ItemEntry.builder(Items.GRASS_BLOCK).weight(50))
                        .with(ItemEntry.builder(Items.DIRT).weight(50))
                        //TODO 50stone,50dirt
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/sunflower"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.SUNFLOWER).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3))))
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/cherry"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.CHERRY_LOG).weight(25))
                        .with(ItemEntry.builder(Items.CHERRY_LEAVES).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.BEE_NEST).weight(1))
                        .with(ItemEntry.builder(Items.PINK_PETALS).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.GRASS_BLOCK).weight(20))
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/taiga"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.PODZOL).weight(20))
                        .with(ItemEntry.builder(Items.COARSE_DIRT).weight(10))
                        .with(ItemEntry.builder(Items.MOSSY_COBBLESTONE).weight(5))
                        .with(ItemEntry.builder(Items.SPRUCE_LOG).weight(30).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.SPRUCE_LEAVES).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.SPRUCE_SAPLING).weight(3))
                        .with(ItemEntry.builder(Items.FERN).weight(3))
                        .with(ItemEntry.builder(Items.LARGE_FERN).weight(3))
                        .with(ItemEntry.builder(Items.DEAD_BUSH).weight(3))
                        .with(ItemEntry.builder(Items.SWEET_BERRIES).weight(1))

                        .with(ItemEntry.builder(Items.DIRT).weight(50))
                        .with(ItemEntry.builder(Items.GRASS_BLOCK).weight(30))
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/dark_forest"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.DARK_OAK_LOG).weight(25).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.DARK_OAK_LEAVES).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.DARK_OAK_SAPLING).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.RED_MUSHROOM_BLOCK).weight(5))
                        .with(ItemEntry.builder(Items.BROWN_MUSHROOM_BLOCK).weight(5))
                        .with(ItemEntry.builder(Items.OAK_LOG).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.OAK_LEAVES).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.OAK_SAPLING).weight(1))
                        .with(ItemEntry.builder(Items.ROSE_BUSH).weight(1))
                        .with(ItemEntry.builder(Items.LILAC).weight(1))
                        .with(ItemEntry.builder(Items.PEONY).weight(1))
                        .with(ItemEntry.builder(Items.LILY_OF_THE_VALLEY).weight(1))

                        .with(ItemEntry.builder(Items.GRASS_BLOCK).weight(50))
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/savanna"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.ACACIA_LOG).weight(20))
                        .with(ItemEntry.builder(Items.ACACIA_LEAVES).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.ACACIA_SAPLING).weight(3))
                        .with(ItemEntry.builder(Items.TALL_GRASS).weight(3))
                        .with(ItemEntry.builder(Items.COARSE_DIRT).weight(5))

                        .with(ItemEntry.builder(Items.DIRT).weight(15))
                        .with(ItemEntry.builder(Items.GRASS_BLOCK).weight(15))
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/badlands"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.RED_SAND).weight(20))
                        .with(ItemEntry.builder(Items.CACTUS).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3))))
                        .with(ItemEntry.builder(Items.DEAD_BUSH).weight(5))
                        .with(ItemEntry.builder(Items.RED_TERRACOTTA).weight(15))
                        .with(ItemEntry.builder(Items.YELLOW_TERRACOTTA).weight(15))
                        .with(ItemEntry.builder(Items.WHITE_TERRACOTTA).weight(10))
                        .with(ItemEntry.builder(Items.LIGHT_GRAY_TERRACOTTA).weight(10))
                        .with(ItemEntry.builder(Items.BROWN_TERRACOTTA).weight(10))
                        .with(ItemEntry.builder(Items.TERRACOTTA).weight(20))
                        //TODO 10stone
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/emerald"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.EMERALD_ORE).weight(1))
                        .with(EmptyEntry.builder().weight(9))
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/river"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.SAND).weight(70))
                        .with(ItemEntry.builder(Items.SAND).weight(70))
                        .with(ItemEntry.builder(Items.CLAY).weight(20))
                        .with(ItemEntry.builder(Items.INK_SAC).weight(20))
                        .with(ItemEntry.builder(Items.SUGAR_CANE).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3))))
                        //TODO fish
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/ice"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.ICE).weight(100))
                        .with(ItemEntry.builder(Items.PACKED_ICE).weight(40))
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/snow"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.SNOWBALL).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.SNOW_BLOCK).weight(1))
                )
        );


        biConsumer.accept(new Identifier("aequitas", "gameplay/dripstone"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.DRIPSTONE_BLOCK).weight(10))
                        .with(ItemEntry.builder(Items.POINTED_DRIPSTONE).weight(5))
                        .with(ItemEntry.builder(Items.COPPER_ORE).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.DEEPSLATE_COPPER_ORE).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.RAW_COPPER_BLOCK).weight(5))
                )
        );

        biConsumer.accept(new Identifier("aequitas", "gameplay/lush_caves"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.AZALEA).weight(1))
                        .with(ItemEntry.builder(Items.AZALEA_LEAVES).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.FLOWERING_AZALEA).weight(1))
                        .with(ItemEntry.builder(Items.FLOWERING_AZALEA_LEAVES).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,8))))
                        .with(ItemEntry.builder(Items.ROOTED_DIRT).weight(1))
                        .with(ItemEntry.builder(Items.HANGING_ROOTS).weight(3))
                        .with(ItemEntry.builder(Items.MOSS_BLOCK).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.MOSS_CARPET).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,6))))
                        .with(ItemEntry.builder(Items.SHORT_GRASS).weight(1))
                        .with(ItemEntry.builder(Items.TALL_GRASS).weight(1))
                        .with(ItemEntry.builder(Items.VINE).weight(3))
                        .with(ItemEntry.builder(Items.CLAY).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,4))))
                        .with(ItemEntry.builder(Items.SMALL_DRIPLEAF).weight(3))
                        .with(ItemEntry.builder(Items.BIG_DRIPLEAF).weight(3))
                        .with(ItemEntry.builder(Items.GLOW_BERRIES).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,5))))
                        .with(ItemEntry.builder(Items.SPORE_BLOSSOM).weight(1))
                )
        );


        biConsumer.accept(new Identifier("aequitas", "gameplay/deep_dark"), LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(ItemEntry.builder(Items.SCULK).weight(10))
                        .with(ItemEntry.builder(Items.SCULK_VEIN).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,12))))
                        .with(ItemEntry.builder(Items.SCULK_SENSOR).weight(3))
                        .with(ItemEntry.builder(Items.SCULK_CATALYST).weight(3))
                        .with(ItemEntry.builder(Items.SCULK_SHRIEKER).weight(1))
                )
        );






        //Biome selectors
        biConsumer.accept(new Identifier("aequitas", "gameplay/biomes"), LootTable.builder()

                //Dimensions
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/nether")))
                        .conditionally(LocationCheckLootCondition.builder(LocationPredicate.Builder.createDimension(RegistryKeys.toWorldKey(DimensionOptions.NETHER))))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/end")))
                        .conditionally(LocationCheckLootCondition.builder(LocationPredicate.Builder.createDimension(RegistryKeys.toWorldKey(DimensionOptions.END))))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/overworld")))
                        .conditionally(LocationCheckLootCondition.builder(LocationPredicate.Builder.createDimension(RegistryKeys.toWorldKey(DimensionOptions.OVERWORLD))))
                )

                //Biome Types
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/ocean")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.OCEAN)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.DEEP_OCEAN)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.COLD_OCEAN)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.DEEP_COLD_OCEAN)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.LUKEWARM_OCEAN)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.DEEP_LUKEWARM_OCEAN)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.WARM_OCEAN)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.FROZEN_OCEAN)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.DEEP_FROZEN_OCEAN))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/desert")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.DESERT))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/swamp")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.SWAMP))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/mushroom")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.MUSHROOM_FIELDS))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/jungle")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.JUNGLE)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.BAMBOO_JUNGLE)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.SPARSE_JUNGLE))
                        ))
                )

                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/forest")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.FOREST)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.FLOWER_FOREST)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.BIRCH_FOREST)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.OLD_GROWTH_BIRCH_FOREST)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.PLAINS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.SUNFLOWER_PLAINS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.WINDSWEPT_FOREST)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.WINDSWEPT_HILLS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.MEADOW))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/emerald")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.JAGGED_PEAKS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.WINDSWEPT_HILLS))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/stone"))) //TODO
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.JAGGED_PEAKS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.FROZEN_PEAKS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.STONY_PEAKS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.STONY_SHORE))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/gravel")))  //TODO
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.STONY_SHORE)) //???
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/taiga")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.TAIGA)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.OLD_GROWTH_PINE_TAIGA)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.SNOWY_TAIGA)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.GROVE))

                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/river")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.RIVER)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.FROZEN_RIVER)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.BEACH)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.SNOWY_BEACH))

                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/frozen")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.FROZEN_PEAKS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.FROZEN_OCEAN)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.DEEP_FROZEN_OCEAN)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.FROZEN_RIVER)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.ICE_SPIKES))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/snow")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.FROZEN_PEAKS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.JAGGED_PEAKS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.STONY_PEAKS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.GROVE)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.SNOWY_SLOPES)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.SNOWY_TAIGA)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.SNOWY_BEACH)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.SNOWY_PLAINS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.ICE_SPIKES)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.FROZEN_OCEAN)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.DEEP_FROZEN_OCEAN)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.FROZEN_RIVER))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/dark_forest")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.DARK_FOREST))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/savanna")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.SAVANNA)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.SAVANNA_PLATEAU)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.WINDSWEPT_SAVANNA)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.WOODED_BADLANDS))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/badlands")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.BADLANDS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.ERODED_BADLANDS)),
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.WOODED_BADLANDS))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/cherry")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.CHERRY_GROVE))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/mangrove")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.MANGROVE_SWAMP))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/sunflower")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.SUNFLOWER_PLAINS))
                        ))
                )

                //Cave biomes
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/deep_dark")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.DEEP_DARK))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/dripstone")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.DRIPSTONE_CAVES))
                        ))
                )
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1f))
                        .with(LootTableEntry.builder(new Identifier("aequitas", "gameplay/lush_caves")))
                        .conditionally(AnyOfLootCondition.builder(
                                LocationCheckLootCondition.builder(LocationPredicate.Builder.createBiome(BiomeKeys.LUSH_CAVES))
                        ))
                )
        );


    }
}
