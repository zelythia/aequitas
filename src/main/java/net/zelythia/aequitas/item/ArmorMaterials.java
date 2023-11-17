package net.zelythia.aequitas.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Lazy;
import net.zelythia.aequitas.Aequitas;

import java.util.function.Supplier;

public enum ArmorMaterials implements ArmorMaterial {
    PRIMAL("primal_essence", 37, new int[]{4, 7, 9, 4}, 25, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 4.0F, 0.2F, () -> Ingredient.ofItems(Aequitas.PRIMAL_ESSENCE)),
    PRIMORDIAL("primal_essence", 37, new int[]{5, 8, 10, 5}, 30, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 5.0F, 0.3F, () -> Ingredient.ofItems(Aequitas.PRIMAL_ESSENCE)),
    PRISTINE("pristine_essence", 37, new int[]{6, 9, 11, 6}, 35, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 6.0F, 0.4F, () -> Ingredient.ofItems(Aequitas.PRIMAL_ESSENCE));

    private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Lazy<Ingredient> repairIngredientSupplier;

    ArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier repairIngredientSupplier) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredientSupplier = new Lazy(repairIngredientSupplier);
    }

    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
    }

    public int getProtectionAmount(EquipmentSlot slot) {
        return this.protectionAmounts[slot.getEntitySlotId()];
    }

    public int getEnchantability() {
        return this.enchantability;
    }

    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    public Ingredient getRepairIngredient() {
        return (Ingredient)this.repairIngredientSupplier.get();
    }

    @Environment(EnvType.CLIENT)
    public String getName() {
        return this.name;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public static boolean isEssenceArmor(Item item){
        if(item instanceof ArmorItem){
            ArmorMaterial material = ((ArmorItem) item).getMaterial();
            return material == PRIMAL || material == PRIMORDIAL || material == PRISTINE;
        }
        return false;
    }
}
