package net.zelythia.aequitas.networking.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;

public record UpdatePortablePedestalFilter(int syncId, String filter, int page) implements CustomPayload {
    public static final CustomPayload.Id<UpdatePortablePedestalFilter> PACKET_ID = new CustomPayload.Id<>(Identifier.of(Aequitas.MOD_ID, "update_portable_pedestal_filter"));
    public static final PacketCodec<PacketByteBuf, UpdatePortablePedestalFilter> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public UpdatePortablePedestalFilter decode(PacketByteBuf buf) {
            int syncId = buf.readInt();
            String filter = buf.readString();
            int page = buf.readInt();
            return new UpdatePortablePedestalFilter(syncId, filter, page);
        }

        @Override
        public void encode(PacketByteBuf buf, UpdatePortablePedestalFilter value) {
            buf.writeInt(value.syncId);
            buf.writeString(value.filter);
            buf.writeInt(value.page);
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}

