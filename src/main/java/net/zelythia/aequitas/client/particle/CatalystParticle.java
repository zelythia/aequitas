package net.zelythia.aequitas.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.zelythia.aequitas.Util;

public class CatalystParticle extends SpriteBillboardParticle {
    protected final double startX;
    protected final double startY;
    protected final double startZ;
    protected double maxDistanceSq; //squared for calculation

    public CatalystParticle(ClientWorld clientWorld, double x, double y, double z, double velX, double velY, double velZ) {
        super(clientWorld, x, y, z);
        this.startX = this.x;
        this.startY = this.y;
        this.startZ = this.z;

        this.gravityStrength = 0;
        this.collidesWithWorld = false;

        this.velocityX = velX;
        this.velocityY = velY;
        this.velocityZ = velZ;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (Util.distanceSq(startX, startY, startZ, x, y, z) >= maxDistanceSq) {
            this.markDead();
        } else {
            move(this.velocityX, this.velocityY, this.velocityZ);
        }
    }


    @Override
    protected int getBrightness(float tint) {
        BlockPos blockPos = new BlockPos((int) x, (int) y, (int) z);
        if (this.world == null) return 0; //This is missing in the original method
        return this.world.isChunkLoaded(blockPos) ? WorldRenderer.getLightmapCoordinates(this.world, blockPos) : 0;
    }

    public void setMaxDistanceSq(double maxDistanceSq) {
        this.maxDistanceSq = maxDistanceSq;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }


    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            CatalystParticle catalystParticle = new CatalystParticle(clientWorld, d, e, f, g, h, i);
            catalystParticle.setSprite(this.spriteProvider);
            return catalystParticle;
        }
    }
}
