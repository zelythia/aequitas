package net.zelythia.aequitas.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.zelythia.aequitas.Util;

import java.util.stream.Stream;

public class CraftingParticle extends SpriteBillboardParticle {
    protected final double startX;
    protected final double startY;
    protected final double startZ;
    protected double maxDistanceSq;

    public CraftingParticle(ClientWorld clientWorld, double x, double y, double z, double velX, double velY, double velZ) {
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

        if (Util.distanceSq(startX, startZ, this.x, this.z) >= maxDistanceSq) {
            this.markDead();
        } else {
            double newY = startY + calculateHeight(Util.distance(startX, startZ, this.x, this.z));
            this.velocityY = newY -y;
            move(this.velocityX, this.velocityY, this.velocityZ);
        }
    }


    private double calculateHeight(double x) {
        // y = \frac{-x(x-a)}{a^2/4} * maxHeight
        return ( (-x*(x-Math.sqrt(this.maxDistanceSq)))/(this.maxDistanceSq/4d) ) * 0.4d;
    }


    @Override
    protected int getBrightness(float tint) {
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        if (this.world == null) return 0;
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
            CraftingParticle craftingParticle = new CraftingParticle(clientWorld, d, e, f, g, h, i);
            craftingParticle.setSprite(this.spriteProvider);
            return craftingParticle;
        }
    }
}
