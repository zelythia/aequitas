package net.zelythia.aequitas.client.mixin;


import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityClientAccessor {
    @Accessor
    int getRoll();
}
