package net.zelythia.aequitas.client.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ConfigScreen extends GameOptionsScreen {

    public ConfigScreen(Screen screen) {
        super(screen, MinecraftClient.getInstance().options, Text.of("Aequitas config"));
    }

    public static final SimpleOption<Boolean> SHOW_TOOLTIP = SimpleOption.ofBoolean(
            "ui.aequitas.config.showTooltip",
            value -> Tooltip.of(Text.translatable("ui.aequitas.desc.showTooltip")),
            (optionText, value) -> Text.translatable("ui.aequitas.config.value." + value),
            AequitasConfig.config.getOrDefault("showTooltip", false),
            aBoolean -> AequitasConfig.config.setOrCreate("showTooltip", aBoolean)
    );

    public static final SimpleOption<Boolean> PLAY_AMBIENT_SOUND = SimpleOption.ofBoolean(
            "ui.aequitas.config.playAmbientSound",
            value -> Tooltip.of(Text.translatable("ui.aequitas.desc.playAmbientSound")),
            (optionText, value) -> Text.translatable("ui.aequitas.config.value."  + value),
            AequitasConfig.config.getOrDefault("playAmbientSound", true),
            aBoolean -> AequitasConfig.config.setOrCreate("playAmbientSound", aBoolean)
    );

    public static final SimpleOption<Boolean> DISPLAY_FLIGHT_DURATION = SimpleOption.ofBoolean(
            "ui.aequitas.config.displayFlightDuration",
            value -> Tooltip.of(Text.translatable("ui.aequitas.desc.displayFlightDuration")),

            (optionText, value) -> Text.translatable("ui.aequitas.config.value."  + value),
            AequitasConfig.config.getOrDefault("displayFlightDuration", true),
            aBoolean -> AequitasConfig.config.setOrCreate("displayFlightDuration", aBoolean)
    );

    @Override
    protected void init() {
        super.init();
        AequitasConfig.config.loadConfig();
    }

    @Override
    protected void addOptions() {
        body.addAll(SHOW_TOOLTIP, PLAY_AMBIENT_SOUND, DISPLAY_FLIGHT_DURATION);
    }

    protected void initFooter() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
            this.client.setScreen(this.parent);
        }).build());
    }

}
