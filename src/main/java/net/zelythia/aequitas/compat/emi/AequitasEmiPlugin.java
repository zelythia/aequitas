package net.zelythia.aequitas.compat.emi;

import com.google.gson.JsonElement;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.util.Identifier;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.client.NetworkingHandler;
import net.zelythia.aequitas.compat.LootTableParser;

import java.util.ArrayList;
import java.util.List;

public class AequitasEmiPlugin implements EmiPlugin {
    public static final Identifier MY_SPRITE_SHEET = new Identifier("emi", "textures/gui/widgets.png");
    public static final EmiStack COLLECTION_BOWL_BLOCK_ITEM_I = EmiStack.of(Aequitas.COLLECTION_BOWL_BLOCK_ITEM_I);
    public static final EmiRecipeCategory COLLECTION_BOWL_CATEGORY
            = new EmiRecipeCategory(new Identifier(Aequitas.MOD_ID, "collection_bowl"), COLLECTION_BOWL_BLOCK_ITEM_I, new EmiTexture(MY_SPRITE_SHEET, 0, 0, 16, 16));


    @Override
    public void register(EmiRegistry registry) {
        boolean b = NetworkingHandler.updateLootTables();

        // Tell EMI to add a tab for your category
        registry.addCategory(COLLECTION_BOWL_CATEGORY);

        // Add all the workstations your category uses
        registry.addWorkstation(COLLECTION_BOWL_CATEGORY, COLLECTION_BOWL_BLOCK_ITEM_I);
        registry.addWorkstation(COLLECTION_BOWL_CATEGORY, EmiStack.of(Aequitas.COLLECTION_BOWL_BLOCK_ITEM_II));
        registry.addWorkstation(COLLECTION_BOWL_CATEGORY, EmiStack.of(Aequitas.COLLECTION_BOWL_BLOCK_ITEM_III));

        if (b) {
            for (JsonElement element : net.zelythia.aequitas.client.NetworkingHandler.LOOTTABLES.get(new Identifier("aequitas", "gameplay/biomes")).getAsJsonArray("pools")) {

                try {
                    List<Identifier> conditions = new ArrayList<>();
                    for (JsonElement condition : element.getAsJsonObject().getAsJsonArray("conditions")) {
                        LootTableParser.parseCondition(condition.getAsJsonObject(), conditions);
                    }

                    StringBuilder name = new StringBuilder();
                    List<LootTableParser.ItemEntry> entries = new ArrayList<>();
                    for (JsonElement entry : element.getAsJsonObject().getAsJsonArray("entries")) {
                        LootTableParser.parseEntry(entry.getAsJsonObject(), entries, name);
                    }

                    System.out.println("Emi: added " + name);
                    registry.addRecipe(new CollectionBowlEmiRecipe(entries, conditions, name.toString()));
                } catch (Exception e) {
                    Aequitas.LOGGER.error("Emi: Error parsing loot tables");
                }
            }
        }
    }
}
