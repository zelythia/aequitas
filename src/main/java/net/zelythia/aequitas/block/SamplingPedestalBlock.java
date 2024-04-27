package net.zelythia.aequitas.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.block.entity.SamplingPedestalBlockEntity;
import org.jetbrains.annotations.Nullable;

public class SamplingPedestalBlock extends BlockWithEntity {

//    public static final MapCodec<SamplingPedestalBlock> CODEC = createCodec(SamplingPedestalBlock::new);
//
//    public MapCodec<SamplingPedestalBlock> getCodec() {
//        return CODEC;
//    }

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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (player.getStackInHand(hand).getItem() == Aequitas.PORTABLE_PEDESTAL_ITEM && player.getStackInHand(hand).hasNbt() && player.getStackInHand(hand).getNbt().getType("unlocked") == NbtElement.LIST_TYPE) {
            if (((NbtList) player.getStackInHand(hand).getNbt().get("unlocked")).size() > 0) return ActionResult.FAIL;
        }

        if (world.isClient) return ActionResult.SUCCESS;



        Inventory blockEntity = (Inventory) world.getBlockEntity(pos);
        if (!player.getStackInHand(hand).isEmpty() && world.getBlockState(pos.add(0,1,0)).isAir()) {
            if (blockEntity.getStack(0).isEmpty()) {
                blockEntity.setStack(0, player.getStackInHand(hand).copy());
                player.getStackInHand(hand).setCount(0);
            } else if (blockEntity.getStack(0).getItem().equals(player.getStackInHand(hand).getItem())) {
                int i = blockEntity.getStack(0).getCount() + player.getStackInHand(hand).getCount();

                if (i <= blockEntity.getMaxCountPerStack()) {
                    blockEntity.getStack(0).setCount(i);
                    player.getStackInHand(hand).setCount(0);
                } else {
                    blockEntity.getStack(0).setCount(blockEntity.getMaxCountPerStack());
                    player.getStackInHand(hand).setCount(i - blockEntity.getMaxCountPerStack());
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
