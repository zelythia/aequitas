package net.zelythia.aequitas.client;

import com.sun.javafx.geom.Vec2d;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.client.block.entity.CollectionBowlBlockEntityRenderer;
import net.zelythia.aequitas.client.block.entity.SamplingPedestalBlockEntityRenderer;
import net.zelythia.aequitas.client.config.AequitasConfig;
import net.zelythia.aequitas.client.mixins.SpriteMixin;
import net.zelythia.aequitas.client.screen.CollectionBowlScreen;
import net.zelythia.aequitas.client.screen.CraftingPedestalScreen;
import net.zelythia.aequitas.networking.EssencePacket;
import net.zelythia.aequitas.networking.NetworkingHandler;
import net.zelythia.aequitas.screen.CollectionBowlScreenHandler;

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

        ParticleFactoryRegistry.getInstance().register(Aequitas.CRAFTING_PARTICLE, CraftingParticle.Factory::new);


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
