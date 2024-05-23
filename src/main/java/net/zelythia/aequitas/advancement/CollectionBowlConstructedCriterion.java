package net.zelythia.aequitas.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CollectionBowlConstructedCriterion extends AbstractCriterion<CollectionBowlConstructedCriterion.Conditions> {

    static final Identifier ID = new Identifier("collection_bowl_constructed");

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new CollectionBowlConstructedCriterion.Conditions(playerPredicate, obj.get("tier").getAsInt());
    }

    public void trigger(ServerPlayerEntity player, int tier) {
        this.trigger(player, (conditions) -> conditions.test(tier));
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final int tier;

        public Conditions(LootContextPredicate entity, int tier) {
            super(ID, entity);
            this.tier = tier;
        }

        public static CollectionBowlConstructedCriterion.Conditions create(int tier) {
            return new Conditions(LootContextPredicate.EMPTY, tier);
        }

        public boolean test(int tier) {
            return this.tier == tier;
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.addProperty("tier", tier);
            return jsonObject;
        }
    }
}
