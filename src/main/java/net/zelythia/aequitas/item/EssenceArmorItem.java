package net.zelythia.aequitas.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.World;
import net.zelythia.aequitas.Aequitas;

public class EssenceArmorItem extends ArmorItem {
    public EssenceArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
        super(material, slot, settings);
    }

    private static final int MAX_FLY_TIME = 600;
    private int timeFlown = 0;

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        LivingEntity livingEntity = (LivingEntity) entity;
        PlayerEntity player = (PlayerEntity) livingEntity;


        if (slot == EquipmentSlot.HEAD.getEntitySlotId() && ArmorMaterials.isEssenceArmor(stack.getItem())) {
            if (!player.isSubmergedIn(FluidTags.WATER)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 200, 0, false, false, true));
            }

            return;
        }

        //Code for the chestplate
        if (player.abilities.creativeMode || player.isSpectator()) return;

        if (checkSetPristine(player) || (checkSetPrimordial(player) && timeFlown <= MAX_FLY_TIME)) {
            player.abilities.allowFlying = true;
        } else {
            player.abilities.allowFlying = false;
            player.abilities.flying = false;
        }

        if (player.abilities.flying) ++timeFlown;
        if (player.isOnGround() || player.isTouchingWater()) timeFlown = 0;
    }

    public float getFlightProgress() {
        return (float) (MAX_FLY_TIME - timeFlown) / MAX_FLY_TIME;
    }


    public static boolean checkSetPrimal(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.FEET).getItem().equals(Aequitas.PRIMAL_ESSENCE_BOOTS) && player.getEquippedStack(EquipmentSlot.LEGS).getItem().equals(Aequitas.PRIMAL_ESSENCE_LEGGINGS) && player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Aequitas.PRIMAL_ESSENCE_CHESTPLATE) && player.getEquippedStack(EquipmentSlot.HEAD).getItem().equals(Aequitas.PRIMAL_ESSENCE_HELMET);
    }

    public static boolean checkSetPrimordial(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.FEET).getItem().equals(Aequitas.PRIMORDIAL_ESSENCE_BOOTS) && player.getEquippedStack(EquipmentSlot.LEGS).getItem().equals(Aequitas.PRIMORDIAL_ESSENCE_LEGGINGS) && player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Aequitas.PRIMORDIAL_ESSENCE_CHESTPLATE) && player.getEquippedStack(EquipmentSlot.HEAD).getItem().equals(Aequitas.PRIMORDIAL_ESSENCE_HELMET);
    }

    public static boolean checkSetPristine(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.FEET).getItem().equals(Aequitas.PRISTINE_ESSENCE_BOOTS) && player.getEquippedStack(EquipmentSlot.LEGS).getItem().equals(Aequitas.PRISTINE_ESSENCE_LEGGINGS) && player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Aequitas.PRISTINE_ESSENCE_CHESTPLATE) && player.getEquippedStack(EquipmentSlot.HEAD).getItem().equals(Aequitas.PRISTINE_ESSENCE_HELMET);
    }
}
