package net.zelythia.aequitas.client.block.entity;


import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.*;
import net.zelythia.aequitas.block.entity.SamplingPedestalBlockEntity;

@Environment(EnvType.CLIENT)
public class SamplingPedestalBlockEntityRenderer extends BlockEntityRenderer<SamplingPedestalBlockEntity> {
    public SamplingPedestalBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SamplingPedestalBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(blockEntity != null){
            matrices.push();

            //blockEntity.getWorld().isClient always true

            // Calculate the current offset in the y value
            double offset = Math.sin((blockEntity.getWorld().getTime() + tickDelta) / 8.0) / 8.0;
            // Move the item
            matrices.translate(0.5, 1.35 + offset, 0.5);


            // Rotate the item
            matrices.multiply(new Vector3f(0,1,0).getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 4));

            ItemStack stack = blockEntity.getStack(0);
            if(stack.isEmpty()) stack = blockEntity.getLastConsumedItem();

            int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers);

            // Mandatory call after GL calls
            matrices.pop();
        }
    }
}
