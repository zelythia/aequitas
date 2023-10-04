package net.zelythia.aequitas.client.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.zelythia.aequitas.Aequitas;
import org.jetbrains.annotations.Nullable;

public class Particles {

    public static final DefaultParticleType CRAFTING_PARTICLE;
    public static final DefaultParticleType CATALYST_PARTICLE;

    public static final Float[][] TIER_COLORS = new Float[4][3];

    static {
        CRAFTING_PARTICLE = Registry.register(Registry.PARTICLE_TYPE, new Identifier(Aequitas.MOD_ID, "crafting_particle"), FabricParticleTypes.simple());
        CATALYST_PARTICLE = Registry.register(Registry.PARTICLE_TYPE, new Identifier(Aequitas.MOD_ID, "catalyst_particle"), FabricParticleTypes.simple());

        TIER_COLORS[1][0] = 0.8313725490f;
        TIER_COLORS[1][1] = 0.9254901961f;
        TIER_COLORS[1][2] = 0.8039215686f;

        TIER_COLORS[2][0] = 0.8235294118f;
        TIER_COLORS[2][1] = 0.9058823529f;
        TIER_COLORS[2][2] = 0.9058823529f;

        TIER_COLORS[3][0] = 0.8784313725f;
        TIER_COLORS[3][1] = 0.8705882353f;
        TIER_COLORS[3][2] = 0.8392156863f;
    }


    @Nullable
    public static Particle spawnParticle(MinecraftClient client, ParticleEffect parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Camera camera = client.gameRenderer.getCamera();
        if (camera.isReady() && client.particleManager != null && client.world!=null) {

            ParticlesMode particlesMode = client.options.particles;
            if (canSpawnOnMinimal && particlesMode == ParticlesMode.MINIMAL && client.world.random.nextInt(10) == 0) {
                particlesMode = ParticlesMode.DECREASED;
            }
            if (particlesMode == ParticlesMode.DECREASED && client.world.random.nextInt(3) == 0) {
                particlesMode = ParticlesMode.MINIMAL;
            }


            if (alwaysSpawn) {
                return client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
            } else if (camera.getPos().squaredDistanceTo(x, y, z) > 1024.0) {
                return null;
            } else {
                return particlesMode == ParticlesMode.MINIMAL ? null : client.particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
            }
        } else {
            return null;
        }
    }
}
