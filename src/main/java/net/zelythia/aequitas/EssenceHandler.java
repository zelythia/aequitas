package net.zelythia.aequitas;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.zelythia.aequitas.mixin.SmithingTransformRecipeAccessor;
import net.zelythia.aequitas.networking.NetworkingHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EssenceHandler {

    public static final Map<Item, Long> map = new HashMap<>();


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

    public static void setCustomRecipes(Map<Item, List<Recipe<?>>> customRecipes) {
//        RecipeMapper.customRecipes.clear();
//        RecipeMapper.customRecipes.putAll(map);

        customRecipes.forEach((item, recipes) -> {
            if(!RecipeMapper.itemRecipes.containsKey(item)){
                RecipeMapper.itemRecipes.put(item ,recipes);
            }
            else{
                RecipeMapper.itemRecipes.get(item).addAll(recipes);
            }
        });
    }

    public static void reloadEssenceValues(Map<Item, Long> newValues) {
        if (newValues.size() > 0) map.clear();

        map.putAll(newValues);
        RecipeMapper.mapRecipes(recipeManager);
        cleanEssenceMap();

        NetworkingHandler.updateEssence();
    }


    private static class RecipeMapper {
        private static final Map<RecipeType<?>, Long> craftingCost = new HashMap<>();

        private static final Map<Item, List<Recipe<?>>> itemRecipes = new HashMap<>();

        static final ArrayList<Item> no_value = new ArrayList<>();

        private static void mapRecipes(RecipeManager recipeManager) {
            Aequitas.LOGGER.info("Started mapping recipes");

            long startTime = System.currentTimeMillis();
            if (recipeManager == null || registryManager == null) return;

            for (Recipe<?> recipe : recipeManager.values()) {
                Item output = recipe.getOutput(registryManager).getItem();

                if (!itemRecipes.containsKey(output)) {
                    itemRecipes.put(output, new ArrayList<>());
                }
                if(!itemRecipes.get(output).contains(recipe)){
                    if(recipe instanceof SmithingTransformRecipe){
                        SmithingTransformRecipeAccessor smithingRecipe = (SmithingTransformRecipeAccessor) recipe;
                        DefaultedList<Ingredient> ingredients = DefaultedList.copyOf(Ingredient.EMPTY, smithingRecipe.getTemplate(), smithingRecipe.getBase(), smithingRecipe.getAddition());

                        itemRecipes.get(output).add(new ShapelessRecipe(new Identifier("aequitas", "custom"),"custom", CraftingRecipeCategory.MISC, new ItemStack(output, recipe.getOutput(registryManager).getCount()), ingredients));
                    }
                    else itemRecipes.get(output).add(recipe);
                }
            }


//            Item testItem = Registries.ITEM.get(new Identifier("minecraft", "raw_iron"));
//            RecipeMapper.calculateValue(testItem, itemRecipes.get(testItem), false);


            for (Map.Entry<Item, List<Recipe<?>>> entry : itemRecipes.entrySet()) {
                Item key = entry.getKey();
                List<Recipe<?>> value = entry.getValue();
                calculateValue(key, value, true);
            }

            List<Item> reRun = List.copyOf(no_value);
            no_value.clear();
            current_run.clear();
            reRun.forEach(item -> {
                RecipeMapper.calculateValue(item, itemRecipes.get(item), false);
            });



            Aequitas.LOGGER.info("Finished mapping recipes. Time elapsed: {}ms", System.currentTimeMillis() - startTime);
            if (!no_value.isEmpty()){
                StringBuilder s = new StringBuilder();
                for (Item item : no_value) {
                    s.append(Registries.ITEM.getId(item));
                }
                Aequitas.LOGGER.debug("Could not calculate essence values: [{}]", s);
                Aequitas.LOGGER.warn("Could not calculate essence values for {} items.", no_value.size());
            }


            List<Item> noValue = new ArrayList<>();
            Registries.ITEM.getEntrySet().forEach(registryKeyItemEntry -> {
                if (!map.containsKey(registryKeyItemEntry.getValue())) {
                    if (!(registryKeyItemEntry.getValue() instanceof SpawnEggItem))
                        noValue.add(registryKeyItemEntry.getValue());
                }
            });
            if (!noValue.isEmpty()){
                StringBuilder s = new StringBuilder();
                for (Item item : noValue) {
                    s.append(Registries.ITEM.getId(item));
                }
                Aequitas.LOGGER.debug("Items with no value: [{}]", s);
                Aequitas.LOGGER.warn("There are no essence values for {} items.", noValue.size());
            }
        }


        private static final ArrayList<Item> current_run = new ArrayList<>();

        private static long calculateValue(Item item, List<Recipe<?>> recipes, boolean b) {

//            System.out.println(item);

            //Item has already been mapped
            if (getEssenceValue(item) > 0) {
                return getEssenceValue(item);
            }

            long lowest_recipe_cost = 0L;

            if (recipes == null) {
                if (!no_value.contains(item)) no_value.add(item);
                return 0;
            }

            if (current_run.contains(item)) {
                return 0;
            }

            current_run.add(item);
            recipes:
            for (Recipe<?> recipe : recipes) {
                DefaultedList<Ingredient> inputs = recipe.getIngredients();

                long recipe_cost = 0;

                for (Ingredient ingredient : inputs) {
                    ItemStack[] stacks = ingredient.getMatchingStacks();

                    if(stacks.length > 0) {
                        long lowestIngredientCost = 0;
                        for (ItemStack stack : stacks) {
                            if (stack.getItem() == item) continue;

                            long l = calculateValue(stack.getItem(), itemRecipes.get(stack.getItem()), b);
                            l = l * stack.getCount();

                            if(recipe instanceof CraftingRecipe craftingRecipe){
                                DefaultedList<ItemStack> remainder = craftingRecipe.getRemainder(new RecipeCalculationInputInventory(stack));
                                l -= EssenceHandler.getEssenceValue(remainder.get(0));
                            }

                            if (lowestIngredientCost == 0 || l < lowestIngredientCost) lowestIngredientCost = l;
                        }

                        if (lowestIngredientCost == 0 && b) {
                            continue recipes;
                        }
                        recipe_cost += lowestIngredientCost;
                    }
                }

                ItemStack output = recipe.getOutput(registryManager);

                //Adding crafting costs for specific crafting type like e.g. smelting
                if(recipe_cost > 0){
                    if(craftingCost.getOrDefault(recipe.getType(), 0L) < 0) continue;

                    recipe_cost += craftingCost.getOrDefault(recipe.getType(), 0L);
                }

                if (output.getCount() != 0) {
                    long l = recipe_cost / output.getCount();
                    recipe_cost = (l<1 && recipe_cost > 0) ? 1 : l;
                }

                if (lowest_recipe_cost == 0 || (recipe_cost > 0 && recipe_cost < lowest_recipe_cost)) lowest_recipe_cost = recipe_cost;
            }

            if(lowest_recipe_cost > 0){
                if(map.getOrDefault(item, Long.MAX_VALUE) > lowest_recipe_cost){
                    map.put(item, lowest_recipe_cost);
                }

                no_value.remove(item);
//                current_run.remove(item);
//                current_run.clear();
            }
            else if (!no_value.contains(item)) no_value.add(item);

//            System.out.println(item+": "+lowest_recipe_cost);
            return lowest_recipe_cost;
        }
    }


    public static long getEssenceValue(Item item) {
        return map.getOrDefault(item, -1L);
    }

    public static long getEssenceValue(ItemStack stack) {
        if (stack.isDamageable()) {
            float m = (float) stack.getDamage() / stack.getMaxDamage();
            return (long) (getEssenceValue(stack.getItem()) * stack.getCount() * m);
        }
        return getEssenceValue(stack.getItem()) * stack.getCount();
    }

    public static int size() {
        return map.size();
    }
}
