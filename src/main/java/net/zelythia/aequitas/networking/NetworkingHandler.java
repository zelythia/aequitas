package net.zelythia.aequitas.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.block.entity.CollectionBowlBlockEntity;
import net.zelythia.aequitas.block.entity.CraftingPedestalBlockEntity;
import net.zelythia.aequitas.client.mixin.SpriteMixin;
import net.zelythia.aequitas.client.particle.CraftingParticle;
import net.zelythia.aequitas.client.particle.Particles;
import net.zelythia.aequitas.Util;

import java.util.Map;

public class NetworkingHandler {

    private static MinecraftServer server;

    public static final Identifier ESSENCE_UPDATE = new Identifier(Aequitas.MOD_ID, "essence_event");
    public static final Identifier CRAFTING_PARTICLE = new Identifier(Aequitas.MOD_ID, "crafting_particle");
    public static final Identifier COLLECTION_PROGRESS = new Identifier(Aequitas.MOD_ID, "collection_progress");


    @Environment(EnvType.CLIENT)
    public static void onInitializeClient(){
        ClientPlayNetworking.registerGlobalReceiver(NetworkingHandler.ESSENCE_UPDATE, (client, handler, buf, responseSender) -> {
            Map<Item, Long> map = EssencePacket.decode(buf);
            client.execute(() -> EssenceHandler.map.putAll(map));
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkingHandler.CRAFTING_PARTICLE, (client, handler, buf, responseSender) -> {
            BlockPos from = buf.readBlockPos();
            BlockPos to = buf.readBlockPos();
            ItemStack item = buf.readItemStack();

            client.execute(() -> {
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


                    CraftingParticle particle = (CraftingParticle) Particles.spawnParticle(client,Particles.CRAFTING_PARTICLE,false, true, x, y, z, velX,0 ,velZ);
                    if (particle != null){
                        particle.setMaxDistanceSq(Util.distanceSq(x,z,to.getX()+0.5, to.getZ()+0.5));
                        particle.setColor(r/255,g/255,b/255);
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkingHandler.COLLECTION_PROGRESS, (client, handler, buf, responseSender) -> {
            if(client.world == null) return;
            BlockPos pos = buf.readBlockPos();
            float progress = buf.readFloat();
            BlockEntity be = client.world.getBlockEntity(pos);

            client.execute(() -> {
                if(be instanceof CollectionBowlBlockEntity){
                    ((CollectionBowlBlockEntity) be).setClientCollectionProgress(progress);
                }
            });
        });
    }

    public static void updateEssence(){
        if(server == null) return;

        PacketByteBuf buf = PacketByteBufs.create();
        EssencePacket.encode(buf);

        for(ServerPlayerEntity player: PlayerLookup.all(server)){
            ServerPlayNetworking.send(player, NetworkingHandler.ESSENCE_UPDATE, buf);
        }
    }

    public static void sendParticle(CraftingPedestalBlockEntity be, BlockPos from, BlockPos to, ItemStack stack){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(from);
        buf.writeBlockPos(to);
        buf.writeItemStack(stack);

        for(ServerPlayerEntity player: PlayerLookup.tracking(be)){
            ServerPlayNetworking.send(player, NetworkingHandler.CRAFTING_PARTICLE, buf);
        }
    }

    public static void updateCollectionBowl(CollectionBowlBlockEntity be){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(be.getPos());
        buf.writeFloat(be.getServerCollectionProgress());

        for(ServerPlayerEntity player: PlayerLookup.tracking(be)){
            ServerPlayNetworking.send(player, NetworkingHandler.COLLECTION_PROGRESS, buf);
        }
    }


    public static void setServer(MinecraftServer server){
        NetworkingHandler.server = server;
    }

    public static MinecraftServer getServer(){
        return server;
    }
}
