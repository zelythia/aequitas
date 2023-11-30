package net.zelythia.aequitas.mixin.client;


import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.client.DoubleJumpEntity;
import net.zelythia.aequitas.client.config.AequitasConfig;
import net.zelythia.aequitas.item.FallFlying;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements DoubleJumpEntity {

    private boolean canJump = false;
    private boolean jumped = false;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void tickMovement(CallbackInfo info) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        if (player.isOnGround() || player.isClimbing()) {
            canJump = player.getEquippedStack(EquipmentSlot.FEET).getItem().equals(Aequitas.PRIMAL_ESSENCE_BOOTS) && player.getEquippedStack(EquipmentSlot.LEGS).getItem().equals(Aequitas.PRIMAL_ESSENCE_LEGGINGS) && player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Aequitas.PRIMAL_ESSENCE_CHESTPLATE) && player.getEquippedStack(EquipmentSlot.HEAD).getItem().equals(Aequitas.PRIMAL_ESSENCE_HELMET);
        } else if (player.input.jumping && !player.abilities.flying && player.getVelocity().y < 0 && !player.hasVehicle() && !player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.LEVITATION)) {
            if (canJump && !jumped) {
                canJump = false;
                player.jump();
                player.input.jumping = false;
            }
        }
        jumped = player.input.jumping;
    }

    @Override
    public boolean canDoubleJump() {
        return !(!canJump && !jumped && (AequitasConfig.config.getOrDefault("enableElytra", false) || this.getVelocity().y < 0));
    }


    //Original check for fall flying will never be called
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/client/network/ClientPlayerEntity.getEquippedStack (Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"), method = "tickMovement")
    public ItemStack changeEquippedStack(ItemStack stack) {
        return ItemStack.EMPTY;
    }

    //Custom check for fall flying
    @Inject(at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/client/network/ClientPlayerEntity.getEquippedStack (Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"), method = "tickMovement")
    public void onElytraCheck(CallbackInfo cb) {
        FallFlying.checkFallFlying();
    }
}
