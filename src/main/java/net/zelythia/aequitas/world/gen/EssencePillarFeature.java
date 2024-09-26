package net.zelythia.aequitas.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.zelythia.aequitas.Util;
import net.zelythia.aequitas.block.AequitasBlocks;

import java.util.Iterator;

public class EssencePillarFeature extends Feature<EssencePillarFeatureConfig> {
    public EssencePillarFeature(Codec<EssencePillarFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<EssencePillarFeatureConfig> context) {

        EssencePillarFeatureConfig config = context.getConfig();
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();

        int maxHeight = config.maxHeight;
        BlockState fillerBlock = config.state;

        //Don't allow Pillars to be close to each other
        for (BlockPos blockPos1 : BlockPos.iterate(blockPos.add(-100, -50, -100), blockPos.add(100, 50, 100))) {
            if (world.getBlockState(blockPos1).isOf(AequitasBlocks.PETRIFIED_ESSENCE)) return false;
        }

        //Finding the floor
        for (; blockPos.getY() > 7; blockPos = blockPos.down()) {
            if (!world.isAir(blockPos.down())) {
                BlockState blockState = world.getBlockState(blockPos.down());
                if (isSoil(blockState) || isStone(blockState)) {
                    break;
                }
            }
        }

        if (blockPos.getY() <= 7 ) return false;

        blockPos = blockPos.down().down().down().down();


        //generating core
        int r = random.nextInt(100);
        BlockState coreBlock = AequitasBlocks.PRIMAL_ESSENCE_BLOCK.getDefaultState();
        if (r < 45) coreBlock = AequitasBlocks.PRIMORDIAL_ESSENCE_BLOCK.getDefaultState();
        if (r < 15) coreBlock = AequitasBlocks.PRISTINE_ESSENCE_BLOCK.getDefaultState();

        world.setBlockState(blockPos, coreBlock, 4);
        world.setBlockState(blockPos.up(), getCoreSurrounding(fillerBlock, random), 4);
        world.setBlockState(blockPos.down(), getCoreSurrounding(fillerBlock, random), 4);
        world.setBlockState(blockPos.north(), getCoreSurrounding(fillerBlock, random), 4);
        world.setBlockState(blockPos.south(), getCoreSurrounding(fillerBlock, random), 4);
        world.setBlockState(blockPos.east(), getCoreSurrounding(fillerBlock, random), 4);
        world.setBlockState(blockPos.west(), getCoreSurrounding(fillerBlock, random), 4);


        //generating pillars
        Iterator<BlockPos> pillars = BlockPos.iterate(blockPos.add(-2, 0, -2), blockPos.add(2, 0, 2)).iterator();
        maxHeight = getRandomHeight(random, maxHeight);

        while (pillars.hasNext()) {
            BlockPos blockPos2 = pillars.next();
            int distanceSq = (int) Util.distanceSq(blockPos, blockPos2);

            if (distanceSq < 8) {
                int start = random.nextInt(3) - 3;

                if (blockPos.equals(blockPos2)) {
                    generatePillar(world, blockPos2, start, maxHeight, fillerBlock);
                }

                int sidePillarMaxHeight = distanceSq == 1 ? maxHeight - 2 : getRandomHeight(random, maxHeight - (int) (distanceSq * 1.5));
                generatePillar(world, blockPos2, start, getRandomHeight(random, sidePillarMaxHeight), fillerBlock);
            }
        }

        return true;
    }

    private int getRandomHeight(Random random, int maxHeight) {
        return Math.max(0, maxHeight - 3 + random.nextInt(maxHeight - (maxHeight - 4)));
    }

    private BlockState getCoreSurrounding(BlockState fillerBlock, Random random) {
        if (random.nextInt(2) == 0) {
            return AequitasBlocks.PRIMAL_ESSENCE_BLOCK.getDefaultState();
        }

        return fillerBlock;
    }

    private void generatePillar(StructureWorldAccess world, BlockPos pos, int start, int stop, BlockState block) {
        if (stop == 0) return;
        for (int i = start; i <= stop; i++) {
            if (shouldReplace(world.getBlockState(pos.add(0, i, 0)))) {
                world.setBlockState(pos.add(0, i, 0), block, 4);
            }
        }

        //Fixing "floating" pillars
        pos = pos.add(0, start, 0);

        while (world.isAir(pos.down())) {
            world.setBlockState(pos.down(), block, 4);
            pos = pos.down();
        }
    }

    private boolean shouldReplace(BlockState blockState) {
        if (blockState.getBlock().equals(AequitasBlocks.PRIMAL_ESSENCE_BLOCK)) return false;
        if (blockState.getBlock().equals(AequitasBlocks.PRIMORDIAL_ESSENCE_BLOCK)) return false;
        if (blockState.getBlock().equals(AequitasBlocks.PRISTINE_ESSENCE_BLOCK)) return false;
        return true;
    }
}
