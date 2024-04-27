package net.zelythia.aequitas.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.registry.Registries;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.compat.LootTableParser.ItemEntry;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class CollectionBowlEmiRecipe implements EmiRecipe {

    public final List<Identifier> conditions;
    public final Map<EmiStack, Double> outputs = new HashMap<>();
    public final String name;

    public static final int X = 0;
    public static final int Y = 40;

    public CollectionBowlEmiRecipe(List<ItemEntry> items, List<Identifier> conditions, String name) {
        this.conditions = conditions;
        this.name = name;

        double weight = 0;
        for (ItemEntry itemEntry : items) {
            weight += itemEntry.weight();
        }

        for (ItemEntry item : items) {
            if (!new Identifier("minecraft", "air").equals(item.id())) {
                outputs.put(EmiStack.of(Registries.ITEM.get(item.id())), ((int) ((item.weight() / weight) * 10000)) / 100d);
            }
        }
    }


    @Override
    public EmiRecipeCategory getCategory() {
        return AequitasEmiPlugin.COLLECTION_BOWL_CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        return null;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of();
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs.entrySet().stream().sorted(
                        Comparator.comparingDouble(value -> ((Map.Entry<EmiStack, Double>) value).getValue()).reversed()
                                .thenComparing(value -> ((Map.Entry<EmiStack, Double>) value).getKey().getId().getPath()))
                .flatMap(entry -> Stream.of(entry.getKey()))
                .toList();
    }

    @Override
    public int getDisplayWidth() {
        return 162;
    }

    @Override
    public int getDisplayHeight() {
        return (int) Math.ceil(outputs.size() / 9d) * 18 + Y;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        List<EmiStack> outputs = this.getOutputs();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        StringBuilder conditionText = new StringBuilder();
        for (Identifier condition : conditions) {
            conditionText.append(condition).append(" ");
        }
        conditionText.delete(conditionText.length() - 1, conditionText.length());


        Identifier icon = new Identifier(Aequitas.MOD_ID, "textures/biomes/" + name.replace("aequitas:gameplay/", "") + ".png");
        if (MinecraftClient.getInstance().getResourceManager().getResource(icon).isEmpty()) {
            icon = new Identifier("textures/painting/earth.png");
        }
        widgets.addTexture(icon, 0, 0, 32, 32, 0, 0, 32, 32, 32, 32);


        widgets.addText(Text.translatable("gui.aequitas.title." + name).append(":").formatted(Formatting.UNDERLINE), 36, 0, 0x000000, false);


        //Description
        List<OrderedText> con = textRenderer.wrapLines(Text.literal(conditionText.toString()), 128).stream().toList();
        for (int i = 0; i < con.size() && i < 3; i++) {
            TextWidget text = widgets.addText(con.get(i), 36, 10 + i * 9, 5592405, false);

            if (i == 2 && con.size() > 3) {
                widgets.addText(Text.literal("..."), text.getBounds().x() + text.getBounds().width(), 10 + i * 9, 5592405, false);
            }
        }

        widgets.addTooltip((mouseX, mouseY) -> {
            List<TooltipComponent> list = new ArrayList<>();

            if (con.size() > 3 && mouseX > 36 && mouseY > 10 && mouseX < 162 && mouseY < 40) {
                for (Identifier condition : conditions) {
                    list.add(new OrderedTextTooltipComponent(Text.literal(condition.toString()).asOrderedText()));
                }
            }

            return list;
        }, 36, 10, 162, 40);


        //Items
        for (int y = 0; y < Math.ceil(outputs.size() / 9d); ++y) {
            for (int x = 0; x < 9; ++x) {
                var index = y * 9 + x;
                if (index < outputs.size()) {
                    String chance = String.valueOf(this.outputs.get(outputs.get(index)));
                    widgets.addSlot(outputs.get(index), X + 18 * x, Y + 18 * y)
                            .appendTooltip(Text.literal(chance + "%").formatted(Formatting.GRAY)).recipeContext(this);
                }
            }
        }
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }
}
