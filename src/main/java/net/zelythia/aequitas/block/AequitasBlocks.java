package net.zelythia.aequitas.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;

public class AequitasBlocks {

    public static final Block PRIMAL_ESSENCE_BLOCK;
    public static final Block PRIMORDIAL_ESSENCE_BLOCK;
    public static final Block PRISTINE_ESSENCE_BLOCK;

    public static final Block INFUSED_STONE;
    public static final Block INFUSED_STONE_PILLAR;
    public static final Block INFUSED_STONE_SLAB;
    public static final Block INFUSED_STONE_STAIRS;
    public static final Block CHISELED_INFUSED_STONE;
    public static final Block ETCHED_INFUSED_STONE;
    public static final Block SMOOTH_INFUSED_STONE;

    public static final Block PETRIFIED_ESSENCE;

    public static final Block PEDESTAL;
    public static final Block CRAFTING_PEDESTAL;
    public static final Block SAMPLING_PEDESTAL;

    public static final Block CATALYST_I;
    public static final Block CATALYST_II;
    public static final Block CATALYST_III;
    public static final Block COLLECTION_BOWL_I;
    public static final Block COLLECTION_BOWL_II;
    public static final Block COLLECTION_BOWL_III;


    public static final BooleanProperty ACTIVE_BLOCK_PROPERTY = BooleanProperty.of("active");
    public static final BooleanProperty TOP_BLOCK_PROPERTY = BooleanProperty.of("top");
    public static final BooleanProperty BOTTOM_BLOCK_PROPERTY = BooleanProperty.of("bottom");

    public static final TagKey<Block> INFUSED_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("aequitas", "infused_blocks"));


    static {
        INFUSED_STONE = register("infused_stone", new InfusedStoneBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.BASALT).requiresTool().strength(1.25F, 4.2F)));
        INFUSED_STONE_PILLAR = register("infused_stone_pillar", new InfusedStonePillar(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.BASALT).strength(1.25F, 4.2F)));
        INFUSED_STONE_SLAB = register("infused_stone_slab", new InfusedStoneSlab(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.BASALT).strength(1.25F, 4.2F)));
        INFUSED_STONE_STAIRS = register("infused_stone_stairs", new InfusedStoneStairs(INFUSED_STONE.getDefaultState(), AbstractBlock.Settings.create().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.BASALT).strength(1.25F, 4.2F)));
        CHISELED_INFUSED_STONE = register("chiseled_infused_stone", new InfusedStoneBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.BASALT).strength(1.25F, 4.2F)));
        ETCHED_INFUSED_STONE = register("etched_infused_stone", new InfusedStoneBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.BASALT).strength(1.25F, 4.2F)));
        SMOOTH_INFUSED_STONE = register("smooth_infused_stone", new InfusedStoneBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.BASALT).strength(1.25F, 4.2F)));
        PETRIFIED_ESSENCE = register("petrified_essence", new Block(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).sounds(BlockSoundGroup.CALCITE).strength(0.8f)));

        PRIMAL_ESSENCE_BLOCK = register("primal_essence_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.GREEN).strength(1.5F).requiresTool()));
        PRIMORDIAL_ESSENCE_BLOCK = register("primordial_essence_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).strength(1.5F).requiresTool()));
        PRISTINE_ESSENCE_BLOCK = register("pristine_essence_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(1.5F).requiresTool()));

        PEDESTAL = register("pedestal", new PedestalBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(1.25F, 4.2F)));
        CRAFTING_PEDESTAL = register("crafting_pedestal", new CraftingPedestalBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(1.25F, 4.2F)));
        SAMPLING_PEDESTAL = register("sampling_pedestal", new SamplingPedestalBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(1.25F, 4.2F)));

        CATALYST_I = register("primal_catalyst", new CatalystBlock(AbstractBlock.Settings.create().strength(1.5F).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK).mapColor(MapColor.GREEN).strength(0.3F).luminance((blockState) -> (Boolean) blockState.get(ACTIVE_BLOCK_PROPERTY) ? 15 : 0), 1));
        CATALYST_II = register("primordial_catalyst", new CatalystBlock(AbstractBlock.Settings.create().strength(1.5F).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK).mapColor(MapColor.LIGHT_BLUE).luminance((blockState) -> (Boolean) blockState.get(ACTIVE_BLOCK_PROPERTY) ? 15 : 0), 2));
        CATALYST_III = register("pristine_catalyst", new CatalystBlock(AbstractBlock.Settings.create().strength(1.5F).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK).mapColor(MapColor.WHITE).luminance((blockState) -> (Boolean) blockState.get(ACTIVE_BLOCK_PROPERTY) ? 15 : 0), 3));
        COLLECTION_BOWL_I = register("collection_bowl_1", new CollectionBowlBlock(AbstractBlock.Settings.create().requiresTool().strength(1.25F, 4.2F).requiresTool().mapColor(MapColor.GRAY), 1));
        COLLECTION_BOWL_II = register("collection_bowl_2", new CollectionBowlBlock(AbstractBlock.Settings.create().requiresTool().strength(1.25F, 4.2F).requiresTool().mapColor(MapColor.GRAY), 9));
        COLLECTION_BOWL_III = register("collection_bowl_3", new CollectionBowlBlock(AbstractBlock.Settings.create().requiresTool().strength(1.25F, 4.2F).requiresTool().mapColor(MapColor.GRAY), 15));
    }

    public static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(Aequitas.MOD_ID, id), block);
    }

    public AequitasBlocks() {

    }
}
