package net.zelythia.aequitas;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.item.AequitasItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootTableModifier {

    private static final Map<Identifier, List<LootPoolEntry>> customCollectionBowlLoot = new HashMap<>();

    public static void setCustomCollectionBowlLoot(Map<Identifier, List<LootPoolEntry>> map){
        customCollectionBowlLoot.clear();
        customCollectionBowlLoot.putAll(map);
    }


    public static class Modifier implements LootTableEvents.Modify{
        @Override
        public void modifyLootTable(ResourceManager resourceManager, LootManager lootManager, Identifier id, LootTable.Builder tableBuilder, LootTableSource source) {

            //Custom CollectionBowl Loot
            if(customCollectionBowlLoot.containsKey(id)){
                tableBuilder.modifyPools(builder -> {
                    Aequitas.LOGGER.info("Added {} loot entries to {}", customCollectionBowlLoot.get(id), id);
                    builder.with(customCollectionBowlLoot.get(id));
                });

                customCollectionBowlLoot.remove(id);
            }

            if (id.toString().startsWith("minecraft:blocks") || id.toString().startsWith("minecraft:entities")) return;
            if (id.equals(LootTables.DESERT_PYRAMID_CHEST) || id.equals(LootTables.SHIPWRECK_TREASURE_CHEST)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(AequitasItems.PRIMAL_ESSENCE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))).weight(4))
                        .with(ItemEntry.builder(AequitasItems.PRIMORDIAL_ESSENCE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))).weight(2))
                        .with(ItemEntry.builder(AequitasItems.PRISTINE_ESSENCE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 4))).weight(1))
                        .with(EmptyEntry.builder().weight(7));

                tableBuilder.pool(poolBuilder);
            }
        }
    }
}
