package net.zelythia.aequitas.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.zelythia.aequitas.block.AequitasBlocks;

import java.util.concurrent.CompletableFuture;

public class TagGenerator extends FabricTagProvider.BlockTagProvider {

    public TagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
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
