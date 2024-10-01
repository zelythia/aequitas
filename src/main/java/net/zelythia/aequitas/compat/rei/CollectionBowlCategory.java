package net.zelythia.aequitas.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.util.ClientEntryStacks;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.item.AequitasItems;

import java.util.ArrayList;
import java.util.List;

public class CollectionBowlCategory implements DisplayCategory<CollectionBowlDisplay> {

    public static final CategoryIdentifier<CollectionBowlDisplay> IDENTIFIER = CategoryIdentifier.of(Aequitas.MOD_ID, "collection_bowl_display");
    public static final int OUTPUT_SLOTS_X = 9;
    public static final int OUTPUT_SLOTS_Y = 3;
    public static final int X = 0;
    public static final int Y = 40;


    @Override
    public CategoryIdentifier<CollectionBowlDisplay> getCategoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("rei.category.aequitas.collection_bowl");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(AequitasItems.COLLECTION_BOWL_I);
    }


    @Override
    public int getDisplayHeight() {
        return 100;
    }

    @Override
    public int getDisplayWidth(CollectionBowlDisplay display) {
        return 162;
    }

    @Override
    public List<Widget> setupDisplay(CollectionBowlDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        List<EntryIngredient> outputs = display.getOutputEntries();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        StringBuilder conditionText = new StringBuilder();
        for (Identifier condition : display.getConditions()) {
            conditionText.append(condition).append(" ");
        }
        conditionText.delete(conditionText.length() - 1, conditionText.length());


        Widget description = Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
            //Icon
            Identifier icon = new Identifier(Aequitas.MOD_ID, "textures/biomes/" + display.getName().replace("aequitas:gameplay/", "") + ".png");
            if (MinecraftClient.getInstance().getResourceManager().getResource(icon).isEmpty()) {
                icon = new Identifier("textures/painting/earth.png");
            }
            graphics.drawTexture(icon, bounds.getMinX(), bounds.getMinY(), 32, 32, 0, 0, 32, 32, 32, 32);


            //Title
            graphics.drawText(textRenderer, Text.translatable("gui.aequitas.title." + display.getName()).append(":").formatted(Formatting.UNDERLINE), bounds.getMinX() + 36, bounds.getMinY(), 0x000000, false);

            //Description
            List<OrderedText> con = textRenderer.wrapLines(Text.literal(conditionText.toString()), 136).stream().toList();
            for (int i = 0; i < con.size() && i < 3; i++) {
                int j = graphics.drawText(textRenderer, con.get(i), bounds.getMinX() + 36, bounds.getMinY() + 10 + i * 9, 5592405, false);

                if (i == 2 && con.size() > 3) {
                    graphics.drawText(textRenderer, Text.literal("..."), j, bounds.getMinY() + 10 + i * 9, 5592405, false);
                }
            }

            //Tooltip
            if (con.size() > 3 && mouseX > bounds.getX() + 36 && mouseY > bounds.getMinY() + 10 && mouseX < bounds.getMinX() + 162 && mouseY < bounds.getMinY() + 40) {
                List<OrderedText> list = new ArrayList<>();
                for (Identifier condition : display.getConditions()) {
                    list.add(Text.literal(condition.toString()).asOrderedText());
                }
                graphics.drawOrderedTooltip(textRenderer, list, mouseX, mouseY);
            }
        });
        widgets.add(description);


        //Items
        for (int y = 0; y < OUTPUT_SLOTS_Y; ++y) {
            for (int x = 0; x < OUTPUT_SLOTS_X; ++x) {
                var slot = Widgets.createSlot(new Point(bounds.getMinX() + X + 18 * x, bounds.getMinY() + Y + 18 * y)).markOutput();
                var index = y * OUTPUT_SLOTS_X + x;
                if (index < outputs.size()) {
                    EntryIngredient output = outputs.get(index);

                    if(y == OUTPUT_SLOTS_Y - 1 && x == OUTPUT_SLOTS_X - 1) {
                        List<EntryStack<?>> overflow = new ArrayList<>();

                        for (int i = index; i < outputs.size(); i++) {
                            EntryIngredient entryStacks = outputs.get(i);
                            overflow.addAll(entryStacks);

                            double overflowChance = display.outputs.get(outputs.get(i));
                            Tooltip.Entry overflowTooltip = Tooltip.entry(Text.literal(overflowChance + "%").formatted(getColor(overflowChance)));
                            applyTooltip(entryStacks, overflowTooltip);
                        }

                        slot.entries(overflow);
                        widgets.add(slot);
                        continue;
                    }

                    double chance = display.outputs.get(outputs.get(index));
                    Tooltip.Entry tooltip = Tooltip.entry(Text.literal(chance + "%").formatted(getColor(chance)));
                    applyTooltip(output, tooltip);

                    slot.entries(output);
                }
                widgets.add(slot);
            }
        }

        return widgets;
    }

    private void applyTooltip(EntryIngredient outputs, Tooltip.Entry tooltip) {
        for (var stack : outputs) {
            ClientEntryStacks.setTooltipProcessor(stack, ((entryStack, tooltips) -> {
                tooltips.entries().add(tooltip);
                return tooltips;
            }));
        }
    }

    private Formatting getColor(double chance){
        if(chance >= 25) return Formatting.GREEN;
        if(chance >= 10) return Formatting.WHITE;
        if(chance >= 1) return Formatting.YELLOW;
        return Formatting.RED;
    }
}
