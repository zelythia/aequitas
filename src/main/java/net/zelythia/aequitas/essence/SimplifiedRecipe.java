package net.zelythia.aequitas.essence;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record SimplifiedRecipe(List<SimplifiedIngredient> ingredients, ItemStack output, @Nullable RecipeType type, boolean isReversed) {
    public static SimplifiedRecipe of(Recipe<?> recipe, DynamicRegistryManager registryManager) {
        List<SimplifiedIngredient> ingredients = new ArrayList<>();

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredients.add(SimplifiedIngredient.of(ingredient));
        }

        return new SimplifiedRecipe(ingredients, recipe.getOutput(registryManager), recipe.getType(), false);
    }
}
