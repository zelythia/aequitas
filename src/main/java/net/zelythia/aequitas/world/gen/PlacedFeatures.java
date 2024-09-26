package net.zelythia.aequitas.world.gen;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;
import net.zelythia.aequitas.Aequitas;

import java.util.List;

public class PlacedFeatures {
    public static final RegistryKey<PlacedFeature> ESSENCE_PILLAR = RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(Aequitas.MOD_ID, "essence_pillar"));

    public static void bootstrap(Registerable<PlacedFeature> context) {
        RegistryEntryLookup<ConfiguredFeature<?, ?>> registryLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        context.register(ESSENCE_PILLAR, new PlacedFeature(registryLookup.getOrThrow(ConfiguredFeatures.ESSENCE_PILLAR), List.of(
                SquarePlacementModifier.of(),
                net.minecraft.world.gen.feature.PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
                RarityFilterPlacementModifier.of(150) // 1/chance
                ))
        );
    }
}
