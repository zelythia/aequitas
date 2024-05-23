package net.zelythia.aequitas.compat.rei;

import com.google.gson.JsonElement;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.compat.LootTableParser;
import net.zelythia.aequitas.compat.LootTableParser.ItemEntry;
import net.zelythia.aequitas.item.AequitasItems;

import java.util.ArrayList;
import java.util.List;

public class ReiPlugin implements REIClientPlugin {


    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new CollectionBowlCategory());

        registry.addWorkstations(CollectionBowlCategory.IDENTIFIER, EntryStacks.of(AequitasItems.COLLECTION_BOWL_I), EntryStacks.of(AequitasItems.COLLECTION_BOWL_II), EntryStacks.of(AequitasItems.COLLECTION_BOWL_III));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        net.zelythia.aequitas.client.NetworkingHandler.updateLootTables();

        for (JsonElement element : net.zelythia.aequitas.client.NetworkingHandler.LOOTTABLES.get(new Identifier("aequitas", "gameplay/biomes")).getAsJsonArray("pools")) {

            try {
                List<Identifier> conditions = new ArrayList<>();
                for (JsonElement condition : element.getAsJsonObject().getAsJsonArray("conditions")) {
                    LootTableParser.parseCondition(condition.getAsJsonObject(), conditions);
                }

                StringBuilder name = new StringBuilder();
                List<ItemEntry> entries = new ArrayList<>();
                for (JsonElement entry : element.getAsJsonObject().getAsJsonArray("entries")) {
                    LootTableParser.parseEntry(entry.getAsJsonObject(), entries, name);
                }

                registry.add(new CollectionBowlDisplay(entries, conditions, name.toString()));
            } catch (Exception e) {
                Aequitas.LOGGER.error("REI: Error parsing loot tables");
            }

        }
    }


//    private void registerDisplays(){
//        DisplayRegistry registry = DisplayRegistry.getInstance();
//
//        for (JsonElement element : net.zelythia.aequitas.client.NetworkingHandler.LOOTTABLES.get(new Identifier("aequitas", "gameplay/biomes")).getAsJsonArray("pools")) {
//
//            try {
//                List<Identifier> conditions = new ArrayList<>();
//                for (JsonElement condition : element.getAsJsonObject().getAsJsonArray("conditions")) {
//                    LootTableParser.parseCondition(condition.getAsJsonObject(), conditions);
//                }
//
//                StringBuilder name = new StringBuilder();
//                List<ItemEntry> entries = new ArrayList<>();
//                for (JsonElement entry : element.getAsJsonObject().getAsJsonArray("entries")) {
//                    LootTableParser.parseEntry(entry.getAsJsonObject(), entries, name);
//                }
//
//                registry.add(new CollectionBowlDisplay(entries, conditions, name.toString()));
//            }
//            catch (Exception e){
//                Aequitas.LOGGER.error("REI: Error parsing loot tables");
//            }
//
//        }
//    }
}
