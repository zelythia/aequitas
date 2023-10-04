package net.zelythia.aequitas.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.client.block.entity.CollectionBowlBlockEntityRenderer;
import net.zelythia.aequitas.client.block.entity.SamplingPedestalBlockEntityRenderer;
import net.zelythia.aequitas.client.config.AequitasConfig;
import net.zelythia.aequitas.client.particle.CatalystParticle;
import net.zelythia.aequitas.client.particle.CraftingParticle;
import net.zelythia.aequitas.client.particle.Particles;
import net.zelythia.aequitas.client.screen.CollectionBowlScreen;
import net.zelythia.aequitas.client.screen.CraftingPedestalScreen;
import net.zelythia.aequitas.networking.NetworkingHandler;

@Environment(EnvType.CLIENT)
public class AequitasClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NetworkingHandler.onInitializeClient();

        ScreenRegistry.register(Aequitas.CRAFTING_PEDESTAL_SCREEN_HANDLER, CraftingPedestalScreen::new);
        ScreenRegistry.register(Aequitas.COLLECTION_BOWL_SCREEN_HANDLER, CollectionBowlScreen::new);

        BlockEntityRendererRegistry.INSTANCE.register(Aequitas.SAMPLING_PEDESTAL_BLOCK_ENTITY, SamplingPedestalBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(Aequitas.COLLECTION_BOWL_BLOCK_ENTITY_I, CollectionBowlBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(Aequitas.COLLECTION_BOWL_BLOCK_ENTITY_II, CollectionBowlBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(Aequitas.COLLECTION_BOWL_BLOCK_ENTITY_III, CollectionBowlBlockEntityRenderer::new);

        ParticleFactoryRegistry.getInstance().register(Particles.CRAFTING_PARTICLE, CraftingParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Particles.CATALYST_PARTICLE, CatalystParticle.Factory::new);

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if(AequitasConfig.config.getOrDefault("showTooltip", false) || Screen.hasShiftDown()){
                long value = EssenceHandler.getEssenceValue(stack.getItem());
                String s = "";
                if(value >= 0L) s += "Essence: " + value;
                if(stack.getCount()>1) s+= " ("+value*stack.getCount()+")";
                if(value >= 0L) lines.add(new LiteralText(s).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
            }
        });
    }
}
