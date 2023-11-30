package net.zelythia.aequitas.client.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.BooleanOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigScreen extends GameOptionsScreen {

    private ButtonListWidget list;

    public static final BooleanOption SHOW_TOOLTIP = new BooleanOption(
            "ui.aequitas.config.showTooltip",
            new TranslatableText("ui.aequitas.desc.showTooltip"),
            gameOptions1 -> AequitasConfig.config.getOrDefault("showTooltip", false),
            (gameOptions1, showTooltip) -> AequitasConfig.config.setOrCreate("showTooltip", showTooltip)
    );

    public static final BooleanOption PLAY_AMBIENT_SOUND = new BooleanOption(
            "ui.aequitas.config.playAmbientSound",
            new TranslatableText("ui.aequitas.desc.playAmbientSound"),
            gameOptions1 -> AequitasConfig.config.getOrDefault("playAmbientSound", false),
            (gameOptions1, playSound) -> AequitasConfig.config.setOrCreate("playAmbientSound", playSound)
    );

    public static final BooleanOption DISPLAY_FLIGHT_DURATION = new BooleanOption(
            "ui.aequitas.config.displayFlightDuration",
            new TranslatableText("ui.aequitas.desc.displayFlightDuration"),
            gameOptions1 -> AequitasConfig.config.getOrDefault("displayFlightDuration", false),
            (gameOptions1, displayFlightDuration) -> AequitasConfig.config.setOrCreate("displayFlightDuration", displayFlightDuration)
    );


    public ConfigScreen(Screen screen) {
        super(screen, MinecraftClient.getInstance().options, new LiteralText("Aequitas config"));
    }


    @Override
    protected void init(){
        AequitasConfig.config.loadConfig();

        this.list = new ButtonListWidget(this.client, this.width, this.height, 32, this.height -32, 25);

        list.addSingleOptionEntry(SHOW_TOOLTIP);
        list.addSingleOptionEntry(PLAY_AMBIENT_SOUND);
        list.addSingleOptionEntry(DISPLAY_FLIGHT_DURATION);

        this.children.add(this.list);
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, (button) -> this.client.openScreen(this.parent)));
    }

    @Override
    public void render(@NotNull MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
        super.render(matrices, mouseX, mouseY, delta);

        //Drawing tooltips to the screen
        List<OrderedText> list = getHoveredButtonTooltip(this.list, mouseX, mouseY);
        if (list != null) {
            this.renderOrderedTooltip(matrices, list, mouseX, mouseY);
        }
    }
}
