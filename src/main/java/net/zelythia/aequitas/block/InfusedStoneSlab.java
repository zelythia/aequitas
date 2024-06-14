package net.zelythia.aequitas.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.state.StateManager;

public class InfusedStoneSlab extends SlabBlock {
    public InfusedStoneSlab(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(AequitasBlocks.ACTIVE_BLOCK_PROPERTY, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(AequitasBlocks.ACTIVE_BLOCK_PROPERTY);
    }
}
