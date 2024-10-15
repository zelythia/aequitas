package net.zelythia.aequitas.compat;

import com.google.gson.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LootTableParser {

    public record ItemEntry(String id, int min, int max, int weight) {

    }

    public static void parseEntry(JsonObject entry, List<ItemEntry> list, StringBuilder name) {
        String type = entry.get("type").getAsString();

        switch (type) {
            case "minecraft:loot_table": {
                //Assuming sub loot tables don't have conditions

                String s = entry.get("name").getAsString();
                for (JsonElement subEntry : net.zelythia.aequitas.client.NetworkingHandler.LOOTTABLES.get(new Identifier(s)).getAsJsonArray("pools").get(0).getAsJsonObject().getAsJsonArray("entries")) {
                    if (name.isEmpty()) name.append(s);
                    parseEntry(subEntry.getAsJsonObject(), list, name);
                }

                break;
            }
            case "minecraft:tag": {
                String id = "#" + entry.get("name").getAsString();
                int weight = 1;
                if (entry.has("weight")) weight = entry.get("weight").getAsInt();

                //Assuming we only have minecraft:set_count with minecraft:uniform
                if (entry.has("functions")) {
                    JsonObject count = entry.getAsJsonArray("functions").get(0).getAsJsonObject().getAsJsonObject("count");

                    list.add(new ItemEntry(id, count.get("min").getAsInt(), count.get("max").getAsInt(), weight));
                } else {
                    list.add(new ItemEntry(id, 1, 1, weight));
                }

                break;
            }
            case "minecraft:item": {
                String id = entry.get("name").getAsString();
                int weight = 1;
                if (entry.has("weight")) weight = entry.get("weight").getAsInt();

                //Assuming we only have minecraft:set_count with minecraft:uniform
                if (entry.has("functions")) {
                    JsonObject count = entry.getAsJsonArray("functions").get(0).getAsJsonObject().getAsJsonObject("count");

                    list.add(new ItemEntry(id, count.get("min").getAsInt(), count.get("max").getAsInt(), weight));
                } else {
                    list.add(new ItemEntry(id, 1, 1, weight));
                }

                break;
            }
            case "minecraft:empty": {
                int weight = 1;
                if (entry.has("weight")) weight = entry.get("weight").getAsInt();

                list.add(new ItemEntry("minecraft:air", 1, 1, weight));

                break;
            }
        }
    }

    public static void parseCondition(JsonObject condition, List<Identifier> list) {
        String type = condition.get("condition").getAsString();

        switch (type) {
            case "minecraft:location_check": {
                for (Map.Entry<String, JsonElement> predicate : condition.getAsJsonObject("predicate").entrySet()) {
                    list.add(new Identifier(predicate.getValue().getAsString()));
                }
                break;
            }

            case "minecraft:any_of": {
                for (JsonElement terms : condition.getAsJsonArray("terms")) {
                    parseCondition(terms.getAsJsonObject(), list);
                }
                break;
            }
        }
    }


    public static void writeIdentifier(PacketByteBuf buf, Identifier identifier) {
        if (identifier.getNamespace().equals("minecraft")) {
            buf.writeString(identifier.getPath());
        } else {
            buf.writeString(identifier.toString());
        }
    }

    public static void writeJson(PacketByteBuf buf, JsonElement element) {
        if (element.isJsonNull()) {
            buf.writeByte(0);
        } else if (element.isJsonPrimitive()) {
            writeJsonPrimitive(buf, element.getAsJsonPrimitive());
        } else if (element.isJsonArray()) {
            buf.writeByte(12);
            JsonArray array = element.getAsJsonArray();
            buf.writeVarInt(array.size());
            for (JsonElement arrayElement : array) {
                writeJson(buf, arrayElement);
            }
        } else if (element.isJsonObject()) {
            buf.writeByte(13);
            JsonObject object = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = object.entrySet();
            buf.writeVarInt(entrySet.size());
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                buf.writeString(entry.getKey());
                writeJson(buf, entry.getValue());
            }
        } else {
            throw new IllegalArgumentException("Unknown json element type: " + element.getClass());
        }
    }


    private static void writeJsonPrimitive(PacketByteBuf buf, JsonPrimitive primitive) {
        if (primitive.isString()) {
            buf.writeByte(1);
            buf.writeString(primitive.getAsString());
        } else if (primitive.isBoolean()) {
            buf.writeByte(primitive.getAsBoolean() ? 3 : 2);
        } else if (primitive.isNumber()) {
            Number number = primitive.getAsNumber();
            if (number instanceof Integer) {
                buf.writeByte(4);
                buf.writeVarInt(number.intValue());
            } else if (number instanceof Long) {
                buf.writeByte(5);
                buf.writeVarLong(number.longValue());
            } else if (number instanceof Short) {
                buf.writeByte(6);
                buf.writeShort(number.shortValue());
            } else if (number instanceof Byte) {
                buf.writeByte(7);
                buf.writeByte(number.byteValue());
            } else if (number instanceof BigInteger) {
                buf.writeByte(8);
                byte[] bytes = ((BigInteger) number).toByteArray();
                buf.writeByteArray(bytes);
            } else if (number instanceof Float) {
                buf.writeByte(9);
                buf.writeFloat(number.floatValue());
            } else if (number instanceof Double) {
                buf.writeByte(10);
                buf.writeDouble(number.doubleValue());
            } else if (number instanceof BigDecimal decimal) {
                buf.writeByte(11);
                // serialize with unscaled value, scale, and precision
                buf.writeByteArray(decimal.unscaledValue().toByteArray());
                buf.writeInt(decimal.scale());
                buf.writeInt(decimal.precision());
            } else {
                throw new IllegalArgumentException("Unknown number type: " + number.getClass());
            }
        } else {
            throw new IllegalArgumentException("Unknown primitive type: " + primitive.getClass());
        }
    }


    public static JsonElement readJson(PacketByteBuf buf) {
        byte type = buf.readByte();
        int size = 0;
        switch (type) {
            case 0:
                return JsonNull.INSTANCE;
            case 12:
                size = buf.readVarInt();
                JsonArray array = new JsonArray(size);
                for (int i = 0; i < size; i++) {
                    array.add(readJson(buf));
                }
                return array;
            case 13:
                size = buf.readVarInt();
                JsonObject object = new JsonObject();
                for (int i = 0; i < size; i++) {
                    String key = buf.readString();
                    object.add(key, readJson(buf));
                }
                return object;
            default:
                if (type < 1 || type > 11) {
                    throw new IllegalArgumentException("Unknown json type: " + type);
                }
                return readJsonPrimitive(type, buf);
        }
    }

    private static JsonPrimitive readJsonPrimitive(int type, PacketByteBuf buf) {
        switch (type) {
            case 1:
                return new JsonPrimitive(buf.readString());
            case 2:
                return new JsonPrimitive(false);
            case 3:
                return new JsonPrimitive(true);
            case 4:
                return new JsonPrimitive(buf.readVarInt());
            case 5:
                return new JsonPrimitive(buf.readVarLong());
            case 6:
                return new JsonPrimitive(buf.readShort());
            case 7:
                return new JsonPrimitive(buf.readByte());
            case 8:
                return new JsonPrimitive(new BigInteger(buf.readByteArray()));
            case 9:
                return new JsonPrimitive(buf.readFloat());
            case 10:
                return new JsonPrimitive(buf.readDouble());
            case 11:
                // deserialize with unscaled value, scale, and precision
                byte[] unscaledValue = buf.readByteArray();
                int scale = buf.readInt();
                int precision = buf.readInt();
                MathContext context = new MathContext(precision);
                return new JsonPrimitive(new BigDecimal(new BigInteger(unscaledValue), scale, context));
            default:
                throw new IllegalArgumentException("Unknown json primitive type: " + type);
        }
    }
}
