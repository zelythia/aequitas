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
        ScreenRegistry.register(Aequitas.CRAFTING_PEDESTAL_SCREEN_HANDLER, CraftingPedestalScreen::new);
        ScreenRegistry.register(Aequitas.COLLECTION_BOWL_SCREEN_HANDLER, CollectionBowlScreen::new);

        BlockEntityRendererRegistry.INSTANCE.register(Aequitas.SAMPLING_PEDESTAL_BLOCK_ENTITY, SamplingPedestalBlockEntityRenderer::new);

        ParticleFactoryRegistry.getInstance().register(Aequitas.CRAFTING_PARTICLE, CraftingParticle.Factory::new);

        ClientPlayNetworking.registerGlobalReceiver(NetworkingHandler.ESSENCE_UPDATE, (client, handler, buf, responseSender) -> {
            EssenceHandler.map.putAll(EssencePacket.decode(buf));
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkingHandler.CRAFTING_PARTICLE, (client, handler, buf, responseSender) -> {
            BlockPos from = buf.readBlockPos();
            BlockPos to = buf.readBlockPos();
            ItemStack item = buf.readItemStack();

            if(!item.isEmpty()){
                double x = 0.5 + from.getX()+ (Math.random() * 2.0 - 1.0) * 0.15;
                double y = 1.2 + from.getY();
                double z = 0.5 + from.getZ()+ (Math.random() * 2.0 - 1.0) * 0.15;


                double velX = (to.getX()+0.5) - x;
                double velZ = to.getZ()+0.5 - z;
                double len = Math.sqrt(velX * velX + velZ * velZ);

                velX = velX/len;
                velZ = velZ/len;

                velX *= 0.1;
                velZ *= 0.1;



                float r = 1F;
                float g = 1F;
                float b = 1F;
                int div = 1;

                BakedModel itemModel = client.getItemRenderer().getHeldItemModel(item, client.world, client.player);
                if(itemModel != null){
                    NativeImage image = ((SpriteMixin) itemModel.getSprite()).getImages()[0];
                    div = image.getHeight()* image.getWidth();

                    for(int img_x = 0; img_x < image.getWidth(); img_x++){
                        for(int img_y = 0; img_y < image.getHeight(); img_y++){
                            int color = image.getPixelColor(img_x, img_y);
                            r += color >> 0 & 255;
                            g += color >> 8 & 255;
                            b += color >> 16 & 255;

//                            div++;
                        }
                    }



                    r = (int) (r/div);
                    g = (int) (g/div);
                    b = (int) (b/div);
                }


                CraftingParticle particle = (CraftingParticle) client.particleManager.addParticle(Aequitas.CRAFTING_PARTICLE, x, y, z, velX,0 ,velZ);
                if (particle != null){
                    particle.max_distance = Vec2d.distanceSq(x,z,to.getX()+0.5, to.getZ()+0.5);
                    particle.setColor(r/255,g/255,b/255);
                }
            }


        });

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
