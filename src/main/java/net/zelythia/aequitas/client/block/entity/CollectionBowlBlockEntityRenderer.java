package net.zelythia.aequitas.client.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.block.entity.CollectionBowlBlockEntity;
import net.zelythia.aequitas.client.particle.Particles;

@Environment(EnvType.CLIENT)
public class CollectionBowlBlockEntityRenderer extends BlockEntityRenderer<CollectionBowlBlockEntity> {

    public static final Identifier TEXTURE = new Identifier(Aequitas.MOD_ID, "textures/entity/collection_bowl_essence.png");

    public CollectionBowlBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(CollectionBowlBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity != null) {
            matrices.push();

            double y = 0.751 + blockEntity.getClientCollectionProgress() * 0.1865;

            matrices.translate(0.5, y, 0.5);

            matrices.scale(0.0625f, 0.0625f, 0.0625f);

            float r = Particles.TIER_COLORS[blockEntity.tier][0];
            float g = Particles.TIER_COLORS[blockEntity.tier][1];
            float b = Particles.TIER_COLORS[blockEntity.tier][2];
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE));

            vertexConsumer.vertex(matrices.peek().getModel(), -6, 0, 6).color(r, g, b, 1f).texture(0, 12).overlay(overlay).light(light).normal(matrices.peek().getNormal(), 0, 1, 0).next();
            vertexConsumer.vertex(matrices.peek().getModel(), 6, 0, 6).color(r, g, b, 1).texture(12, 12).overlay(overlay).light(light).normal(matrices.peek().getNormal(), 0, 1, 0).next();
            vertexConsumer.vertex(matrices.peek().getModel(), 6, 0, -6).color(r, g, b, 1f).texture(12, 0).overlay(overlay).light(light).normal(matrices.peek().getNormal(), 0, 1, 0).next();
            vertexConsumer.vertex(matrices.peek().getModel(), -6, 0, -6).color(r, g, b, 1f).texture(0, 0).overlay(overlay).light(light).normal(matrices.peek().getNormal(), 0, 1, 0).next();
            matrices.pop();
        }
    }
}
