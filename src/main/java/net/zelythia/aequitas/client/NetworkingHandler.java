package net.zelythia.aequitas.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.Util;
import net.zelythia.aequitas.block.entity.CollectionBowlBlockEntity;
import net.zelythia.aequitas.client.particle.CraftingParticle;
import net.zelythia.aequitas.client.particle.Particles;
import net.zelythia.aequitas.compat.LootTableParser;
import net.zelythia.aequitas.mixin.client.SpriteContentsMixin;
import net.zelythia.aequitas.networking.EssencePacket;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.zelythia.aequitas.networking.NetworkingHandler.C2S_UPDATE_FILTER;

@Environment(EnvType.CLIENT)
public class NetworkingHandler {
    public static final Map<Identifier, JsonObject> LOOTTABLES = new ConcurrentHashMap<>();
    public static boolean loottablesUpdated = false;




    public static void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(net.zelythia.aequitas.networking.NetworkingHandler.ESSENCE_UPDATE, (client, handler, buf, responseSender) -> {
            Map<Item, Long> map = EssencePacket.decode(buf);
            client.execute(() -> EssenceHandler.map.putAll(map));
        });

        ClientPlayNetworking.registerGlobalReceiver(net.zelythia.aequitas.networking.NetworkingHandler.CRAFTING_PARTICLE, (client, handler, buf, responseSender) -> {
            BlockPos from = buf.readBlockPos();
            BlockPos to = buf.readBlockPos();
            ItemStack item = buf.readItemStack();

            client.execute(() -> {
                if (!item.isEmpty()) {
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


                    float r = 1F;
                    float g = 1F;
                    float b = 1F;
                    int div = 1;

                    BakedModel itemModel = client.getItemRenderer().getModel(item, client.world, client.player, 0);
                    if (itemModel != null) {


                        NativeImage image = ((SpriteContentsMixin)itemModel.getParticleSprite().getContents()).getImage();
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

                    CraftingParticle particle = (CraftingParticle) Particles.spawnParticle(client, Particles.CRAFTING_PARTICLE, false, true, x, y, z, velX, 0, velZ);
                    if (particle != null) {
                        particle.setMaxDistanceSq(Util.distanceSq(x, z, to.getX() + 0.5, to.getZ() + 0.5));
                        particle.setColor(r / 255, g / 255, b / 255);
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(net.zelythia.aequitas.networking.NetworkingHandler.COLLECTION_PROGRESS, (client, handler, buf, responseSender) -> {
            if (client.world == null) return;
            BlockPos pos = buf.readBlockPos();
            float progress = buf.readFloat();
            BlockEntity be = client.world.getBlockEntity(pos);

            client.execute(() -> {
                if (be instanceof CollectionBowlBlockEntity) {
                    ((CollectionBowlBlockEntity) be).setClientCollectionProgress(progress);
                }
            });
        });


        ClientPlayNetworking.registerGlobalReceiver(net.zelythia.aequitas.networking.NetworkingHandler.SEND_LOOT_INFO, (client, handler, buf, responseSender) -> {
            LOOTTABLES.clear();

            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                Identifier identifier = buf.readIdentifier();
                JsonElement json = LootTableParser.readJson(buf);
                LOOTTABLES.put(identifier, json.getAsJsonObject());
            }

            client.execute(() -> {
                Aequitas.LOGGER.error("Updated loottables");
                loottablesUpdated = true;
            });


        });
    }

    public static void updatePortablePedestalSearchProperties(int syncId, String filter, int page) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(syncId);
        buf.writeString(filter);
        buf.writeInt(page);
        ClientPlayNetworking.send(C2S_UPDATE_FILTER, buf);
    }


    public static boolean updateLootTables(){
        loottablesUpdated = false;
        ClientPlayNetworking.send(net.zelythia.aequitas.networking.NetworkingHandler.ASK_SYNC_INFO, PacketByteBufs.empty());
        long start = System.currentTimeMillis();

        while(!loottablesUpdated){
            long current = System.currentTimeMillis();
            if(current - start > 10000){
                Aequitas.LOGGER.error("Failed to sync loot tables (took more than 10s to sync). EMI or REI won't work");
                return false;
            }
        }

        return true;
    }
}
