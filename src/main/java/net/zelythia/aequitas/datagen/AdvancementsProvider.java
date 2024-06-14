package net.zelythia.aequitas.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.advancement.CollectionBowlConstructedCriterion;
import net.zelythia.aequitas.advancement.ItemDuplicatedCriterion;
import net.zelythia.aequitas.item.AequitasItems;

import java.util.function.Consumer;

public class AdvancementsProvider extends FabricAdvancementProvider {
    public AdvancementsProvider(FabricDataOutput output) {
        super(output);
    }


    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        Advancement root = Advancement.Builder.create()
                .display(
                        AequitasItems.CRAFTING_PEDESTAL,
                        Text.translatable("advancements.aequitas.welcome.title"),
                        Text.translatable("advancements.aequitas.welcome.description"),
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion("root", TickCriterion.Conditions.createTick())
                .build(consumer, "aequitas:" + "root");


        Advancement duplicateItem = Advancement.Builder.create().parent(root)
                .display(
                        AequitasItems.CRAFTING_PEDESTAL,
                        Text.translatable("advancements.aequitas.duplicated_item.title"),
                        Text.translatable("advancements.aequitas.duplicated_item.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )

                .criterion("duplicated_item", ItemDuplicatedCriterion.Conditions.create(ItemPredicate.ANY))
                .build(consumer, "aequitas:" + "duplicate_item");


        Advancement dragonEggDuplication = Advancement.Builder.create().parent(duplicateItem)
                .display(
                        Items.DRAGON_EGG,
                        Text.translatable("advancements.aequitas.duplicated_dragon_egg.title"),
                        Text.translatable("advancements.aequitas.duplicated_dragon_egg.description"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(1000))
                .criterion("duplicated_dragon_egg", ItemDuplicatedCriterion.Conditions.create(ItemPredicate.Builder.create().items(Items.DRAGON_EGG).build()))
                .build(consumer, "aequitas:" + "duplicated_dragon_egg");

        Advancement portablePedestalAdvancement = Advancement.Builder.create().parent(duplicateItem)
                .display(
                        AequitasItems.PORTABLE_PEDESTAL,
                        Text.translatable("advancements.aequitas.get_portable_pedestal.title"),
                        Text.translatable("advancements.aequitas.get_portable_pedestal.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .rewards(AdvancementRewards.Builder.experience(1000))
                .criterion("get_portable_pedestal", InventoryChangedCriterion.Conditions.items(AequitasItems.PORTABLE_PEDESTAL))
                .build(consumer, "aequitas:" + "portable_pedestal");


        Advancement collectionBowlI = Advancement.Builder.create().parent(root)
                .display(
                        AequitasItems.COLLECTION_BOWL_I,
                        Text.translatable("advancements.aequitas.collection_bowl_I.title"),
                        Text.translatable("advancements.aequitas.collection_bowl_I.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )

                .criterion("collection_bowl_I", CollectionBowlConstructedCriterion.Conditions.create(1))
                .build(consumer, "aequitas:" + "collection_bowl_1");

        Advancement collectionBowlII = Advancement.Builder.create().parent(collectionBowlI)
                .display(
                        AequitasItems.COLLECTION_BOWL_II,
                        Text.translatable("advancements.aequitas.collection_bowl_II.title"),
                        Text.translatable("advancements.aequitas.collection_bowl_II.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )

                .criterion("collection_bowl_II", CollectionBowlConstructedCriterion.Conditions.create(2))
                .build(consumer, "aequitas:" + "collection_bowl_2");

        Advancement collectionBowlIII = Advancement.Builder.create().parent(collectionBowlII)
                .display(
                        AequitasItems.COLLECTION_BOWL_III,
                        Text.translatable("advancements.aequitas.collection_bowl_III.title"),
                        Text.translatable("advancements.aequitas.collection_bowl_III.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )

                .criterion("collection_bowl_II", CollectionBowlConstructedCriterion.Conditions.create(3))
                .build(consumer, "aequitas:" + "collection_bowl_3");

    }
}
