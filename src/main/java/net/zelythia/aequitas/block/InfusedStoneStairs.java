package net.zelythia.aequitas.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.StateManager;

public class InfusedStoneStairs extends StairsBlock {
    public InfusedStoneStairs(BlockState baseBlockState, Settings settings) {
        super(baseBlockState, settings);
        setDefaultState(getDefaultState().with(AequitasBlocks.ACTIVE_BLOCK_PROPERTY, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(AequitasBlocks.ACTIVE_BLOCK_PROPERTY);
    }
}
