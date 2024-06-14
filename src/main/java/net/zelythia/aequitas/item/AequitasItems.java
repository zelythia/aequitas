package net.zelythia.aequitas.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.block.AequitasBlocks;

public class AequitasItems {

    public static final Item INFUSED_STONE;
    public static final Item INFUSED_STONE_PILLAR;
    public static final Item INFUSED_STONE_SLAB;
    public static final Item INFUSED_STONE_STAIRS;
    public static final Item CHISELED_INFUSED_STONE;
    public static final Item ETCHED_INFUSED_STONE;
    public static final Item SMOOTH_INFUSED_STONE;

    public static final Item PRIMAL_ESSENCE;
    public static final Item PRIMORDIAL_ESSENCE;
    public static final Item PRISTINE_ESSENCE;
    public static final Item PRIMAL_ESSENCE_BLOCK;
    public static final Item PRIMORDIAL_ESSENCE_BLOCK;
    public static final Item PRISTINE_ESSENCE_BLOCK;


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


    public static final Item SAMPLING_PEDESTAL_CORE;
    public static final Item CRAFTING_PEDESTAL_CORE;
    public static final Item PORTABLE_PEDESTAL_CORE;

    public static final Item PEDESTAL;
    public static final Item SAMPLING_PEDESTAL;
    public static final Item CRAFTING_PEDESTAL;
    public static final Item PORTABLE_PEDESTAL;


    public static final Item CATALYST_I;
    public static final Item CATALYST_II;
    public static final Item CATALYST_III;
    public static final Item COLLECTION_BOWL_I;
    public static final Item COLLECTION_BOWL_II;
    public static final Item COLLECTION_BOWL_III;


    public static final ItemGroup ITEM_GROUP;

