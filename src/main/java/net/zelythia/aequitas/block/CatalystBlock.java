package net.zelythia.aequitas.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.zelythia.aequitas.Aequitas;

public class CatalystBlock extends Block {

    public CatalystBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Aequitas.ACTIVE_BLOCK_PROPERTY, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Aequitas.ACTIVE_BLOCK_PROPERTY);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 0.75, 0.75));

        return shape;
    }
}
