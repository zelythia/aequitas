package net.zelythia.aequitas.client.block.entity;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.zelythia.aequitas.block.entity.SamplingPedestalBlockEntity;

@Environment(EnvType.CLIENT)
public class SamplingPedestalBlockEntityRenderer implements BlockEntityRenderer<SamplingPedestalBlockEntity> {
    public SamplingPedestalBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    @Override
    public void render(SamplingPedestalBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity != null) {
            matrices.push();

            // Calculate the current offset in the y value
            double offset = Math.sin((blockEntity.getWorld().getTime() + tickDelta) / 8.0) / 8.0;

            // Move the item
            matrices.translate(0.5, 1.2 + offset, 0.5);

            // Rotate the item
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((blockEntity.getWorld().getTime() + tickDelta) * 4));


            ItemStack stack = new ItemStack(blockEntity.getCurrentlyConsuming());

            int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, lightAbove, overlay, matrices, vertexConsumers, null, 0);
//            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, lightAbove, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);

            matrices.pop();
        }
    }
}
