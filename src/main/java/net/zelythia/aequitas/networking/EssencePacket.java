package net.zelythia.aequitas.networking;

import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.zelythia.aequitas.EssenceHandler;

import java.util.HashMap;
import java.util.Map;

public class EssencePacket {


    public static void encode(PacketByteBuf buf){
        buf.writeVarInt(EssenceHandler.map.size());

        EssenceHandler.map.forEach((item, value) -> {
            buf.writeVarInt(Item.getRawId(item));
            buf.writeVarLong(value);
        });
    }

    public static Map<Item, Long> decode(PacketByteBuf buf){
         Map<Item, Long> map = new HashMap<>();

         int size = buf.readVarInt();
         for(int i = 0; i < size; i++){
            Item item = Item.byRawId(buf.readVarInt());
            map.put(item, buf.readVarLong());
         }

         return map;
    }
}
