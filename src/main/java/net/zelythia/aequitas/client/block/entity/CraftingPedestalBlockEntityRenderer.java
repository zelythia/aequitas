package net.zelythia.aequitas.client.block.entity;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.block.entity.CraftingPedestalBlockEntity;
import net.zelythia.aequitas.block.entity.SamplingPedestalBlockEntity;

@Environment(EnvType.CLIENT)
public class CraftingPedestalBlockEntityRenderer extends BlockEntityRenderer<CraftingPedestalBlockEntity> {
    public CraftingPedestalBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(CraftingPedestalBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity != null) {
            matrices.push();

            // Calculate the current offset in the y value
            double offset = Math.sin((blockEntity.getWorld().getTime() + tickDelta) / 8.0) / 8.0;

            // Move the item
            matrices.translate(0.5, 1.2 + offset, 0.5);

            // Rotate the item
            matrices.multiply(new Vec3f(0, 1, 0).getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 4));

            ItemStack stack = blockEntity.getStack(1);
            if (blockEntity.getStack(0).getItem() == Aequitas.PORTABLE_PEDESTAL_ITEM) stack = blockEntity.getStack(0);

            int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers);

            matrices.pop();
        }
    }
}
