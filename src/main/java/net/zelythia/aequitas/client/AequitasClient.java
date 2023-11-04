package net.zelythia.aequitas.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.client.block.entity.CollectionBowlBlockEntityRenderer;
import net.zelythia.aequitas.client.block.entity.CraftingPedestalBlockEntityRenderer;
import net.zelythia.aequitas.client.block.entity.SamplingPedestalBlockEntityRenderer;
import net.zelythia.aequitas.client.config.AequitasConfig;
import net.zelythia.aequitas.client.particle.CatalystParticle;
import net.zelythia.aequitas.client.particle.CraftingParticle;
import net.zelythia.aequitas.client.particle.Particles;
import net.zelythia.aequitas.client.screen.CollectionBowlScreen;
import net.zelythia.aequitas.client.screen.CraftingPedestalScreen;
import net.zelythia.aequitas.client.screen.PortablePedestalScreen;
import net.zelythia.aequitas.item.EssenceArmorItem;
import net.zelythia.aequitas.networking.NetworkingHandler;

import java.text.NumberFormat;

@Environment(EnvType.CLIENT)
public class AequitasClient implements ClientModInitializer {

    private static final Identifier FLIGHT_PROGRESS = new Identifier(Aequitas.MOD_ID, "textures/gui/flight_progress.png");

    @Override
    public void onInitializeClient() {
        NetworkingHandler.onInitializeClient();

        ScreenRegistry.register(Aequitas.CRAFTING_PEDESTAL_SCREEN_HANDLER, CraftingPedestalScreen::new);
        ScreenRegistry.register(Aequitas.COLLECTION_BOWL_SCREEN_HANDLER, CollectionBowlScreen::new);
        ScreenRegistry.register(Aequitas.PORTABLE_PEDESTAL_SCREEN_HANDLER, PortablePedestalScreen::new);

        BlockEntityRendererRegistry.INSTANCE.register(Aequitas.SAMPLING_PEDESTAL_BLOCK_ENTITY, SamplingPedestalBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(Aequitas.CRAFTING_PEDESTAL_BLOCK_ENTITY, CraftingPedestalBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(Aequitas.COLLECTION_BOWL_BLOCK_ENTITY_I, CollectionBowlBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(Aequitas.COLLECTION_BOWL_BLOCK_ENTITY_II, CollectionBowlBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(Aequitas.COLLECTION_BOWL_BLOCK_ENTITY_III, CollectionBowlBlockEntityRenderer::new);

        ParticleFactoryRegistry.getInstance().register(Particles.CRAFTING_PARTICLE, CraftingParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Particles.CATALYST_PARTICLE, CatalystParticle.Factory::new);

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {

            if(stack.getItem() == Aequitas.PORTABLE_PEDESTAL_ITEM){
                if(stack.hasTag()){
                    if(stack.getTag().contains("essence")){
                        long storedEssence = stack.getTag().getLong("essence");
                        lines.add(new TranslatableText("tooltip.aequitas.portable_pedestal", storedEssence));
                    }
                }
            }

            if(AequitasConfig.config.getOrDefault("showTooltip", false) || Screen.hasShiftDown()){
                long value = EssenceHandler.getEssenceValue(stack.getItem());
                String s = "";
                if(value >= 0L) s += "Essence: " + NumberFormat.getNumberInstance().format(value);
                if(stack.getCount()>1) s+= " ("+NumberFormat.getNumberInstance().format(value*stack.getCount())+")";
                if(value >= 0L) lines.add(new LiteralText(s).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            }
        });


        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client != null && !client.options.hudHidden && AequitasConfig.config.getOrDefault("displayFlightDuration", true)){
                if(client.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Aequitas.PRIMORDIAL_ESSENCE_CHESTPLATE){
                    EssenceArmorItem item = (EssenceArmorItem) client.player.getEquippedStack(EquipmentSlot.CHEST).getItem();
                    if(item.checkSetPrimordial(client.player)){
                        client.getTextureManager().bindTexture(FLIGHT_PROGRESS);

                        int x = client.getWindow().getScaledWidth() / 2 - 97;
                        int y = client.getWindow().getScaledHeight() - 21;
                        int h = (int) (item.getFlightProgress() * 20);


                        DrawableHelper.drawTexture(matrices, x, y +20 -h, 0, 20 -h, 4, h, 32, 32);
                    }
                }
            }
        });
    }
}
