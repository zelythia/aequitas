package net.zelythia.aequitas.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.block.entity.CraftingPedestalBlockEntity;
import org.lwjgl.system.CallbackI;

public class NetworkingHandler {

    public static final Identifier ESSENCE_UPDATE = new Identifier(Aequitas.MOD_ID, "essence_event");
    public static final Identifier CRAFTING_PARTICLE = new Identifier(Aequitas.MOD_ID, "crafting_particle");


    public static void updateEssence(){
        PacketByteBuf buf = PacketByteBufs.create();
        EssencePacket.encode(buf);
        if(Aequitas.server != null){
            for(ServerPlayerEntity player: PlayerLookup.all(Aequitas.server)){
                ServerPlayNetworking.send(player, NetworkingHandler.ESSENCE_UPDATE, buf);
            }
        }
    }

    public static void sendParticle(CraftingPedestalBlockEntity be, BlockPos from, BlockPos to, ItemStack stack){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(from);
        buf.writeBlockPos(to);
        buf.writeItemStack(stack);

        for(ServerPlayerEntity player: PlayerLookup.tracking(be)){
            ServerPlayNetworking.send(player, NetworkingHandler.CRAFTING_PARTICLE, buf);
        }
    }
}
