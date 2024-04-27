package net.zelythia.aequitas.networking;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.block.entity.CollectionBowlBlockEntity;
import net.zelythia.aequitas.block.entity.CraftingPedestalBlockEntity;
import net.zelythia.aequitas.compat.LootTableParser;
import net.zelythia.aequitas.item.FallFlying;
import net.zelythia.aequitas.screen.PortablePedestalScreenHandler;

import java.util.ArrayList;
import java.util.List;

public class NetworkingHandler {

    private static MinecraftServer server;

    public static final Identifier ESSENCE_UPDATE = new Identifier(Aequitas.MOD_ID, "essence_event");
    public static final Identifier CRAFTING_PARTICLE = new Identifier(Aequitas.MOD_ID, "crafting_particle");
    public static final Identifier COLLECTION_PROGRESS = new Identifier(Aequitas.MOD_ID, "collection_progress");

    public static final Identifier START_FLYING = new Identifier(Aequitas.MOD_ID, "start_flying");


    public static final Identifier C2S_UPDATE_FILTER = new Identifier(Aequitas.MOD_ID, "update_filter");



    //Rei + Emi compat
    public static final Identifier ASK_SYNC_INFO = new Identifier("aequitas_rei", "asi");
    public static final Identifier SEND_LOOT_INFO = new Identifier("aequitas_rei", "sli");
    public static final Gson GSON = LootGsons.getTableGsonBuilder().create();


    public static void onInitialize() {
        List<Identifier> syncedLootTables = new ArrayList<>();
        syncedLootTables.add(new Identifier("aequitas", "gameplay/biomes"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/nether"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/end"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/overworld"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/badlands"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/cherry"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/dark_forest"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/deep_dark"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/desert"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/dripstone"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/emerald"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/forest"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/gravel"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/ice"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/jungle"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/lush_caves"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/mangrove"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/mushroom"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/ocean"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/river"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/savanna"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/snow"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/stone"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/swamp"));
        syncedLootTables.add(new Identifier("aequitas", "gameplay/taiga"));


        ServerPlayNetworking.registerGlobalReceiver(START_FLYING, (server1, player, handler, buf, responseSender) -> {
            server1.execute(() -> {
                if (!FallFlying.startFallFlying(player)) {
                    player.stopFallFlying();
                }
            });
        });


        ServerPlayNetworking.registerGlobalReceiver(NetworkingHandler.C2S_UPDATE_FILTER, (server, player, handler, buf, responseSender) -> {
            int syncId = buf.readInt();
            String filter = buf.readString();
            int page = buf.readInt();

            server.execute(() -> {
                if (player.currentScreenHandler.syncId == syncId && player.currentScreenHandler.canUse(player)) {
                    if (player.currentScreenHandler instanceof PortablePedestalScreenHandler) {
                        ((PortablePedestalScreenHandler) player.currentScreenHandler).updateSearchProperties(filter, page);
                    }
                }
            });
        });


        //Rei + Emi compat
        ServerPlayNetworking.registerGlobalReceiver(ASK_SYNC_INFO, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                LootManager lootManager = server.getLootManager();

                int size = 50;
                for (int i = 0; i < syncedLootTables.size(); i += size) {
                    int end = Math.min(syncedLootTables.size(), i + size);
                    PacketByteBuf res = new PacketByteBuf(Unpooled.buffer());
                    res.writeInt(end - i);
                    for (int j = i; j < end; j++) {
                        Identifier identifier = syncedLootTables.get(j);
                        LootTable table = lootManager.getLootTable(identifier);
                        LootTableParser.writeIdentifier(res, identifier);
                        LootTableParser.writeJson(res, GSON.toJsonTree(table));
                    }

                    responseSender.sendPacket(SEND_LOOT_INFO, new PacketByteBuf(res.duplicate()));
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
