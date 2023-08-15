package net.zelythia.aequitas.compat.wthit;

import mcp.mobius.waila.api.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.block.entity.CraftingPedestalBlockEntity;
import net.zelythia.aequitas.block.entity.SamplingPedestalBlockEntity;

import java.util.List;

public class WailaIntegration implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(new Identifier(Aequitas.MOD_ID, "sampling_pedestal"), true);
        registrar.addConfig(new Identifier(Aequitas.MOD_ID, "crafting_pedestal"), true);


        registrar.addComponent(new SamplingPedestalBlockComponentProvider(), TooltipPosition.BODY, SamplingPedestalBlockEntity.class);

        registrar.addBlockData(new CraftingPedestalBlockDataProvider(), CraftingPedestalBlockEntity.class);
        registrar.addComponent(new CraftingPedestalBlockComponentProvider(), TooltipPosition.BODY, CraftingPedestalBlockEntity.class);
    }


    private static class SamplingPedestalBlockComponentProvider implements IBlockComponentProvider{
        @Override
        public void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
            if(config.get(new Identifier(Aequitas.MOD_ID, "sampling_pedestal"))){
                SamplingPedestalBlockEntity be = (SamplingPedestalBlockEntity) accessor.getBlockEntity();

                if(!be.getStack(0).isEmpty()){
                    CompoundTag tag = new CompoundTag();
                    tag.putString("id", Registry.ITEM.getId(be.getStack(0).getItem()).toString());
                    tag.putInt("Count", (be.getStack(0).getCount()));

                    CompoundTag tag2 = new CompoundTag();
                    tag2.putString("text", be.getStack(0).getItem().toString());

                    tooltip.add(IDrawableText.create().with(new Identifier("item"), tag));
                }
            }
        }
    }


    private static class CraftingPedestalBlockDataProvider implements IServerDataProvider<BlockEntity>{
        @Override
        public void appendServerData(CompoundTag data, ServerPlayerEntity player, World world, BlockEntity blockEntity) {
            CraftingPedestalBlockEntity be = (CraftingPedestalBlockEntity) blockEntity;
            data.putLong("storedEssence", be.getStoredEssence());
            data.putLong("targetEssence", EssenceHandler.getEssenceValue(be.getTargetItem()));
        }
    }


    private static class CraftingPedestalBlockComponentProvider implements IBlockComponentProvider{
        @Override
        public void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config) {
            if(config.get(new Identifier(Aequitas.MOD_ID, "crafting_pedestal"))){
                CompoundTag data = accessor.getServerData();

                if(data.contains("storedEssence")){
                    long storedEssence = data.getLong("storedEssence");

                    if(data.contains("targetEssence")){
                        long targetEssence = data.getLong("targetEssence");

                        if(targetEssence > 0){
                            tooltip.add(new LiteralText("Essence: "+storedEssence +"/"+ targetEssence));
                            return;
                        }

                        tooltip.add(new LiteralText("Essence: "+ storedEssence));
                    }
                }
            }
        }
    }
}
