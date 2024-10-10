package net.zelythia.aequitas.essence;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.mixin.SmithingTransformRecipeAccessor;
import net.zelythia.aequitas.networking.NetworkingHandler;

import java.util.*;

import static net.zelythia.aequitas.item.AequitasItems.ESSENCE_HOLDER;


public class EssenceHandler {
    private static final int INGREDIENT_LIMIT = 64;
    private static final int MAX_TIME = 60000;

    public static Map<Item, Long> map = new HashMap<>();

    private static RecipeManager recipeManager;
    private static DynamicRegistryManager registryManager;

    public static void registerRecipeManager(RecipeManager r) {
        recipeManager = r;
    }

    public static void registerRegistryManager(DynamicRegistryManager r) {
        registryManager = r;
    }

    private static void cleanEssenceMap() {
        map.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }

    public static void setCraftingCost(Map<RecipeType<?>, Long> map) {
        RecipeMapper.craftingCost.clear();
        RecipeMapper.craftingCost.putAll(map);
    }

    public static void setCustomRecipes(Map<Item, List<SimplifiedRecipe>> customRecipes) {
        RecipeMapper.itemRecipes.clear();
        RecipeMapper.reversedRecipes.clear();

        customRecipes.forEach((item, recipes) -> {
            RecipeMapper.itemRecipes.computeIfAbsent(item, key -> new ArrayList<>()).addAll(recipes);
        });
    }

    public static void reloadEssenceValues(Map<Item, Long> newValues) {
        if (!newValues.isEmpty()) map.clear();

        map.put(ESSENCE_HOLDER, 1L);
        map.putAll(newValues);
        RecipeMapper.mapRecipes(recipeManager);
        cleanEssenceMap();

        NetworkingHandler.updateEssence();
    }


    private static class RecipeMapper {
        private static final Map<RecipeType<?>, Long> craftingCost = new HashMap<>();

        private static Map<Item, List<SimplifiedRecipe>> itemRecipes = new HashMap<>();
        private static final Map<Item, List<SimplifiedRecipe>> reversedRecipes = new HashMap<>();

        private static void mapRecipes(RecipeManager recipeManager) {
            if (recipeManager == null || registryManager == null) return;

            Aequitas.LOGGER.info("Started mapping recipes");
            long startTime = System.currentTimeMillis();

            for (Recipe<?> recipe : recipeManager.values()) {
                Item output = recipe.getOutput(registryManager).getItem();

                if(System.currentTimeMillis() - startTime > 60000) {
                    Aequitas.LOGGER.error("Recipe: " + recipe.getOutput(registryManager).getItem());
                    return;
                }

                if (!itemRecipes.containsKey(output)) {
                    itemRecipes.put(output, new ArrayList<>());
                }

                //Transforming smithing recipes into normal ones that can be handled by aequitas
                if (recipe instanceof SmithingTransformRecipe) {
                    SmithingTransformRecipeAccessor smithingRecipe = (SmithingTransformRecipeAccessor) recipe;

                    itemRecipes.get(output).add(new SimplifiedRecipe(List.of(SimplifiedIngredient.of(smithingRecipe.getTemplate()), SimplifiedIngredient.of(smithingRecipe.getBase()), SimplifiedIngredient.of(smithingRecipe.getAddition())), recipe.getOutput(registryManager), recipe.getType(), false));
                } else {
                    itemRecipes.get(output).add(SimplifiedRecipe.of(recipe, registryManager));

                    //Creating a reverse recipe for every ingredient in the original one
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        for (ItemStack stack : ingredient.getMatchingStacks()) {
                            if (!stack.getRecipeRemainder().isEmpty())
                                continue; //Ignoring recipe remainders for reversed recipes

                            int outputCount = 0;
                            List<SimplifiedIngredient> ingredients = new ArrayList<>();
                            ingredients.add(SimplifiedIngredient.of(recipe.getOutput(registryManager)));

                            for (Ingredient ingredient2 : recipe.getIngredients()) {
                                if (ingredient2.equals(ingredient)) {
                                    outputCount += stack.getCount();
                                    continue;
                                }

                                List<StackHolder> reversedMatchingStacks = new ArrayList<>();
                                for (int i = 0; i < ingredient2.getMatchingStacks().length && i < INGREDIENT_LIMIT; i++) {
                                    ItemStack ingredientStack = ingredient2.getMatchingStacks()[i];

                                    reversedMatchingStacks.add(new StackHolder(ingredientStack.getItem(), ingredientStack.getCount() * -1, new ItemStack(ingredientStack.getItem())));

                                    ingredients.add(new SimplifiedIngredient(reversedMatchingStacks.toArray(new StackHolder[0])));

                                    if(System.currentTimeMillis() - startTime > MAX_TIME) {
                                        Aequitas.LOGGER.error("Recipe mapping took to long");
                                        return;
                                    }
                                }
                            }

                            reversedRecipes.computeIfAbsent(stack.getItem(), item -> new ArrayList<>()).add(new SimplifiedRecipe(ingredients, new ItemStack(stack.getItem(), outputCount), recipe.getType(), true));
                        }
                    }
                }

            }

            Aequitas.LOGGER.info("Finished Recipe Mapping, starting essence calculation");

            //Normal Recipes
            for (Map.Entry<Item, List<SimplifiedRecipe>> entry : itemRecipes.entrySet()) {
                calculateEssence(entry.getKey());
            }

            //With Reversed Recipes
            reversedRecipes.forEach((item, r) -> itemRecipes.computeIfAbsent(item, i -> new ArrayList<>()).addAll(r));
            for (Map.Entry<Item, List<SimplifiedRecipe>> entry : itemRecipes.entrySet()) {
                calculateEssence(entry.getKey());
            }


