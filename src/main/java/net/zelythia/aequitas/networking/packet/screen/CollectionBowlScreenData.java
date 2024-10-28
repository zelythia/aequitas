package net.zelythia.aequitas.networking.packet.screen;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;

public record CollectionBowlScreenData(int size) implements CustomPayload {

    public static final CustomPayload.Id<CollectionBowlScreenData> PACKET_ID = new CustomPayload.Id<>(Identifier.of(Aequitas.MOD_ID, "collection_bowl_progress"));
    public static final PacketCodec<PacketByteBuf, CollectionBowlScreenData> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public CollectionBowlScreenData decode(PacketByteBuf buf) {
            int size = buf.readInt();
            return new CollectionBowlScreenData(size);
        }

        @Override
        public void encode(PacketByteBuf buf, CollectionBowlScreenData value) {
            buf.writeInt(value.size);
        }
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
