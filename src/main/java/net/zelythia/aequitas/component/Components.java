package net.zelythia.aequitas.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;

import java.util.List;

public class Components {
    public Components() {
    }

    public static final ComponentType<Long> STORED_ESSENCE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Aequitas.MOD_ID, "stored_essence"),
            ComponentType.<Long>builder().codec(Codec.LONG).packetCodec(PacketCodecs.VAR_LONG).build()
    );


    public static final ComponentType<List<Identifier>> UNLOCKED_ITEMS = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Aequitas.MOD_ID, "unlocked_items"),
            ComponentType.<List<Identifier>>builder().codec(Identifier.CODEC.listOf()).packetCodec(Identifier.PACKET_CODEC.collect(PacketCodecs.toList())).build()
    );

}
