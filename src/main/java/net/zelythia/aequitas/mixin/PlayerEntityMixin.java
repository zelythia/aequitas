package net.zelythia.aequitas.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.zelythia.aequitas.Aequitas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void handleFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Boolean> cir){
        boolean primordial = this.getEquippedStack(EquipmentSlot.FEET).getItem().equals(Aequitas.PRIMORDIAL_ESSENCE_BOOTS) && this.getEquippedStack(EquipmentSlot.LEGS).getItem().equals(Aequitas.PRIMORDIAL_ESSENCE_LEGGINGS) && this.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Aequitas.PRIMORDIAL_ESSENCE_CHESTPLATE) && this.getEquippedStack(EquipmentSlot.HEAD).getItem().equals(Aequitas.PRIMORDIAL_ESSENCE_HELMET);
        if(primordial) cir.setReturnValue(false);
    }
}
