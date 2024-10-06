package net.zelythia.aequitas.essence;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.Arrays;

public record SimplifiedIngredient(StackHolder[] matchingStacks) {

    public static SimplifiedIngredient of(Ingredient ingredient) {
        StackHolder[] matchingStacks = new StackHolder[ingredient.getMatchingStacks().length];
        for (int i = 0; i < ingredient.getMatchingStacks().length; i++) {
            matchingStacks[i] = StackHolder.of(ingredient.getMatchingStacks()[i]);
        }
        return new SimplifiedIngredient(matchingStacks);
    }

    public static SimplifiedIngredient of(ItemStack stack) {
        return new SimplifiedIngredient(new StackHolder[]{StackHolder.of(stack)});
    }

    @Override
    public String toString() {
        return Arrays.toString(matchingStacks);
    }
}
