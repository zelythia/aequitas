package net.zelythia.aequitas.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.zelythia.aequitas.world.gen.ConfiguredFeatures;
import net.zelythia.aequitas.world.gen.PlacedFeatures;

public class AequitasDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(LootTableProvider::new);
        pack.addProvider(WorldGen::new);
        pack.addProvider(AdvancementsProvider::new);
        pack.addProvider(TagGenerator.BlockTagGenerator::new);
        pack.addProvider(TagGenerator.ItemTagGenerator::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ConfiguredFeatures::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, PlacedFeatures::bootstrap);
    }
}
