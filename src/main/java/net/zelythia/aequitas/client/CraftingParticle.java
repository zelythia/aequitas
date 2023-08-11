package net.zelythia.aequitas.client;

import com.sun.javafx.geom.Vec2d;
import jdk.nashorn.internal.ir.Block;
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

import java.util.stream.Stream;

public class CraftingParticle extends SpriteBillboardParticle {


    private final double startX;
    private final double startZ;
    public double max_distance; //squared for calculation


    public CraftingParticle(ClientWorld clientWorld, double x, double y, double z, double velX, double velY, double velZ) {
        super(clientWorld, x, y, z);
        this.startX = this.x;
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
        if(Vec2d.distanceSq(startX, startZ, this.x, this.z) >= max_distance){
            this.markDead();
        }
        else{
            this.velocityY = calculateHeight(Vec2d.distance(startX, startZ, this.x, this.z));
            move(this.velocityX, this.velocityY, this.velocityZ);
        }
    }

    private double calculateHeight(double x){
        return (2*x-Math.sqrt(this.max_distance-3))*-0.02;
    }



    @Override
    protected int getColorMultiplier(float tint) {
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        if(this.world == null) return 0;
        return this.world.isChunkLoaded(blockPos) ? WorldRenderer.getLightmapCoordinates(this.world, blockPos) : 0;
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
