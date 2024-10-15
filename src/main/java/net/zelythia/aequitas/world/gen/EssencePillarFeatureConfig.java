package net.zelythia.aequitas.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public class EssencePillarFeatureConfig implements FeatureConfig {
    public static final Codec<EssencePillarFeatureConfig> CODEC;

    public final BlockState state;
    public final int maxHeight;

    public EssencePillarFeatureConfig(int maxHeight, BlockState state) {
        this.state = state;
        this.maxHeight = maxHeight;
    }

    static {
        CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.intRange(0, 64).fieldOf("maxHeight").forGetter((essencePillarFeatureConfig) -> {
                return essencePillarFeatureConfig.maxHeight;
            }), BlockState.CODEC.fieldOf("blockState").forGetter((essencePillarFeatureConfig) -> {
                return essencePillarFeatureConfig.state;
            })).apply(instance, EssencePillarFeatureConfig::new);
        });
    }
}
