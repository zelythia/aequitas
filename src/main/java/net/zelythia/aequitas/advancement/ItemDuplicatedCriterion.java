package net.zelythia.aequitas.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class ItemDuplicatedCriterion extends AbstractCriterion<ItemDuplicatedCriterion.Conditions> {

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.trigger(player, (conditions) -> conditions.test(stack));
    }

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return ItemDuplicatedCriterion.Conditions.CODEC;
    }


    public record Conditions(Optional<LootContextPredicate> player,
                             Optional<ItemPredicate> item) implements AbstractCriterion.Conditions {
        public static final Codec<ItemDuplicatedCriterion.Conditions> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ItemDuplicatedCriterion.Conditions::player),
                ItemPredicate.CODEC.optionalFieldOf("item").forGetter(ItemDuplicatedCriterion.Conditions::item)).apply(instance, ItemDuplicatedCriterion.Conditions::new));


        public static AdvancementCriterion<ItemDuplicatedCriterion.Conditions> createAny() {
            return PlayerStatistics.ITEM_DUPLICATED_CRITERION.create(new ItemDuplicatedCriterion.Conditions(Optional.empty(), Optional.empty()));
        }

        public static AdvancementCriterion<ItemDuplicatedCriterion.Conditions> create(ItemPredicate itemPredicate) {
            return PlayerStatistics.ITEM_DUPLICATED_CRITERION.create(new ItemDuplicatedCriterion.Conditions(Optional.empty(), Optional.of(itemPredicate)));
        }


        public boolean test(ItemStack stack) {
            return this.item.isEmpty() || this.item.get().test(stack);
        }
    }
}
