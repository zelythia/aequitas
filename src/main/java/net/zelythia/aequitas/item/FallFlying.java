package net.zelythia.aequitas.item;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.client.DoubleJumpEntity;
import net.zelythia.aequitas.networking.NetworkingHandler;

public class FallFlying {

    public static final TagKey<Item> ELYTRA = TagKey.of(RegistryKeys.ITEM, new Identifier("c", "elytra"));

    public static boolean canFly(LivingEntity livingEntity, boolean flag) {
        return flag && !livingEntity.isOnGround() && !livingEntity.hasVehicle() && !livingEntity.hasStatusEffect(StatusEffects.LEVITATION) && canFly(livingEntity);
    }

    private static boolean canFly(LivingEntity livingEntity) {
        ItemStack stack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
        return stack.isIn(ELYTRA) && ElytraItem.isUsable(stack);
    }

    public static void checkFallFlying() {
        ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;

        if (playerEntity != null && startFallFlying(playerEntity)) {
            ClientPlayNetworking.send(NetworkingHandler.START_FLYING, new PacketByteBuf(Unpooled.buffer()));
        }
    }

    public static boolean startFallFlying(PlayerEntity playerEntity) {
        ItemStack itemStack = playerEntity.getEquippedStack(EquipmentSlot.CHEST);

        if (!playerEntity.isOnGround() && !playerEntity.isFallFlying() && !playerEntity.isTouchingWater() && !playerEntity.hasStatusEffect(StatusEffects.LEVITATION)
                && itemStack.isIn(ELYTRA) && ElytraItem.isUsable(itemStack)) {

            if (playerEntity instanceof DoubleJumpEntity) {
                if (((DoubleJumpEntity) playerEntity).canDoubleJump()) return false;
            }

            playerEntity.startFallFlying();
            return true;
        }
        return false;
    }
}
