package net.zelythia.aequitas.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ItemDuplicatedCriterion extends AbstractCriterion<ItemDuplicatedCriterion.Conditions> {

    static final Identifier ID = new Identifier("duplicated_item");

    public ItemDuplicatedCriterion() {
    }

    public Identifier getId() {
        return ID;
    }

    public Conditions conditionsFromJson(JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(lootContextPredicate, itemPredicate);
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.trigger(player, (conditions) -> conditions.test(stack));
    }


    public static class Conditions extends AbstractCriterionConditions {
        private final ItemPredicate item;

        public Conditions(LootContextPredicate player, ItemPredicate item) {
            super(ID, player);
            this.item = item;
        }

        public static Conditions create(ItemPredicate item) {
            return new Conditions(LootContextPredicate.EMPTY, item);
        }

        public boolean test(ItemStack stack) {
            return this.item.test(stack);
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("item", this.item.toJson());
            return jsonObject;
        }
    }
}
