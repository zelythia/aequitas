package net.zelythia.aequitas.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

public class CollectionBowlConstructedCriterion extends AbstractCriterion<CollectionBowlConstructedCriterion.Conditions> {

    public void trigger(ServerPlayerEntity player, int tier) {
        this.trigger(player, (conditions) -> conditions.test(tier));
    }

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return CollectionBowlConstructedCriterion.Conditions.CODEC;
    }


    public record Conditions(Optional<LootContextPredicate> player,
                             Optional<Integer> tier) implements AbstractCriterion.Conditions {
        public static final Codec<CollectionBowlConstructedCriterion.Conditions> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                Codecs.POSITIVE_INT.optionalFieldOf("tier").forGetter(Conditions::tier)).apply(instance, Conditions::new));

        public static AdvancementCriterion<CollectionBowlConstructedCriterion.Conditions> create(int tier) {
            return PlayerStatistics.COLLECTION_BOWL_CONSTRUCTED_CRITERION.create(new CollectionBowlConstructedCriterion.Conditions(Optional.empty(), Optional.of(tier)));
        }

        public boolean test(int tier) {
            return this.tier.isEmpty() || this.tier.get() == tier;
        }
    }
}
