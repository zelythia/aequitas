package net.zelythia.aequitas.networking.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.zelythia.aequitas.Aequitas;

public record CollectionBowlProgress(BlockPos pos, Float progress) implements CustomPayload {
    public static final CustomPayload.Id<CollectionBowlProgress> PACKET_ID = new CustomPayload.Id<>(Identifier.of(Aequitas.MOD_ID, "collection_bowl_progress"));
    public static final PacketCodec<PacketByteBuf, CollectionBowlProgress> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public CollectionBowlProgress decode(PacketByteBuf buf) {
            BlockPos pos = buf.readBlockPos();
            Float progress = buf.readFloat();
            return new CollectionBowlProgress(pos, progress);
        }

        @Override
        public void encode(PacketByteBuf buf, CollectionBowlProgress value) {
            buf.writeBlockPos(value.pos);
            buf.writeFloat(value.progress);
        }
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
