package net.zelythia.aequitas.block;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.Util;
import net.zelythia.aequitas.client.particle.CatalystParticle;
import net.zelythia.aequitas.client.particle.Particles;

import java.util.Random;

public class CatalystBlock extends FacingBlock {

    private final int tier;

    public CatalystBlock(Settings settings) {
        this(settings, 1);
    }

    public CatalystBlock(Settings settings, int tier) {
        super(settings);
        this.tier = tier;
        setDefaultState(getDefaultState().with(Aequitas.ACTIVE_BLOCK_PROPERTY, false).with(FACING, Direction.UP));
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState) state.with(FACING, rotation.rotate((Direction) state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState) state.with(FACING, mirror.apply((Direction) state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Aequitas.ACTIVE_BLOCK_PROPERTY);
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
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
        return blockState.isOf(this) && blockState.get(FACING) == direction ? (BlockState) this.getDefaultState().with(FACING, direction.getOpposite()) : (BlockState) this.getDefaultState().with(FACING, direction);
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        for (int i = 0; i < 3; i++) {
            if (state.get(Aequitas.ACTIVE_BLOCK_PROPERTY)) {
                Direction direction = state.get(FACING);

                double end_x = 0.5 + pos.getX();
                double end_y = (direction == Direction.UP ? 0.375 : 0.625) + pos.getY();
                double end_z = 0.5 + pos.getZ();

                double x = end_x + random.nextDouble() * (tier + 1) * 2 - (tier + 1);
                double y = end_y + random.nextDouble() * 6 - 3;
                double z = end_z + random.nextDouble() * (tier + 1) * 2 - (tier + 1);

                double velX = end_x - x;
                double velY = end_y - y;
                double velZ = end_z - z;

                double len = Math.sqrt(velX * velX + velZ * velZ + velY * velY);
                velX = velX / len;
                velY = velY / len;
                velZ = velZ / len;

                double r = 0.1 * (random.nextDouble() * 0.3 + 0.7);
                velX *= r;
                velY *= r;
                velZ *= r;


                CatalystParticle particle = (CatalystParticle) Particles.spawnParticle(MinecraftClient.getInstance(), Particles.CATALYST_PARTICLE, false, true, x, y, z, velX, velY, velZ);

                if (particle != null) {
                    particle.setMaxDistanceSq(Util.distanceSq(x, y, z, end_x, end_y, end_z));
                    particle.setColor(Particles.TIER_COLORS[tier][0], Particles.TIER_COLORS[tier][1], Particles.TIER_COLORS[tier][2]);
                }
            }
        }

    }
}
