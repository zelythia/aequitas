package net.zelythia.aequitas.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.zelythia.aequitas.block.AequitasBlocks;
import net.zelythia.aequitas.item.AequitasItems;

import java.util.concurrent.CompletableFuture;

public class TagGenerator {

    public static class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
        public BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            getOrCreateTagBuilder(AequitasBlocks.INFUSED_BLOCKS)
                    .add(AequitasBlocks.INFUSED_STONE)
                    .add(AequitasBlocks.INFUSED_STONE_PILLAR)
                    .add(AequitasBlocks.INFUSED_STONE_SLAB)
                    .add(AequitasBlocks.INFUSED_STONE_STAIRS)
                    .add(AequitasBlocks.CHISELED_INFUSED_STONE)
                    .add(AequitasBlocks.ETCHED_INFUSED_STONE)
                    .add(AequitasBlocks.SMOOTH_INFUSED_STONE);
        }
    }

    public static class ItemTagGenerator extends FabricTagProvider.ItemTagProvider{
        public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
            super(output, completableFuture, null);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
                    .add(AequitasItems.PRIMAL_ESSENCE_HELMET)
                    .add(AequitasItems.PRIMAL_ESSENCE_CHESTPLATE)
                    .add(AequitasItems.PRIMAL_ESSENCE_LEGGINGS)
                    .add(AequitasItems.PRIMAL_ESSENCE_BOOTS)

                    .add(AequitasItems.PRIMORDIAL_ESSENCE_HELMET)
                    .add(AequitasItems.PRIMORDIAL_ESSENCE_CHESTPLATE)
                    .add(AequitasItems.PRIMORDIAL_ESSENCE_LEGGINGS)
                    .add(AequitasItems.PRIMORDIAL_ESSENCE_BOOTS)

                    .add(AequitasItems.PRISTINE_ESSENCE_HELMET)
                    .add(AequitasItems.PRISTINE_ESSENCE_CHESTPLATE)
                    .add(AequitasItems.PRISTINE_ESSENCE_LEGGINGS)
                    .add(AequitasItems.PRISTINE_ESSENCE_BOOTS);
        }
    }
}