            Aequitas.LOGGER.info("Finished mapping recipes. Time elapsed: {}ms", System.currentTimeMillis() - startTime);
            List<String> noValue = new ArrayList<>();
            Registries.ITEM.getEntrySet().forEach(registryEntry -> {
                if (!map.containsKey(registryEntry.getValue()) && !(registryEntry.getValue() instanceof SpawnEggItem)) {
                    noValue.add(registryEntry.getKey().getValue().toString());
                }
            });
//            Aequitas.LOGGER.info("Could not calculate essence values: {}", Arrays.toString(noValue.toArray()));
            Aequitas.LOGGER.warn("Could not calculate essence values for {} items.", noValue.size());
        }


        private static void calculateEssence(Item requestedItem) {
            if (getEssenceValue(requestedItem) > 0) return;

            long startTime = System.currentTimeMillis();
            ArrayList<Item> visited = new ArrayList<>();
            Stack<Item> itemStack = new Stack<>();
            itemStack.push(requestedItem);

            while (!itemStack.isEmpty()) {
                if(System.currentTimeMillis() - startTime > MAX_TIME) {
                    Aequitas.LOGGER.error("Essence calculation for item {} took too long", requestedItem);
                    return;
                }

                Item item = itemStack.peek();

                List<SimplifiedRecipe> recipes = itemRecipes.getOrDefault(item, new ArrayList<>());
                long lowestRecipeCost = 0L;
                boolean pushed = false;
                boolean usedReversed = false;

                recipes:
                for (SimplifiedRecipe recipe : recipes) {
                    if (craftingCost.getOrDefault(recipe.type(), 0L) < 0) continue; // Skip disabled recipes
                    if (lowestRecipeCost > 0L && !usedReversed && recipe.isReversed())
                        continue; //skip reversed recipes if we already found a value using a normal one (only happens in second run)

                    ItemStack output = recipe.output().copy();
                    List<SimplifiedIngredient> ingredients = recipe.ingredients();
                    long recipeCost = 0;

                    for (SimplifiedIngredient ingredient : ingredients) {
                        StackHolder[] stacks = ingredient.matchingStacks();

                        if (stacks.length > 0) {
                            long lowestIngredientCost = 0;
                            boolean zeroAllowed = false;

                            for (StackHolder stack : stacks) {
                                if (stack.item() == item) { // handles self crafting items like armor trims
                                    zeroAllowed = true;
                                    output.setCount(output.getCount() - stack.count());
                                    continue;
                                }

                                long itemValue = map.getOrDefault(stack.item(), 0L);

                                if (itemValue <= 0 && !visited.contains(stack.item()) && !itemStack.contains(stack.item())) {
                                    itemStack.push(stack.item());
                                    pushed = true;
                                }

                                itemValue = itemValue * stack.count();

                                if (!stack.stack().getRecipeRemainder().isEmpty()) {
                                    itemValue -= EssenceHandler.getEssenceValue(stack.stack().getRecipeRemainder());
                                    zeroAllowed = true;
                                }

                                if (recipe.isReversed() && itemValue < 0) {
                                    if (lowestIngredientCost == 0 || itemValue > lowestIngredientCost) {
                                        lowestIngredientCost = itemValue;
                                    }
                                } else {
                                    if (lowestIngredientCost == 0 || itemValue < lowestIngredientCost) {
                                        lowestIngredientCost = itemValue;
                                    }
                                }
                            }

                            if (lowestIngredientCost == 0 && !zeroAllowed && !pushed) {
                                continue recipes;
                            }
                            recipeCost += lowestIngredientCost;
                        }
                    }

                    if (recipeCost > 0) {
                        if (recipe.isReversed()) recipeCost -= craftingCost.getOrDefault(recipe.type(), 0L);
                        else recipeCost += craftingCost.getOrDefault(recipe.type(), 0L);
                    }

                    if (output.getCount() != 0) {
                        long l = (long) Math.ceil((double) recipeCost / output.getCount());
                        recipeCost = (l < 1 && recipeCost > 0) ? 1 : l;
                    }

                    if (recipe.isReversed()) {
                        //Reversed recipes should always use the highes amount of essence for balancing reasons
                        if (lowestRecipeCost == 0 || (usedReversed && recipeCost > lowestRecipeCost)) {
                            lowestRecipeCost = recipeCost;
                            usedReversed = true;
                        }
                    } else if (lowestRecipeCost == 0 || (recipeCost > 0 && recipeCost < lowestRecipeCost) || (recipeCost > 0 && usedReversed)) {
                        lowestRecipeCost = recipeCost;
                        usedReversed = false;
                    }
                }

                if (!pushed) {
                    visited.add(item);
                    itemStack.pop();
                    if (lowestRecipeCost > 0) {
                        if(getEssenceValue(item) <= 0){
                            map.put(item,lowestRecipeCost);
                        }
                    }
                }
            }
        }
    }


    public static long getEssenceValue(Item item) {
        return map.getOrDefault(item, -1L);
    }

    public static long getEssenceValue(ItemStack stack) {
        if(stack.hasNbt()){
            if(!(stack.getNbt().getKeys().size() == 1 && stack.getNbt().contains("Damage"))){
                return -1;
            }
        }

        if (stack.isDamageable()) {
            float m = (float) (stack.getMaxDamage() - stack.getDamage()) / stack.getMaxDamage();
            return (long) (getEssenceValue(stack.getItem()) * stack.getCount() * m);
        }
        return getEssenceValue(stack.getItem()) * stack.getCount();
    }

    public static int size() {
        return map.size();
    }
}
