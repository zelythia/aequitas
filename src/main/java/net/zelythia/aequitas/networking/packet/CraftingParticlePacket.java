package net.zelythia.aequitas.networking.packet;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.zelythia.aequitas.Aequitas;

public record CraftingParticlePacket(BlockPos from, BlockPos to, ItemStack stack) implements CustomPayload {
    public static final CustomPayload.Id<CraftingParticlePacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(Aequitas.MOD_ID, "crafting_particle"));
    public static final PacketCodec<RegistryByteBuf, CraftingParticlePacket> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public CraftingParticlePacket decode(RegistryByteBuf buf) {
            BlockPos from = buf.readBlockPos();
            BlockPos to = buf.readBlockPos();
            ItemStack stack = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
            return new CraftingParticlePacket(from, to, stack);
        }

        @Override
        public void encode(RegistryByteBuf buf, CraftingParticlePacket value) {
            buf.writeBlockPos(value.from);
            buf.writeBlockPos(value.to);
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, value.stack);
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
