package net.zelythia.aequitas.advancement;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;

public class PlayerStatistics {

    public static final Identifier USE_CRAFTING_PEDESTAL = new Identifier(Aequitas.MOD_ID, "use_crafting_pedestal");


    public static final ItemDuplicatedCriterion ITEM_DUPLICATED_CRITERION = Criteria.register(new ItemDuplicatedCriterion());
    public static final CollectionBowlConstructedCriterion COLLECTION_BOWL_CONSTRUCTED_CRITERION = Criteria.register(new CollectionBowlConstructedCriterion());


    public static void register() {
        Registry.register(Registries.CUSTOM_STAT, "use_crafting_pedestal", USE_CRAFTING_PEDESTAL);

        Stats.CUSTOM.getOrCreateStat(USE_CRAFTING_PEDESTAL, StatFormatter.DEFAULT);
    }
}
