package net.zelythia.aequitas.networking.packet.compat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.compat.LootTableParser;

import java.util.HashMap;
import java.util.Map;

public record LootInfoRes(Map<Identifier, JsonObject> loottables) implements CustomPayload {
    public static final Id<LootInfoRes> PACKET_ID = new Id<>(Identifier.of(Aequitas.MOD_ID, "req_loot_info"));
    public static final PacketCodec<PacketByteBuf, LootInfoRes> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public LootInfoRes decode(PacketByteBuf buf) {
            Map<Identifier, JsonObject> loottables = new HashMap<>();

            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                Identifier identifier = buf.readIdentifier();
                JsonElement json = LootTableParser.readJson(buf);
                loottables.put(identifier, json.getAsJsonObject());
            }

            return new LootInfoRes(loottables);
        }

        @Override
        public void encode(PacketByteBuf buf, LootInfoRes value) {
            buf.writeInt(value.loottables.size());

            for (Map.Entry<Identifier, JsonObject> entry : value.loottables.entrySet()) {
                buf.writeIdentifier(entry.getKey());
//                LootTableParser.writeIdentifier(buf, entry.getKey());
                LootTableParser.writeJson(buf, entry.getValue());
            }
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
