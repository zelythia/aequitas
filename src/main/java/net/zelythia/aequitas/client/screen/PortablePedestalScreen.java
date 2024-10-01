package net.zelythia.aequitas.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.client.NetworkingHandler;
import net.zelythia.aequitas.screen.PortablePedestalScreenHandler;

import java.text.NumberFormat;
import java.util.Iterator;

public class PortablePedestalScreen extends HandledScreen<PortablePedestalScreenHandler> {

    //A path to the gui texture. In this example we use the texture from the dispenser
    private final Identifier TEXTURE;

    private TextFieldWidget searchBox;

    private int textureZeroX;
    private int textureZeroY;

    private int page = 0;

    public PortablePedestalScreen(PortablePedestalScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        TEXTURE = new Identifier(Aequitas.MOD_ID, "textures/gui/portable_pedestal.png");
    }

    @Override
    protected void init() {
        super.init();
        textureZeroX = ((width - backgroundWidth) / 2);
        textureZeroY = ((height - backgroundHeight) / 2);

        searchBox = new TextFieldWidget(this.textRenderer, textureZeroX + 61, textureZeroY + 16, 107, 11, Text.translatable("ui.aequitas.portable.search"));

        TexturedButtonWidget pageUp = new TexturedButtonWidget(textureZeroX + 154, textureZeroY + 33, 13, 13, 177, 0, 13, TEXTURE, button -> {
            if (page > 0) {
                page--;
                updateSearchProperties();
            }
        });

        TexturedButtonWidget pageDown = new TexturedButtonWidget(textureZeroX + 154, textureZeroY + 52, 13, 13, 190, 0, 13, TEXTURE, button -> {
            if (!handler.inventory.getStack(10).isEmpty()) {
                page++;
                updateSearchProperties();
            }
        });

        this.addDrawableChild(pageUp);
        this.addDrawableChild(pageDown);
        this.addDrawableChild(searchBox);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        String essence = NumberFormat.getNumberInstance().format(handler.inventory.storedEssence);

        int essenceX = 3;
        int textWidth = textRenderer.getWidth(essence);
        if (textWidth <= 57) {
            essenceX = (60 - textWidth) / 2;
        }

        int essenceY = 52;
        for(Iterator<OrderedText> text = textRenderer.wrapLines(StringVisitable.plain(essence), 60).iterator(); text.hasNext(); essenceY += 9) {
            OrderedText orderedText = text.next();
            matrices.drawText(textRenderer, orderedText, textureZeroX + essenceX, textureZeroY + essenceY, 0x404040, false);
        }

        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchBox.isFocused()) {
            boolean b = searchBox.charTyped(chr, modifiers);
            this.page = 0;
            updateSearchProperties();
            return b;
        }
        return super.charTyped(chr, modifiers);
    }


    @Override
    public boolean  mouseScrolled(double mouseX, double mouseY, double amount) {
        if (mouseX >= textureZeroX + 53 && mouseX <= textureZeroX + 143) {
            if (mouseY >= textureZeroY + 32 && mouseY <= textureZeroY + 68) {
                if (amount < 0) {
                    if (!handler.inventory.getStack(10).isEmpty() && page < handler.inventory.maxPage) {
                        Aequitas.LOGGER.info(page + "/" + handler.inventory.maxPage);
                        page++;
                        updateSearchProperties();
                    }
                } else {
                    if (page > 0) {
                        page--;
                        updateSearchProperties();
                    }
                }
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBox.isFocused()) {
            boolean b = searchBox.keyPressed(keyCode, scanCode, modifiers);
            if(b){
                this.page = 0;
                updateSearchProperties();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void updateSearchProperties() {
        NetworkingHandler.updatePortablePedestalSearchProperties(handler.syncId, searchBox.getText(), page);
    }
}
