package net.zelythia.aequitas.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArmorMaterials {

    public static final RegistryEntry<ArmorMaterial> PRIMAL;
    public static final RegistryEntry<ArmorMaterial> PRIMORDIAL;
    public static final RegistryEntry<ArmorMaterial> PRISTINE;

    public ArmorMaterials() {
    }

    static {
        PRIMAL = register("primal_essence",
                Map.of(
                        ArmorItem.Type.HELMET, 4,
                        ArmorItem.Type.CHESTPLATE, 9,
                        ArmorItem.Type.LEGGINGS, 7,
                        ArmorItem.Type.BOOTS, 4
                ),
                25,
                SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                () -> Ingredient.ofItems(AequitasItems.PRIMAL_ESSENCE),
                4.0F,
                0.2F,
                List.of(new ArmorMaterial.Layer(Identifier.of(Aequitas.MOD_ID, "primal_essence"), "", false)));

        PRIMORDIAL = register("primordial_essence",
                Map.of(
                        ArmorItem.Type.HELMET, 5,
                        ArmorItem.Type.CHESTPLATE, 10,
                        ArmorItem.Type.LEGGINGS, 8,
                        ArmorItem.Type.BOOTS, 5
                ),
                25,
                SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                () -> Ingredient.ofItems(AequitasItems.PRIMORDIAL_ESSENCE),
                5.0F,
                0.3F,
                List.of(new ArmorMaterial.Layer(Identifier.of(Aequitas.MOD_ID, "primordial_essence"), "", false)));

        PRISTINE = register("pristine_essence",
                Map.of(
                        ArmorItem.Type.HELMET, 6,
                        ArmorItem.Type.CHESTPLATE, 11,
                        ArmorItem.Type.LEGGINGS, 9,
                        ArmorItem.Type.BOOTS, 6
                ),
                25,
                SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                () -> Ingredient.ofItems(AequitasItems.PRISTINE_ESSENCE),
                6.0F,
                0.4F,
                List.of(
                        new ArmorMaterial.Layer(Identifier.of(Aequitas.MOD_ID, "pristine_essence"), "", true),
                        new ArmorMaterial.Layer(Identifier.of(Aequitas.MOD_ID, "pristine_essence"), "_overlay", false)
                ));
    }

    public static RegistryEntry<ArmorMaterial> register(String id, Map<ArmorItem.Type, Integer> defensePoints, int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredientSupplier, float toughness, float knockbackResistance, List<ArmorMaterial.Layer> layers) {
        ArmorMaterial material = new ArmorMaterial(defensePoints, enchantability, equipSound, repairIngredientSupplier, layers, toughness, knockbackResistance);
        material = Registry.register(Registries.ARMOR_MATERIAL, Identifier.of(Aequitas.MOD_ID, id), material);
        return RegistryEntry.of(material);
    }

    public static boolean isEssenceArmor(Item item) {
        if (item instanceof ArmorItem) {
            RegistryEntry<ArmorMaterial> material = ((ArmorItem) item).getMaterial();
            return material == PRIMAL || material == PRIMORDIAL || material == PRISTINE;
        }
        return false;
    }
}
