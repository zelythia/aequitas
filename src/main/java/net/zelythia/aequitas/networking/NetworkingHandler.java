package net.zelythia.aequitas.networking;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.zelythia.aequitas.block.entity.CollectionBowlBlockEntity;
import net.zelythia.aequitas.block.entity.CraftingPedestalBlockEntity;
import net.zelythia.aequitas.essence.EssenceHandler;
import net.zelythia.aequitas.item.FallFlying;
import net.zelythia.aequitas.networking.packet.*;
import net.zelythia.aequitas.networking.packet.compat.LootInfoReq;
import net.zelythia.aequitas.networking.packet.compat.LootInfoRes;
import net.zelythia.aequitas.screen.PortablePedestalScreenHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkingHandler {
    private static MinecraftServer server;

    public static void onInitialize() {
        PayloadTypeRegistry.playC2S().register(LootInfoReq.PACKET_ID, LootInfoReq.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(StartFallFlying.PACKET_ID, StartFallFlying.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(UpdatePortablePedestalFilter.PACKET_ID, UpdatePortablePedestalFilter.PACKET_CODEC);

        PayloadTypeRegistry.playS2C().register(LootInfoRes.PACKET_ID, LootInfoRes.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(CollectionBowlProgress.PACKET_ID, CollectionBowlProgress.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(CraftingParticlePacket.PACKET_ID, CraftingParticlePacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(EssencePacket.PACKET_ID, EssencePacket.PACKET_CODEC);


        List<Identifier> syncedLootTables = new ArrayList<>();
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/biomes"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/nether"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/end"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/overworld"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/badlands"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/cherry"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/dark_forest"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/deep_dark"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/desert"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/dripstone"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/emerald"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/forest"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/gravel"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/ice"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/jungle"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/lush_caves"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/mangrove"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/mushroom"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/ocean"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/river"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/savanna"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/snow"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/stone"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/swamp"));
        syncedLootTables.add(Identifier.of("aequitas", "gameplay/taiga"));


        ServerPlayNetworking.registerGlobalReceiver(StartFallFlying.PACKET_ID, (payload, context) -> {
            context.server().execute(() -> {
                if (!FallFlying.startFallFlying(context.player())) {
                    context.player().stopFallFlying();
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(UpdatePortablePedestalFilter.PACKET_ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();

                if (player.currentScreenHandler.syncId == payload.syncId() && player.currentScreenHandler.canUse(player)) {
                    if (player.currentScreenHandler instanceof PortablePedestalScreenHandler) {
                        ((PortablePedestalScreenHandler) player.currentScreenHandler).updateSearchProperties(payload.filter(), payload.page());
                    }
                }
            });
        });


        //Rei + Emi compat
        ServerPlayNetworking.registerGlobalReceiver(LootInfoReq.PACKET_ID, (payload, context) -> {
            context.server().execute(() -> {
                Registry<LootTable> lootManager = context.server().getReloadableRegistries().getRegistryManager().get(RegistryKeys.LOOT_TABLE);
                Map<Identifier, JsonObject> loottables = new HashMap<>();


                for (Identifier identifier : syncedLootTables) {
                    LootTable table = lootManager.get(identifier);
                    DataResult<JsonElement> json = LootTable.CODEC.encodeStart(RegistryOps.of(JsonOps.INSTANCE, context.server().getRegistryManager()), table);

                    loottables.put(identifier, json.result().get().getAsJsonObject());
                }

                context.responseSender().sendPacket(new LootInfoRes(loottables));
            });
        });
    }


    public static void updateEssence() {
        if (server == null) return;

        for (ServerPlayerEntity player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, new EssencePacket(EssenceHandler.map));
        }
    }

    public static void sendParticle(CraftingPedestalBlockEntity be, BlockPos from, BlockPos to, ItemStack stack) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(be)) {
            ServerPlayNetworking.send(player, new CraftingParticlePacket(from, to, stack));
        }
    }

    public static void updateCollectionBowl(CollectionBowlBlockEntity be) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(be)) {
            ServerPlayNetworking.send(player, new CollectionBowlProgress(be.getPos(), be.getServerCollectionProgress()));
        }
    }


    public static void setServer(MinecraftServer server) {
        NetworkingHandler.server = server;
    }

    public static MinecraftServer getServer() {
        return server;
    }
}
