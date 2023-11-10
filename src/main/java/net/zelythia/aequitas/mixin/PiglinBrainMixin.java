package net.zelythia.aequitas.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.zelythia.aequitas.item.ArmorMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {

    @Inject(method = "wearsGoldArmor", at = @At(value="HEAD"), cancellable = true)
    private static void essenceArmorCheck(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {

        Iterable<ItemStack> iterable = entity.getArmorItems();
        for (ItemStack itemStack : iterable) {
            Item item = itemStack.getItem();
            if(item instanceof ArmorItem){
                if(ArmorMaterials.isEssenceArmor(item)){
                    cir.setReturnValue(true);
                    break;
                }
            }
        }

    }

}
