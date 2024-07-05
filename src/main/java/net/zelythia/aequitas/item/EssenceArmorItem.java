package net.zelythia.aequitas.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.world.World;

public class EssenceArmorItem extends ArmorItem {
    public EssenceArmorItem(ArmorMaterial material, ArmorItem.Type type, Settings settings) {
        super(material, type, settings);
    }

    private static final int MAX_FLY_TIME = 600;
    private int timeFlown = 0;

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        LivingEntity livingEntity = (LivingEntity) entity;
        PlayerEntity player = (PlayerEntity) livingEntity;

        if (!player.getInventory().armor.contains(stack)) return;


        //Give Water breathing when wearing a helmet
        //slot = 3 can be hotbar and armor
        if (((ArmorItem) stack.getItem()).getSlotType() == EquipmentSlot.HEAD) {
            int time = 200;
            if (player.getInventory().armor.get(3).getItem().equals(AequitasItems.PRIMORDIAL_ESSENCE_HELMET))
                time = time * 3;

            if (!player.isSubmergedIn(FluidTags.WATER) || player.getInventory().armor.get(3).getItem().equals(AequitasItems.PRISTINE_ESSENCE_HELMET)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, time, 0, false, false, false));
            }

            return;
        }

        //Give Speed I for Primal Essence Leggings
        if (stack.getItem().equals(AequitasItems.PRIMAL_ESSENCE_LEGGINGS)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20, 0, false, false, false));
            return;
        }
        //Give Speed II for Primal Essence Leggings
        if (stack.getItem().equals(AequitasItems.PRIMORDIAL_ESSENCE_LEGGINGS)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20, 1, false, false, false));
            return;
        }
        if (stack.getItem().equals(AequitasItems.PRISTINE_ESSENCE_LEGGINGS)) {
            if (player.isSprinting()) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20, 2, false, false, false));
            } else {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20, 1, false, false, false));
            }
        }


        //CHESTPLATE:
        if (stack.getItem().equals(AequitasItems.PRIMORDIAL_ESSENCE_CHESTPLATE)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 20, 0, false, false, false));
        } else if (stack.getItem().equals(AequitasItems.PRISTINE_ESSENCE_CHESTPLATE)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 20, 0, false, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20, 0, false, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20, 0, false, false, false));
        }

        if (player.getAbilities().creativeMode || player.isSpectator()) return;

        if (checkSetPristine(player) || (checkSetPrimordial(player) && timeFlown <= MAX_FLY_TIME)) {
            player.getAbilities().allowFlying = true;
        } else {
            player.getAbilities().allowFlying = false;
            player.getAbilities().flying = false;
        }

        if (player.getAbilities().flying) ++timeFlown;
        if (player.isOnGround() || player.isTouchingWater()) timeFlown = 0;

        player.sendAbilitiesUpdate(); //FIXME maybe needed in 1.16
    }

    public float getFlightProgress() {
        return (float) (MAX_FLY_TIME - timeFlown) / MAX_FLY_TIME;
    }


    public static boolean checkSetPrimal(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.FEET).getItem().equals(AequitasItems.PRIMAL_ESSENCE_BOOTS) && player.getEquippedStack(EquipmentSlot.LEGS).getItem().equals(AequitasItems.PRIMAL_ESSENCE_LEGGINGS) && player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(AequitasItems.PRIMAL_ESSENCE_CHESTPLATE) && player.getEquippedStack(EquipmentSlot.HEAD).getItem().equals(AequitasItems.PRIMAL_ESSENCE_HELMET);
    }

    public static boolean checkSetPrimordial(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.FEET).getItem().equals(AequitasItems.PRIMORDIAL_ESSENCE_BOOTS) && player.getEquippedStack(EquipmentSlot.LEGS).getItem().equals(AequitasItems.PRIMORDIAL_ESSENCE_LEGGINGS) && player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(AequitasItems.PRIMORDIAL_ESSENCE_CHESTPLATE) && player.getEquippedStack(EquipmentSlot.HEAD).getItem().equals(AequitasItems.PRIMORDIAL_ESSENCE_HELMET);
    }

    public static boolean checkSetPristine(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.FEET).getItem().equals(AequitasItems.PRISTINE_ESSENCE_BOOTS) && player.getEquippedStack(EquipmentSlot.LEGS).getItem().equals(AequitasItems.PRISTINE_ESSENCE_LEGGINGS) && player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(AequitasItems.PRISTINE_ESSENCE_CHESTPLATE) && player.getEquippedStack(EquipmentSlot.HEAD).getItem().equals(AequitasItems.PRISTINE_ESSENCE_HELMET);
    }
}
