package net.zelythia.aequitas.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import net.zelythia.aequitas.compat.LootTableParser.ItemEntry;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionBowlDisplay implements Display {

    public final List<Identifier> conditions;
    public final Map<EntryIngredient, Double> outputs = new HashMap<>();
    public final String name;

    public CollectionBowlDisplay(List<ItemEntry> items, List<Identifier> conditions, String name) {
        this.conditions = conditions;
        this.name = name;

        double weight = 0;
        for (ItemEntry itemEntry : items) {
            weight += itemEntry.weight();
        }

        for (ItemEntry item : items) {
            if(!new Identifier("minecraft", "air").equals(item.id())){
                outputs.put(EntryIngredients.of(Registries.ITEM.get(item.id())), ((int) ((item.weight() / weight) * 10000)) / 100d);
            }
        }
    }

    public String getName() {
        return name;
    }


    public List<Identifier> getConditions() {
        return conditions;
    }


    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(EntryIngredient.empty());
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return outputs.entrySet().stream()
                .sorted(
                        Comparator.comparingDouble(value -> ((Map.Entry<EntryIngredient, Double>) value).getValue()).reversed()
                                .thenComparing(o -> ((Map.Entry<EntryIngredient, Double>) o).getKey().get(0).getIdentifier().getPath()))
                .flatMap(entryIngredientDoubleEntry -> Stream.of(entryIngredientDoubleEntry.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CollectionBowlCategory.IDENTIFIER;
    }
}
