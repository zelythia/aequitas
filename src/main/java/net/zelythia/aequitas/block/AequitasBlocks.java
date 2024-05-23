package net.zelythia.aequitas.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;

public class AequitasBlocks {

    public static final Block PRIMAL_ESSENCE_BLOCK;
    public static final Block PRIMORDIAL_ESSENCE_BLOCK;
    public static final Block PRISTINE_ESSENCE_BLOCK;

    public static final Block PEDESTAL;
    public static final Block CRAFTING_PEDESTAL;
    public static final Block SAMPLING_PEDESTAL;

    public static final Block CONDUIT;
    public static final Block CATALYST_I;
    public static final Block CATALYST_II;
    public static final Block CATALYST_III;
    public static final Block COLLECTION_BOWL_I;
    public static final Block COLLECTION_BOWL_II;
    public static final Block COLLECTION_BOWL_III;


    public static final BooleanProperty ACTIVE_BLOCK_PROPERTY = BooleanProperty.of("active");


    static {
        PRIMAL_ESSENCE_BLOCK = register("primal_essence_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.GREEN).sounds(BlockSoundGroup.GLASS).strength(0.3F)));
        PRIMORDIAL_ESSENCE_BLOCK = register("primordial_essence_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).sounds(BlockSoundGroup.GLASS).strength(0.3F)));
        PRISTINE_ESSENCE_BLOCK = register("pristine_essence_block", new Block(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).sounds(BlockSoundGroup.GLASS).strength(0.3F)));

        PEDESTAL = register("pedestal", new PedestalBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(1.25F, 4.2F)));
        CRAFTING_PEDESTAL = register("crafting_pedestal", new CraftingPedestalBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(1.25F, 4.2F)));
        SAMPLING_PEDESTAL = register("sampling_pedestal", new SamplingPedestalBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).requiresTool().strength(1.25F, 4.2F)));

        CONDUIT = register("conduit_block", new ConduitBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).sounds(BlockSoundGroup.STONE).requiresTool().strength(1.8F).luminance((blockState) -> (Boolean) blockState.get(ACTIVE_BLOCK_PROPERTY) ? 13 : 0)));
        CATALYST_I = register("primal_catalyst", new CatalystBlock(AbstractBlock.Settings.create().mapColor(MapColor.GREEN).sounds(BlockSoundGroup.GLASS).strength(0.3F).luminance((blockState) -> (Boolean) blockState.get(ACTIVE_BLOCK_PROPERTY) ? 15 : 0), 1));
        CATALYST_II = register("primordial_catalyst", new CatalystBlock(AbstractBlock.Settings.create().mapColor(MapColor.LIGHT_BLUE).sounds(BlockSoundGroup.GLASS).luminance((blockState) -> (Boolean) blockState.get(ACTIVE_BLOCK_PROPERTY) ? 15 : 0), 2));
        CATALYST_III = register("pristine_catalyst", new CatalystBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).sounds(BlockSoundGroup.GLASS).luminance((blockState) -> (Boolean) blockState.get(ACTIVE_BLOCK_PROPERTY) ? 15 : 0), 3));
        COLLECTION_BOWL_I = register("collection_bowl_1", new CollectionBowlBlock(AbstractBlock.Settings.create().requiresTool().strength(1.8F), 1));
        COLLECTION_BOWL_II = register("collection_bowl_2", new CollectionBowlBlock(AbstractBlock.Settings.create().requiresTool().strength(1.8F), 9));
        COLLECTION_BOWL_III = register("collection_bowl_3", new CollectionBowlBlock(AbstractBlock.Settings.create().requiresTool().strength(1.8F), 15));
    }

    public static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(Aequitas.MOD_ID, id), block);
    }

    public AequitasBlocks() {

    }
}
