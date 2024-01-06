package net.zelythia.aequitas;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.zelythia.aequitas.networking.NetworkingHandler;

import java.util.*;

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

    public static void setCraftingCost(Map<String, Long> map) {
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
        private static final Map<String, Long> craftingCost = new HashMap<>();

        private static final Map<Item, List<Recipe<?>>> itemRecipes = new HashMap<>();

        static final ArrayList<Item> no_value = new ArrayList<>();

        private static void mapRecipes(RecipeManager recipeManager) {
            long startTime = System.currentTimeMillis();
            if (recipeManager == null || registryManager == null) return;

            for (RecipeEntry<?> recipe : recipeManager.values()) {
                Item output = recipe.value().getResult(registryManager).getItem();

                if (!itemRecipes.containsKey(output)) {
                    itemRecipes.put(output, new ArrayList<>());
                }
                if(!itemRecipes.get(output).contains(recipe.value())) itemRecipes.get(output).add(recipe.value());
            }

            itemRecipes.forEach(RecipeMapper::calculateValue);

            Aequitas.LOGGER.info("Finished mapping recipes. Time elapsed: {}ms", System.currentTimeMillis() - startTime);
            if (no_value.size() > 0) Aequitas.LOGGER.error("Could not calculate essence values: " + no_value);

            List<Item> noValue = new ArrayList<>();
            Registries.ITEM.getEntrySet().forEach(registryKeyItemEntry -> {
                if (!map.containsKey(registryKeyItemEntry.getValue())) {
                    if (!(registryKeyItemEntry.getValue() instanceof SpawnEggItem))
                        noValue.add(registryKeyItemEntry.getValue());
                }
            });
            if (noValue.size() > 0) Aequitas.LOGGER.error("Items with no value: " + noValue);
        }


        private static final ArrayList<Item> current_run = new ArrayList<>();

        private static long calculateValue(Item item, List<Recipe<?>> recipes) {

            if(item == Items.COBBLESTONE_SLAB){
                int iii = 0;
            }


            //Item has already been mapped
            if (getEssenceValue(item) > 0) {
                return getEssenceValue(item);
            }

            long lowest_recipe_cost = 0L;

            if (recipes == null) {
                if (!no_value.contains(item)) no_value.add(item);
                return lowest_recipe_cost;
            }

            if (current_run.contains(item)) {
                return 0;
            }

            current_run.add(item);
            for (Recipe<?> recipe : recipes) {
                DefaultedList<Ingredient> inputs = recipe.getIngredients();

                long recipe_cost = 0;

                for (Ingredient ingredient : inputs) {
                    ItemStack[] stacks = ingredient.getMatchingStacks();

                    long lowest_stack_cost = 0;
                    for (ItemStack stack : stacks) {
                        long l = calculateValue(stack.getItem(), itemRecipes.get(stack.getItem()));
                        if (lowest_stack_cost == 0 || l < lowest_stack_cost) lowest_stack_cost = l;
                    }
                    recipe_cost += lowest_stack_cost;
                }


                ItemStack output = recipe.getResult(registryManager);
                //Adding crafting costs for specific crafting type like e.g. smelting
                recipe_cost += craftingCost.getOrDefault(recipe.getType().toString(), 0L);
                if (output.getCount() != 0) {
                    long l = recipe_cost / output.getCount();
                    recipe_cost = (l<1 && recipe_cost > 0) ? 1 : l;
                }

                if (lowest_recipe_cost == 0 || (recipe_cost > 0 && recipe_cost < lowest_recipe_cost)) lowest_recipe_cost = recipe_cost;
            }

            if(lowest_recipe_cost > 0){
                map.put(item, lowest_recipe_cost);
                no_value.remove(item);
            }
            else if (!no_value.contains(item)) no_value.add(item);

            current_run.remove(item);
            return lowest_recipe_cost;
        }


//        @Deprecated
//        private static long calculateCustomRecipeValue(Item item, List<SimplifiedIngredient> inputs) {
//
//            //Item == null can only be true for custom recipes
//            if (item == null) {
//                return 1;
//            }
//
//            if (getEssenceValue(item) > 0) {
//                return getEssenceValue(item);
//            }
//
//            if (inputs == null) {
//                return 0L;
//            }
//
//            long recipe_cost = 0;
//            for (SimplifiedIngredient ingredient : inputs) {
//                long l = calculateCustomRecipeValue(ingredient.item(), customRecipes.get(ingredient.item()));
//                if (l <= 0) {
//                    recipe_cost = 0;
//                    break;
//                }
//
//                recipe_cost += l * ingredient.count();
//            }
//
//            if (recipe_cost > 0) {
//                map.put(item, recipe_cost);
//                no_value.remove(item);
//            }
//            return recipe_cost;
//        }
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
