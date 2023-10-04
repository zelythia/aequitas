package net.zelythia.aequitas.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.screen.CollectionBowlScreenHandler;

public class CollectionBowlScreen extends HandledScreen<CollectionBowlScreenHandler> {

    //A path to the gui texture. In this example we use the texture from the dispenser
    private final Identifier TEXTURE;

    public CollectionBowlScreen(CollectionBowlScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        TEXTURE = new Identifier(Aequitas.MOD_ID, "textures/gui/collection_bowl_"+handler.getSize()+".png");
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
