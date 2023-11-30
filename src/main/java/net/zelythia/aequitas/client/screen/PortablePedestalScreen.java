package net.zelythia.aequitas.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.networking.NetworkingHandler;
import net.zelythia.aequitas.screen.CollectionBowlScreenHandler;
import net.zelythia.aequitas.screen.PortablePedestalScreenHandler;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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

        searchBox = new TextFieldWidget(this.textRenderer, textureZeroX + 61, textureZeroY + 16, 107, 11, new LiteralText("Search"));

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

        this.addButton(pageUp);
        this.addButton(pageDown);
        this.addButton(searchBox);
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

        String essence = NumberFormat.getNumberInstance().format(handler.inventory.storedEssence);

        int essenceX = 3;
        int textWidth = textRenderer.getWidth(essence);
        if (textWidth <= 57) {
            essenceX = (60 - textWidth) / 2;
        }

        textRenderer.drawTrimmed(StringVisitable.plain(essence), textureZeroX + essenceX, textureZeroY + 52, 60, 0x404040);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
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
        //Original minecraft code for esc and tab
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        if (keyCode == 258) {
            boolean bl = !Screen.hasShiftDown();
            if (!this.changeFocus(bl)) {
                this.changeFocus(bl);
            }
            return false;
        }

        if (searchBox.isFocused()) {
            boolean b = searchBox.keyPressed(keyCode, scanCode, modifiers);
            this.page = 0;
            updateSearchProperties();
            return b;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void updateSearchProperties() {
        NetworkingHandler.updatePortablePedestalSearchProperties(handler.syncId, searchBox.getText(), page);
    }
}
