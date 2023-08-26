package net.zelythia.aequitas.block;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class PedestalBlock extends Block {
    public PedestalBlock() {
        super(AbstractBlock.Settings.of(Material.STONE));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.125, 0.875));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.125, 0.25, 0.75, 0.875, 0.75));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.875, 0.0625, 0.9375, 0.9375, 0.9375));
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.9375, 0, 1, 1, 1));

        return shape;
    }
}
