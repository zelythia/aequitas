package net.zelythia.aequitas.world.gen;

import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.zelythia.aequitas.Aequitas;

public class ConfiguredFeatures {

    public static final RegistryKey<ConfiguredFeature<?, ?>> ESSENCE_PILLAR = RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(Aequitas.MOD_ID, "essence_pillar"));

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        context.register(ESSENCE_PILLAR, new ConfiguredFeature<>(Aequitas.ESSENCE_PILLAR_FEATURE, new EssencePillarFeatureConfig(10, Blocks.QUARTZ_BLOCK.getDefaultState())));
    }
}
