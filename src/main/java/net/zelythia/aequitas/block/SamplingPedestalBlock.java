package net.zelythia.aequitas.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.zelythia.aequitas.block.entity.BlockEntityTypes;
import net.zelythia.aequitas.block.entity.SamplingPedestalBlockEntity;
import net.zelythia.aequitas.component.Components;
import net.zelythia.aequitas.item.AequitasItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SamplingPedestalBlock extends BlockWithEntity {
    public static final MapCodec<SamplingPedestalBlock> CODEC = createCodec(SamplingPedestalBlock::new);

    public MapCodec<SamplingPedestalBlock> getCodec() {
        return CODEC;
    }

    public SamplingPedestalBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SamplingPedestalBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, BlockEntityTypes.SAMPLING_PEDESTAL_BLOCK_ENTITY, SamplingPedestalBlockEntity::tick);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() == AequitasItems.PORTABLE_PEDESTAL && stack.getComponents().contains(Components.UNLOCKED_ITEMS)) {
            if ((stack.getOrDefault(Components.UNLOCKED_ITEMS, List.of()).size() > 0)) return ActionResult.FAIL;
        }

        if (world.isClient) return ActionResult.SUCCESS;

        Inventory blockEntity = (Inventory) world.getBlockEntity(pos);
        if (!stack.isEmpty() && world.getBlockState(pos.add(0, 1, 0)).isAir()) {
            if (blockEntity.getStack(0).isEmpty()) {
                blockEntity.setStack(0, stack.copy());
                stack.setCount(0);
            } else if (blockEntity.getStack(0).getItem().equals(stack.getItem())) {
                int i = blockEntity.getStack(0).getCount() + stack.getCount();

                if (i <= blockEntity.getMaxCountPerStack()) {
                    blockEntity.getStack(0).setCount(i);
                    stack.setCount(0);
                } else {
                    blockEntity.getStack(0).setCount(blockEntity.getMaxCountPerStack());
                    stack.setCount(i - blockEntity.getMaxCountPerStack());
                }
            }
        }
        if (player.isSneaking()) {
            //Take items out of the inventory
            player.getInventory().offerOrDrop(blockEntity.getStack(0));
            blockEntity.removeStack(0);

        }

        blockEntity.markDirty();
        world.updateListeners(pos, state, state, 2);

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SamplingPedestalBlockEntity) {
                ItemScatterer.spawn(world, pos, (SamplingPedestalBlockEntity) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
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
