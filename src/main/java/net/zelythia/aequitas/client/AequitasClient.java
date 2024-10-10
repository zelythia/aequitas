package net.zelythia.aequitas.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.Sounds;
import net.zelythia.aequitas.block.entity.BlockEntityTypes;
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
import net.zelythia.aequitas.essence.EssenceHandler;
import net.zelythia.aequitas.item.AequitasItems;
import net.zelythia.aequitas.item.EssenceArmorItem;

import java.text.NumberFormat;

@Environment(EnvType.CLIENT)
public class AequitasClient implements ClientModInitializer {

    private static final Identifier FLIGHT_PROGRESS = new Identifier(Aequitas.MOD_ID, "textures/gui/flight_progress.png");

    @Override
    public void onInitializeClient() {
        Sounds.register();
        NetworkingHandler.onInitializeClient();

        HandledScreens.register(Aequitas.CRAFTING_PEDESTAL_SCREEN_HANDLER, CraftingPedestalScreen::new);
        HandledScreens.register(Aequitas.COLLECTION_BOWL_SCREEN_HANDLER, CollectionBowlScreen::new);
        HandledScreens.register(Aequitas.PORTABLE_PEDESTAL_SCREEN_HANDLER, PortablePedestalScreen::new);

        BlockEntityRendererFactories.register(BlockEntityTypes.SAMPLING_PEDESTAL_BLOCK_ENTITY, SamplingPedestalBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityTypes.CRAFTING_PEDESTAL_BLOCK_ENTITY, CraftingPedestalBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityTypes.COLLECTION_BOWL_BLOCK_ENTITY_I, CollectionBowlBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityTypes.COLLECTION_BOWL_BLOCK_ENTITY_II, CollectionBowlBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(BlockEntityTypes.COLLECTION_BOWL_BLOCK_ENTITY_III, CollectionBowlBlockEntityRenderer::new);

        ParticleFactoryRegistry.getInstance().register(Particles.CRAFTING_PARTICLE, CraftingParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Particles.CATALYST_PARTICLE, CatalystParticle.Factory::new);

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {

            if (stack.getItem() == AequitasItems.PORTABLE_PEDESTAL) {
                if (stack.hasNbt()) {
                    if (stack.getNbt().contains("essence")) {
                        long storedEssence = stack.getNbt().getLong("essence");
                        lines.add(Text.translatable("tooltip.aequitas.portable_pedestal", storedEssence));
                    }
                }
            }

            if (AequitasConfig.config.getOrDefault("showTooltip", false) || Screen.hasShiftDown()) {
                ItemStack singleItem = stack.copy();
                singleItem.setCount(1);
                long value = EssenceHandler.getEssenceValue(singleItem);
                String s = "";
                if (value >= 0L) s += "Essence: " + NumberFormat.getNumberInstance().format(value);
                if (stack.getCount() > 1)
                    s += " (" + NumberFormat.getNumberInstance().format(value * stack.getCount()) + ")";
                if (value >= 0L) lines.addAll(Text.of(s).getWithStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            }
        });


        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client != null && !client.options.hudHidden && AequitasConfig.config.getOrDefault("displayFlightDuration", true)) {
                if (client.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == AequitasItems.PRIMORDIAL_ESSENCE_CHESTPLATE) {
                    EssenceArmorItem item = (EssenceArmorItem) client.player.getEquippedStack(EquipmentSlot.CHEST).getItem();
                    if (EssenceArmorItem.checkSetPrimordial(client.player)) {
                        client.getTextureManager().bindTexture(FLIGHT_PROGRESS);

                        int x = client.getWindow().getScaledWidth() / 2 - 97;
                        int y = client.getWindow().getScaledHeight() - 21;
                        int h = (int) (item.getFlightProgress() * 20);

                        matrices.drawTexture(FLIGHT_PROGRESS, x, y + 20 - h, 0, 20 - h, 4, h, 32, 32);
                    }
                }
            }
        });

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            return tintIndex > 0 ? -1 : ((DyeableItem)stack.getItem()).getColor(stack);
        }, AequitasItems.PRISTINE_ESSENCE_HELMET, AequitasItems.PRISTINE_ESSENCE_CHESTPLATE, AequitasItems.PRISTINE_ESSENCE_LEGGINGS, AequitasItems.PRISTINE_ESSENCE_BOOTS);
    }
}
