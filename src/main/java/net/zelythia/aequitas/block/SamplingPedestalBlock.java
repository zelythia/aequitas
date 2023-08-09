package net.zelythia.aequitas.block;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.zelythia.aequitas.block.entity.CraftingPedestalBlockEntity;
import net.zelythia.aequitas.block.entity.SamplingPedestalBlockEntity;
import org.jetbrains.annotations.Nullable;

public class SamplingPedestalBlock extends BlockWithEntity {
    public SamplingPedestalBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new SamplingPedestalBlockEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.isClient) return ActionResult.SUCCESS;

        Inventory blockEntity = (Inventory) world.getBlockEntity(pos);
        if(!player.getStackInHand(hand).isEmpty()){
           if(blockEntity.getStack(0).isEmpty()){
               blockEntity.setStack(0, player.getStackInHand(hand).copy());
               player.getStackInHand(hand).setCount(0);
           }
           else if(blockEntity.getStack(0).getItem().equals(player.getStackInHand(hand).getItem())){
               int i = blockEntity.getStack(0).getCount() + player.getStackInHand(hand).getCount();

               if(i <= blockEntity.getMaxCountPerStack()){
                   blockEntity.getStack(0).setCount(i);
                   player.getStackInHand(hand).setCount(0);
               }
               else{
                   blockEntity.getStack(0).setCount(blockEntity.getMaxCountPerStack());
                   player.getStackInHand(hand).setCount(i - blockEntity.getMaxCountPerStack());
               }
           }
        }
        if(player.isSneaking()){
            //Take items out of the inventory
            player.inventory.offerOrDrop(world, blockEntity.getStack(0));
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
            if (blockEntity instanceof CraftingPedestalBlockEntity) {
                ItemScatterer.spawn(world, pos, (CraftingPedestalBlockEntity)blockEntity);
                // update comparators
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
