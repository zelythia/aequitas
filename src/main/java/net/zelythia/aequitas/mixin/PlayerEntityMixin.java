package net.zelythia.aequitas.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.zelythia.aequitas.item.AequitasItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    //Reducing the fall height when having the Primal Essence Boots equipped
    @ModifyVariable(method = "handleFallDamage", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    public float lowerFallDamage(float fallDistance) {
        if (((PlayerEntity) (Object) this).getEquippedStack(EquipmentSlot.FEET).getItem().equals(AequitasItems.PRIMAL_ESSENCE_BOOTS)) {
            if (fallDistance >= 2) {
                return fallDistance - 2;
            }
        }
        return fallDistance;
    }

    //Cancelling all fall damage when the player has the primordial armor set
    @Inject(method = "handleFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;handleFallDamage(FFLnet/minecraft/entity/damage/DamageSource;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (((PlayerEntity) (Object) this).getEquippedStack(EquipmentSlot.FEET).getItem().equals(AequitasItems.PRIMORDIAL_ESSENCE_BOOTS))
            cir.setReturnValue(false);
    }
}
