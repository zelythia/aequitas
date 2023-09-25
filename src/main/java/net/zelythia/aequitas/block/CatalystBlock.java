package net.zelythia.aequitas.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.SlabType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.zelythia.aequitas.Aequitas;

public class CatalystBlock extends FacingBlock {

    public CatalystBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Aequitas.ACTIVE_BLOCK_PROPERTY, false).with(FACING, Direction.UP));
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)state.with(FACING, mirror.apply((Direction)state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Aequitas.ACTIVE_BLOCK_PROPERTY);
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)){
            case UP:
            default:
                return Block.createCuboidShape(4, 0, 4, 12, 12, 12);
            case DOWN:
                return Block.createCuboidShape(4, 4, 4, 12, 16, 12);
            case NORTH:
                return Block.createCuboidShape(4, 4, 4, 12, 12, 16);
            case SOUTH:
                return Block.createCuboidShape(4, 4, 0, 12, 12, 12);
            case EAST:
                return Block.createCuboidShape(0, 4, 4, 12, 12, 12);
            case WEST:
                return Block.createCuboidShape(4, 4, 4, 16, 12, 12);
        }
    }


    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction.getOpposite()));
        return blockState.isOf(this) && blockState.get(FACING) == direction ? (BlockState)this.getDefaultState().with(FACING, direction.getOpposite()) : (BlockState)this.getDefaultState().with(FACING, direction);
    }
}
