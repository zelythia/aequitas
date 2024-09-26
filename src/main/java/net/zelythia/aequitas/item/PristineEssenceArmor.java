package net.zelythia.aequitas.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class PristineEssenceArmor extends DyeableArmorItem {

    public PristineEssenceArmor(ArmorMaterial armorMaterial, Type type, Settings settings) {
        super(armorMaterial, type, settings);
    }


    @Override
    public int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt("display");
        return nbtCompound != null && nbtCompound.contains("color", 99) ? nbtCompound.getInt("color") : 16777215;
    }


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
            if (player.getInventory().armor.get(3).getItem().equals(AequitasItems.PRISTINE_ESSENCE_HELMET)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 200, 0, false, false, false));
            }

            return;
        }

        //Speed
        if (stack.getItem().equals(AequitasItems.PRISTINE_ESSENCE_LEGGINGS)) {
            if (player.isSprinting()) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 2, 2, false, false, false));
            } else {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20, 1, false, false, false));
            }
            return;
        }


        //CHESTPLATE:
        if (stack.getItem().equals(AequitasItems.PRISTINE_ESSENCE_CHESTPLATE)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 20, 0, false, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20, 0, false, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20, 0, false, false, false));
        }

        if (player.getAbilities().creativeMode || player.isSpectator()) return;

        if (checkSetPristine(player)) {
            player.getAbilities().allowFlying = true;
        } else {
            player.getAbilities().allowFlying = false;
            player.getAbilities().flying = false;
        }

        if (player.getAbilities().flying) ++timeFlown;
        if (player.isOnGround() || player.isTouchingWater()) timeFlown = 0;

        player.sendAbilitiesUpdate(); //FIXME maybe needed in 1.16
    }

    public static boolean checkSetPristine(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.FEET).getItem().equals(AequitasItems.PRISTINE_ESSENCE_BOOTS) && player.getEquippedStack(EquipmentSlot.LEGS).getItem().equals(AequitasItems.PRISTINE_ESSENCE_LEGGINGS) && player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(AequitasItems.PRISTINE_ESSENCE_CHESTPLATE) && player.getEquippedStack(EquipmentSlot.HEAD).getItem().equals(AequitasItems.PRISTINE_ESSENCE_HELMET);
    }
}
