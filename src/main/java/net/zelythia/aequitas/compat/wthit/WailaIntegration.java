package net.zelythia.aequitas.compat.wthit;

import mcp.mobius.waila.api.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.block.entity.CraftingPedestalBlockEntity;

public class WailaIntegration implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(new Identifier(Aequitas.MOD_ID, "sampling_pedestal"), true);
        registrar.addConfig(new Identifier(Aequitas.MOD_ID, "crafting_pedestal"), true);

        registrar.addBlockData(new CraftingPedestalBlockDataProvider(), CraftingPedestalBlockEntity.class);
        registrar.addComponent(new CraftingPedestalBlockComponentProvider(), TooltipPosition.BODY, CraftingPedestalBlockEntity.class);
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
