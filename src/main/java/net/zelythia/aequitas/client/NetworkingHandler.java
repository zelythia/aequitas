package net.zelythia.aequitas.client;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.Util;
import net.zelythia.aequitas.block.entity.CollectionBowlBlockEntity;
import net.zelythia.aequitas.client.particle.CraftingParticle;
import net.zelythia.aequitas.client.particle.Particles;
import net.zelythia.aequitas.essence.EssenceHandler;
import net.zelythia.aequitas.mixin.client.SpriteContentsMixin;
import net.zelythia.aequitas.networking.packet.CollectionBowlProgress;
import net.zelythia.aequitas.networking.packet.CraftingParticlePacket;
import net.zelythia.aequitas.networking.packet.EssencePacket;
import net.zelythia.aequitas.networking.packet.UpdatePortablePedestalFilter;
import net.zelythia.aequitas.networking.packet.compat.LootInfoReq;
import net.zelythia.aequitas.networking.packet.compat.LootInfoRes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;


@Environment(EnvType.CLIENT)
public class NetworkingHandler {
    public static final Map<Identifier, JsonObject> LOOTTABLES = new ConcurrentHashMap<>();
    public static AtomicBoolean loottablesUpdated = new AtomicBoolean(false);

    public static void onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(EssencePacket.PACKET_ID, (payload, context) -> {
            context.client().execute(() -> EssenceHandler.map.putAll(payload.map()));
        });

        ClientPlayNetworking.registerGlobalReceiver(CraftingParticlePacket.PACKET_ID, (payload, context) -> {
            MinecraftClient client = context.client();

            BlockPos from = payload.from();
            BlockPos to = payload.to();
            ItemStack item = payload.stack();

            client.execute(() -> {
                if (!item.isEmpty()) {
                    float r = 1F;
                    float g = 1F;
                    float b = 1F;
                    int div = 1;

                    BakedModel itemModel = client.getItemRenderer().getModel(item, client.world, client.player, 0);
                    if (itemModel != null) {
                        NativeImage image = ((SpriteContentsMixin) itemModel.getParticleSprite().getContents()).getImage();
                        div = image.getHeight() * image.getWidth();

                        for (int img_x = 0; img_x < image.getWidth(); img_x++) {
                            for (int img_y = 0; img_y < image.getHeight(); img_y++) {
                                int color = image.getColor(img_x, img_y);
                                r += color >> 0 & 255;
                                g += color >> 8 & 255;
                                b += color >> 16 & 255;

//                            div++;
                            }
                        }

                        r = (int) (r / div);
                        g = (int) (g / div);
                        b = (int) (b / div);
                    }


                    int i = 3;
                    if (client.options.getParticles().getValue() == ParticlesMode.DECREASED) i = 2;
                    else if (client.options.getParticles().getValue() == ParticlesMode.MINIMAL) i = 1;
                    for (; i > 0; i--) {
                        double x = 0.5 + from.getX() + (Math.random() * 2.0 - 1.0) * 0.15;
                        double y = 1.2 + from.getY();
                        double z = 0.5 + from.getZ() + (Math.random() * 2.0 - 1.0) * 0.15;

                        double velX = (to.getX() + 0.5) - x;
                        double velZ = to.getZ() + 0.5 - z;
                        double len = Math.sqrt(velX * velX + velZ * velZ);

                        velX = velX / len;
                        velZ = velZ / len;

                        velX *= 0.1;
                        velZ *= 0.1;

                        CraftingParticle particle = (CraftingParticle) Particles.spawnParticle(client, Particles.CRAFTING_PARTICLE, false, true, x, y, z, velX, 0, velZ);
                        if (particle != null) {
                            particle.setMaxDistanceSq(Util.distanceSq(x, z, to.getX() + 0.5, to.getZ() + 0.5));
                            particle.setColor(r / 255, g / 255, b / 255);
                        }
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(CollectionBowlProgress.PACKET_ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().world == null) return;
                BlockEntity be = context.client().world.getBlockEntity(payload.pos());

                if (be instanceof CollectionBowlBlockEntity) {
                    ((CollectionBowlBlockEntity) be).setClientCollectionProgress(payload.progress());
                }
            });
        });


        ClientPlayNetworking.registerGlobalReceiver(LootInfoRes.PACKET_ID, (payload, context) -> {
            LOOTTABLES.clear();
            LOOTTABLES.putAll(payload.loottables());

            context.client().execute(() -> {
                Aequitas.LOGGER.info("Updated loottables");
                loottablesUpdated.set(true);
            });
        });
    }

    public static void updatePortablePedestalSearchProperties(int syncId, String filter, int page) {
        ClientPlayNetworking.send(new UpdatePortablePedestalFilter(syncId, filter, page));
    }


    public static boolean updateLootTables() {
        loottablesUpdated.set(false);
        ClientPlayNetworking.send(new LootInfoReq());
        long start = System.currentTimeMillis();

        while (!loottablesUpdated.get()) {
            long current = System.currentTimeMillis();
            if (current - start > 10000) {
                Aequitas.LOGGER.error("Failed to sync loot tables (took more than 10s to sync). EMI or REI won't work");
                return false;
            }
        }

        return true;
    }
}
