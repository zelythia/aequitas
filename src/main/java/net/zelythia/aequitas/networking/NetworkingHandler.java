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
import net.zelythia.aequitas.Util;
import net.zelythia.aequitas.block.entity.CollectionBowlBlockEntity;
import net.zelythia.aequitas.block.entity.CraftingPedestalBlockEntity;
import net.zelythia.aequitas.client.particle.CraftingParticle;
import net.zelythia.aequitas.client.particle.Particles;
import net.zelythia.aequitas.item.FallFlying;
import net.zelythia.aequitas.mixin.client.SpriteContentsMixin;
import net.zelythia.aequitas.screen.PortablePedestalScreenHandler;

import java.util.Map;

public class NetworkingHandler {

    private static MinecraftServer server;

    public static final Identifier ESSENCE_UPDATE = new Identifier(Aequitas.MOD_ID, "essence_event");
    public static final Identifier CRAFTING_PARTICLE = new Identifier(Aequitas.MOD_ID, "crafting_particle");
    public static final Identifier COLLECTION_PROGRESS = new Identifier(Aequitas.MOD_ID, "collection_progress");

    public static final Identifier START_FLYING = new Identifier(Aequitas.MOD_ID, "start_flying");


    public static final Identifier C2S_UPDATE_FILTER = new Identifier(Aequitas.MOD_ID, "update_filter");

    public static void onInitialize() {

        ServerPlayNetworking.registerGlobalReceiver(START_FLYING, (server1, player, handler, buf, responseSender) -> {
            server1.execute(() -> {
                if (!FallFlying.startFallFlying(player)) {
                    player.stopFallFlying();
                }
            });
        });


        ServerPlayNetworking.registerGlobalReceiver(NetworkingHandler.C2S_UPDATE_FILTER, (server1, player, handler, buf, responseSender) -> {
            int syncId = buf.readInt();
            String filter = buf.readString();
            int page = buf.readInt();

            server1.execute(() -> {
                if (player.currentScreenHandler.syncId == syncId && player.currentScreenHandler.canUse(player)) {
                    if (player.currentScreenHandler instanceof PortablePedestalScreenHandler) {
                        ((PortablePedestalScreenHandler) player.currentScreenHandler).updateSearchProperties(filter, page);
                    }
                }
            });
        });
    }


    public static void updateEssence() {
        if (server == null) return;

        PacketByteBuf buf = PacketByteBufs.create();
        EssencePacket.encode(buf);

        for (ServerPlayerEntity player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, NetworkingHandler.ESSENCE_UPDATE, buf);
        }
    }

    public static void sendParticle(CraftingPedestalBlockEntity be, BlockPos from, BlockPos to, ItemStack stack) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(from);
        buf.writeBlockPos(to);
        buf.writeItemStack(stack);

        for (ServerPlayerEntity player : PlayerLookup.tracking(be)) {
            ServerPlayNetworking.send(player, NetworkingHandler.CRAFTING_PARTICLE, buf);
        }
    }

    public static void updateCollectionBowl(CollectionBowlBlockEntity be) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(be.getPos());
        buf.writeFloat(be.getServerCollectionProgress());

        for (ServerPlayerEntity player : PlayerLookup.tracking(be)) {
            ServerPlayNetworking.send(player, NetworkingHandler.COLLECTION_PROGRESS, buf);
        }
    }



    public static void setServer(MinecraftServer server) {
        NetworkingHandler.server = server;
    }

    public static MinecraftServer getServer() {
        return server;
    }
}
