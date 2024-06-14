package net.zelythia.aequitas.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class InfusedStonePillar extends PillarBlock {
    public InfusedStonePillar(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(AequitasBlocks.ACTIVE_BLOCK_PROPERTY, false).with(AequitasBlocks.TOP_BLOCK_PROPERTY, true).with(AequitasBlocks.BOTTOM_BLOCK_PROPERTY, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(AequitasBlocks.ACTIVE_BLOCK_PROPERTY);
        builder.add(AequitasBlocks.TOP_BLOCK_PROPERTY);
        builder.add(AequitasBlocks.BOTTOM_BLOCK_PROPERTY);
    }


    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState placementState = super.getPlacementState(ctx);

        BlockView blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction.Axis axis = ctx.getSide().getAxis();

        switch (axis) {
            case X: {
                BlockState state = blockView.getBlockState(blockPos.east());
                if (state.getBlock() instanceof InfusedStonePillar && state.get(PillarBlock.AXIS) == Direction.Axis.X)
                    placementState = placementState.with(AequitasBlocks.TOP_BLOCK_PROPERTY, false);
                state = blockView.getBlockState(blockPos.west());
                if (state.getBlock() instanceof InfusedStonePillar && state.get(PillarBlock.AXIS) == Direction.Axis.X)
                    placementState = placementState.with(AequitasBlocks.BOTTOM_BLOCK_PROPERTY, false);
                break;
            }
            case Y: {
                BlockState state = blockView.getBlockState(blockPos.up());
                if (state.getBlock() instanceof InfusedStonePillar && state.get(PillarBlock.AXIS) == Direction.Axis.Y)
                    placementState = placementState.with(AequitasBlocks.TOP_BLOCK_PROPERTY, false);
                state = blockView.getBlockState(blockPos.down());
                if (blockView.getBlockState(blockPos.down()).getBlock() instanceof InfusedStonePillar && blockView.getBlockState(blockPos.down()).get(PillarBlock.AXIS) == Direction.Axis.Y)
                    placementState = placementState.with(AequitasBlocks.BOTTOM_BLOCK_PROPERTY, false);
                break;
            }
            case Z: {
                BlockState state = blockView.getBlockState(blockPos.north());
                if (state.getBlock() instanceof InfusedStonePillar && state.get(PillarBlock.AXIS) == Direction.Axis.Z)
                    placementState = placementState.with(AequitasBlocks.TOP_BLOCK_PROPERTY, false);
                state = blockView.getBlockState(blockPos.south());
                if (state.getBlock() instanceof InfusedStonePillar && state.get(PillarBlock.AXIS) == Direction.Axis.Z)
                    placementState = placementState.with(AequitasBlocks.BOTTOM_BLOCK_PROPERTY, false);
                break;
            }
        }

        return placementState;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {

        boolean b = !(neighborState.getBlock() instanceof InfusedStonePillar) || neighborState.get(PillarBlock.AXIS) != state.get(PillarBlock.AXIS);

        switch (state.get(PillarBlock.AXIS)) {
            case X -> {
                if (direction == Direction.EAST) state = state.with(AequitasBlocks.TOP_BLOCK_PROPERTY, b);
                if (direction == Direction.WEST) state = state.with(AequitasBlocks.BOTTOM_BLOCK_PROPERTY, b);
            }
            case Y -> {
                if (direction == Direction.UP) state = state.with(AequitasBlocks.TOP_BLOCK_PROPERTY, b);
                if (direction == Direction.DOWN) state = state.with(AequitasBlocks.BOTTOM_BLOCK_PROPERTY, b);
            }
            case Z -> {
                if (direction == Direction.NORTH) state = state.with(AequitasBlocks.TOP_BLOCK_PROPERTY, b);
                if (direction == Direction.SOUTH) state = state.with(AequitasBlocks.BOTTOM_BLOCK_PROPERTY, b);
            }
        }

        return state;
    }
}
