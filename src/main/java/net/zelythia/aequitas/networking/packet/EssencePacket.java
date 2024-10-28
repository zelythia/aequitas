package net.zelythia.aequitas.networking.packet;

import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;

import java.util.HashMap;
import java.util.Map;

public record EssencePacket(Map<Item, Long> map) implements CustomPayload {
    public static final CustomPayload.Id<EssencePacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(Aequitas.MOD_ID, "update_portable_pedestal_filter"));
    public static final PacketCodec<PacketByteBuf, EssencePacket> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public EssencePacket decode(PacketByteBuf buf) {
            Map<Item, Long> map = new HashMap<>();

            int size = buf.readVarInt();
            for (int i = 0; i < size; i++) {
                Item item = Item.byRawId(buf.readVarInt());
                map.put(item, buf.readVarLong());
            }

            return new EssencePacket(map);
        }

        @Override
        public void encode(PacketByteBuf buf, EssencePacket essencePacket) {
            buf.writeVarInt(essencePacket.map.size());

            essencePacket.map.forEach((item, value) -> {
                buf.writeVarInt(Item.getRawId(item));
                buf.writeVarLong(value);
            });
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