    static {
        INFUSED_STONE = register("infused_stone", new BlockItem(AequitasBlocks.INFUSED_STONE, new Item.Settings()));
        INFUSED_STONE_PILLAR = register("infused_stone_pillar", new BlockItem(AequitasBlocks.INFUSED_STONE_PILLAR, new Item.Settings()));
        INFUSED_STONE_SLAB = register("infused_stone_slab", new BlockItem(AequitasBlocks.INFUSED_STONE_SLAB, new Item.Settings()));
        INFUSED_STONE_STAIRS = register("infused_stone_stairs", new BlockItem(AequitasBlocks.INFUSED_STONE_STAIRS, new Item.Settings()));
        CHISELED_INFUSED_STONE = register("chiseled_infused_stone", new BlockItem(AequitasBlocks.CHISELED_INFUSED_STONE, new Item.Settings()));
        ETCHED_INFUSED_STONE = register("etched_infused_stone", new BlockItem(AequitasBlocks.ETCHED_INFUSED_STONE, new Item.Settings()));
        SMOOTH_INFUSED_STONE = register("smooth_infused_stone", new BlockItem(AequitasBlocks.SMOOTH_INFUSED_STONE, new Item.Settings()));

        PRIMAL_ESSENCE = register("primal_essence", new Item(new Item.Settings()));
        PRIMORDIAL_ESSENCE = register("primordial_essence", new Item(new Item.Settings()));
        PRISTINE_ESSENCE = register("pristine_essence", new Item(new Item.Settings()));
        PRIMAL_ESSENCE_BLOCK = register("primal_essence_block", new BlockItem(AequitasBlocks.PRIMAL_ESSENCE_BLOCK, new Item.Settings()));
        PRIMORDIAL_ESSENCE_BLOCK = register("primordial_essence_block", new BlockItem(AequitasBlocks.PRIMORDIAL_ESSENCE_BLOCK, new Item.Settings()));
        PRISTINE_ESSENCE_BLOCK = register("pristine_essence_block", new BlockItem(AequitasBlocks.PRISTINE_ESSENCE_BLOCK, new Item.Settings()));

        PRIMAL_ESSENCE_HELMET = register("primal_essence_helmet", new EssenceArmorItem(ArmorMaterials.PRIMAL, ArmorItem.Type.HELMET, new Item.Settings()));
        PRIMAL_ESSENCE_CHESTPLATE = register("primal_essence_chestplate", new EssenceArmorItem(ArmorMaterials.PRIMAL, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
        PRIMAL_ESSENCE_LEGGINGS = register("primal_essence_leggings", new ArmorItem(ArmorMaterials.PRIMAL, ArmorItem.Type.LEGGINGS, new Item.Settings()));
        PRIMAL_ESSENCE_BOOTS = register("primal_essence_boots", new ArmorItem(ArmorMaterials.PRIMAL, ArmorItem.Type.BOOTS, new Item.Settings()));
        PRIMORDIAL_ESSENCE_HELMET = register("primordial_essence_helmet", new EssenceArmorItem(ArmorMaterials.PRIMORDIAL, ArmorItem.Type.HELMET, new Item.Settings()));
        PRIMORDIAL_ESSENCE_CHESTPLATE = register("primordial_essence_chestplate", new EssenceArmorItem(ArmorMaterials.PRIMORDIAL, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
        PRIMORDIAL_ESSENCE_LEGGINGS = register("primordial_essence_leggings", new ArmorItem(ArmorMaterials.PRIMORDIAL, ArmorItem.Type.LEGGINGS, new Item.Settings()));
        PRIMORDIAL_ESSENCE_BOOTS = register("primordial_essence_boots", new ArmorItem(ArmorMaterials.PRIMORDIAL, ArmorItem.Type.BOOTS, new Item.Settings()));
        PRISTINE_ESSENCE_HELMET = register("pristine_essence_helmet", new EssenceArmorItem(ArmorMaterials.PRISTINE, ArmorItem.Type.HELMET, new Item.Settings()));
        PRISTINE_ESSENCE_CHESTPLATE = register("pristine_essence_chestplate", new EssenceArmorItem(ArmorMaterials.PRISTINE, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
        PRISTINE_ESSENCE_LEGGINGS = register("pristine_essence_leggings", new ArmorItem(ArmorMaterials.PRISTINE, ArmorItem.Type.LEGGINGS, new Item.Settings()));
        PRISTINE_ESSENCE_BOOTS = register("pristine_essence_boots", new ArmorItem(ArmorMaterials.PRISTINE, ArmorItem.Type.BOOTS, new Item.Settings()));

        SAMPLING_PEDESTAL_CORE = register("sampling_pedestal_core", new Item(new Item.Settings()));
        CRAFTING_PEDESTAL_CORE = register("crafting_pedestal_core", new Item(new Item.Settings()));
        PORTABLE_PEDESTAL_CORE = register("portable_pedestal_core", new Item(new Item.Settings()));
        PEDESTAL = register("pedestal", new BlockItem(AequitasBlocks.PEDESTAL, new Item.Settings()));
        CRAFTING_PEDESTAL = register("crafting_pedestal", new BlockItem(AequitasBlocks.CRAFTING_PEDESTAL, new Item.Settings()));
        SAMPLING_PEDESTAL = register("sampling_pedestal", new BlockItem(AequitasBlocks.SAMPLING_PEDESTAL, new Item.Settings()));
        PORTABLE_PEDESTAL = register("portable_pedestal", new PortablePedestalItem(new Item.Settings().maxCount(1)));

        CATALYST_I = register("primal_catalyst", new BlockItem(AequitasBlocks.CATALYST_I, new Item.Settings()));
        CATALYST_II = register("primordial_catalyst", new BlockItem(AequitasBlocks.CATALYST_II, new Item.Settings()));
        CATALYST_III = register("pristine_catalyst", new BlockItem(AequitasBlocks.CATALYST_III, new Item.Settings()));
        COLLECTION_BOWL_I = register("collection_bowl_1", new BlockItem(AequitasBlocks.COLLECTION_BOWL_I, new Item.Settings()));
        COLLECTION_BOWL_II = register("collection_bowl_2", new BlockItem(AequitasBlocks.COLLECTION_BOWL_II, new Item.Settings()));
        COLLECTION_BOWL_III = register("collection_bowl_3", new BlockItem(AequitasBlocks.COLLECTION_BOWL_III, new Item.Settings()));


        ITEM_GROUP = Registry.register(Registries.ITEM_GROUP, new Identifier(Aequitas.MOD_ID, "aequitas_item_group"),
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(CRAFTING_PEDESTAL))
                        .displayName(Text.translatable("itemGroup.aequitas.item_group"))
                        .entries((context, entries) -> {
                            entries.add(INFUSED_STONE);
                            entries.add(INFUSED_STONE_PILLAR);
                            entries.add(INFUSED_STONE_SLAB);
                            entries.add(INFUSED_STONE_STAIRS);
                            entries.add(CHISELED_INFUSED_STONE);
                            entries.add(ETCHED_INFUSED_STONE);
                            entries.add(SMOOTH_INFUSED_STONE);

                            entries.add(PRIMAL_ESSENCE);
                            entries.add(PRIMORDIAL_ESSENCE);
                            entries.add(PRISTINE_ESSENCE);
                            entries.add(PRIMAL_ESSENCE_BLOCK);
                            entries.add(PRIMORDIAL_ESSENCE_BLOCK);
                            entries.add(PRISTINE_ESSENCE_BLOCK);

                            entries.add(CATALYST_I);
                            entries.add(CATALYST_II);
                            entries.add(CATALYST_III);
                            entries.add(COLLECTION_BOWL_I);
                            entries.add(COLLECTION_BOWL_II);
                            entries.add(COLLECTION_BOWL_III);

                            entries.add(SAMPLING_PEDESTAL_CORE);
                            entries.add(CRAFTING_PEDESTAL_CORE);
                            entries.add(PORTABLE_PEDESTAL_CORE);

                            entries.add(PEDESTAL);
                            entries.add(CRAFTING_PEDESTAL);
                            entries.add(SAMPLING_PEDESTAL);
                            entries.add(PORTABLE_PEDESTAL);

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
                        })
                        .build());
    }


    public static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Aequitas.MOD_ID, id), item);
    }


    public AequitasItems() {

    }
}
