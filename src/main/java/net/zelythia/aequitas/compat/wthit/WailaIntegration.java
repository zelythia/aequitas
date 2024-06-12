package net.zelythia.aequitas.compat.wthit;

import mcp.mobius.waila.api.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.block.entity.CraftingPedestalBlockEntity;
import net.zelythia.aequitas.block.entity.SamplingPedestalBlockEntity;

public class WailaIntegration implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(new Identifier(Aequitas.MOD_ID, "sampling_pedestal"), true);
        registrar.addConfig(new Identifier(Aequitas.MOD_ID, "crafting_pedestal"), true);


        registrar.addComponent(new SamplingPedestalBlockComponentProvider(), TooltipPosition.BODY, SamplingPedestalBlockEntity.class);

        registrar.addBlockData(new CraftingPedestalBlockDataProvider(), CraftingPedestalBlockEntity.class);
        registrar.addComponent(new CraftingPedestalBlockComponentProvider(), TooltipPosition.BODY, CraftingPedestalBlockEntity.class);
    }


    private static class SamplingPedestalBlockComponentProvider implements IBlockComponentProvider {

        @Override
        public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
//            if (config.getBoolean(new Identifier(Aequitas.MOD_ID, "sampling_pedestal"))) {
//                SamplingPedestalBlockEntity be = accessor.getBlockEntity();
//
//                if (!be.getStack(0).isEmpty()) {
////                    NbtCompound tag = new NbtCompound();
////                    tag.putString("id", Registries.ITEM.getId(be.getStack(0).getItem()).toString());
////                    tag.putInt("Count", (be.getStack(0).getCount()));
////
////                    NbtCompound tag2 = new NbtCompound();
////                    tag2.putString("text", be.getStack(0).getItem().toString());
//
//                    tooltip.addLine(new ItemComponent(be.getStack(0)));
//                }
//            }
        }
    }


    private static class CraftingPedestalBlockDataProvider implements IDataProvider<CraftingPedestalBlockEntity> {
        @Override
        public void appendData(IDataWriter data, IServerAccessor<CraftingPedestalBlockEntity> accessor, IPluginConfig config) {
            CraftingPedestalBlockEntity be = accessor.getTarget();
            data.raw().putLong("storedEssence", be.getStoredEssence());
            data.raw().putLong("targetEssence", EssenceHandler.getEssenceValue(be.getTargetItem()));
        }
    }


    private static class CraftingPedestalBlockComponentProvider implements IBlockComponentProvider {
        @Override
        public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
            if (config.getBoolean(new Identifier(Aequitas.MOD_ID, "crafting_pedestal"))) {
                NbtCompound data = accessor.getData().raw();

                if (data.contains("storedEssence")) {
                    long storedEssence = data.getLong("storedEssence");

                    if (data.contains("targetEssence")) {
                        long targetEssence = data.getLong("targetEssence");

                        if (targetEssence > 0) {
                            tooltip.addLine(Text.literal("Essence: " + storedEssence + "/" + targetEssence));
                            return;
                        }

                        tooltip.addLine(Text.literal("Essence: " + storedEssence));
                    }
                }
            }
        }
    }
}
