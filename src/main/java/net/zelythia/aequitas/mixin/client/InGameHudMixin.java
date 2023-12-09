package net.zelythia.aequitas.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.client.config.AequitasConfig;
import net.zelythia.aequitas.item.EssenceArmorItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int scaledHeight;
    @Shadow
    private int scaledWidth;
    private static final Identifier FLIGHT_PROGRESS = new Identifier(Aequitas.MOD_ID, "textures/gui/flight_progress.png");

    @Inject(method = "render", at = @At("HEAD"))
    private void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (!this.client.options.hudHidden && AequitasConfig.config.getOrDefault("displayFlightDuration", true)) {
            if (this.client.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Aequitas.PRIMORDIAL_ESSENCE_CHESTPLATE) {
                EssenceArmorItem item = (EssenceArmorItem) this.client.player.getEquippedStack(EquipmentSlot.CHEST).getItem();
                if (item.checkSetPrimordial(this.client.player)) {
                    this.client.getTextureManager().bindTexture(FLIGHT_PROGRESS);

                    int x = this.scaledWidth / 2 - 97;
                    int y = this.scaledHeight - 21;
                    int h = (int) (item.getFlightProgress() * 20);

                    context.drawTexture(FLIGHT_PROGRESS, x, y + 20 - h, 0, 20 - h, 4, h, 32, 32);
                }
            }
        }
    }
}
