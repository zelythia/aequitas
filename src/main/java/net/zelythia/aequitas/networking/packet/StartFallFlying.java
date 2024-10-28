package net.zelythia.aequitas.networking.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;

public record StartFallFlying() implements CustomPayload {
    public static final CustomPayload.Id<StartFallFlying> PACKET_ID = new CustomPayload.Id<>(Identifier.of(Aequitas.MOD_ID, "start_fall_flying"));
    public static final PacketCodec<PacketByteBuf, StartFallFlying> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public StartFallFlying decode(PacketByteBuf buf) {
            return new StartFallFlying();
        }

        @Override
        public void encode(PacketByteBuf buf, StartFallFlying essencePacket) {

        }
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
