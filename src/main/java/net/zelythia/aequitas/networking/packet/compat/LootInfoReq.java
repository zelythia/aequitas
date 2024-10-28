package net.zelythia.aequitas.networking.packet.compat;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;

public record LootInfoReq() implements CustomPayload {
    public static final CustomPayload.Id<LootInfoReq> PACKET_ID = new CustomPayload.Id<>(Identifier.of(Aequitas.MOD_ID, "req_loot_info"));
    public static final PacketCodec<PacketByteBuf, LootInfoReq> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public LootInfoReq decode(PacketByteBuf buf) {
            return new LootInfoReq();
        }

        @Override
        public void encode(PacketByteBuf buf, LootInfoReq essencePacket) {

        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
