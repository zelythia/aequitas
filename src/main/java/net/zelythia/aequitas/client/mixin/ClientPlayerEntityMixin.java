package net.zelythia.aequitas.client.mixin;


import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.client.config.AequitasConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    private boolean canJump = false;
    private boolean jumped = false;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void tickMovement(CallbackInfo info) {
//        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
//
//        if (player.isOnGround() || player.isClimbing()) {
//            canJump = player.getEquippedStack(EquipmentSlot.FEET).getItem().equals(Aequitas.PRIMAL_ESSENCE_BOOTS) && player.getEquippedStack(EquipmentSlot.LEGS).getItem().equals(Aequitas.PRIMAL_ESSENCE_LEGGINGS) && player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Aequitas.PRIMAL_ESSENCE_CHESTPLATE) && player.getEquippedStack(EquipmentSlot.HEAD).getItem().equals(Aequitas.PRIMAL_ESSENCE_HELMET);
//        }
//        else if(player.input.jumping && !player.abilities.flying && player.getVelocity().y < 0 && !player.isFallFlying() && !player.hasVehicle() && !player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.LEVITATION)){
//            if(canJump && !jumped){
//                canJump = false;
//                player.jump();
//                player.input.jumping = false;
//            }
//        }
//        jumped = player.input.jumping;
    }

//    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;checkFallFlying()Z"))
//    private boolean replaceFallFlying(ClientPlayerEntity player){
////        System.out.println(!canJump && !jumped && (AequitasConfig.config.getOrDefault("enableElytra", false) || player.getVelocity().y < 0 ) && player.checkFallFlying());
////        && (AequitasConfig.config.getOrDefault("enableElytra", false) || player.getVelocity().y < 0 )
////        !canJump && !jumped  &&
//        return  player.checkFallFlying();
//    }
//
//    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
//    private Item redirectGetItem(ItemStack stack){
//        return stack.getItem() == Aequitas.PRIMAL_ESSENCE_CHESTPLATE ? Items.ELYTRA : stack.getItem();
//    }
}
