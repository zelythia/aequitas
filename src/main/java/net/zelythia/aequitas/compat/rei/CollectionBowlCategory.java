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
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;

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
        return Text.translatable("gui.rei.aequitas.category.collection_bowl");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Aequitas.COLLECTION_BOWL_BLOCK_ITEM_I);
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
                    var output = outputs.get(index);

                    List<Tooltip.Entry> tooltips = new ArrayList<>();
                    String chance = String.valueOf(display.outputs.get(outputs.get(index)));
                    tooltips.add(Tooltip.entry(Text.literal(chance + "%").formatted(Formatting.GRAY)));
                    applyTooltip(output, tooltips);

                    slot.entries(output);
                }
                widgets.add(slot);
            }
        }

        return widgets;
    }

    private void applyTooltip(EntryIngredient ingredient, List<Tooltip.Entry> tooltips) {
        for (var stack : ingredient) {
            ClientEntryStacks.setTooltipProcessor(stack, ((entryStack, tooltip) -> {
                tooltip.entries().addAll(1, tooltips);
                return tooltip;
            }));
        }
    }
}
