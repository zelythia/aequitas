package net.zelythia.aequitas;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.util.collection.DefaultedList;
import net.zelythia.aequitas.networking.NetworkingHandler;

import java.util.*;

public class EssenceHandler {

    public static final Map<Item, Long> map = new HashMap<>();


    private static RecipeManager recipeManager;

    public static void registerRecipeManager(RecipeManager r){
        recipeManager = r;
    }


    private static void cleanEssenceMap(){
        map.entrySet().removeIf( entry -> entry.getValue() <= 0);
    }

    public static void setCraftingCost(Map<String, Long> map){
        RecipeMapper.craftingCost.clear();
        RecipeMapper.craftingCost.putAll(map);
    }

    public static void setCustomRecipes(Map<Item, List<ItemStack>> map){
        RecipeMapper.customRecipes.clear();
        RecipeMapper.customRecipes.putAll(map);
    }

    public static void reloadEssenceValues(Map<Item, Long> newValues){
        if(newValues.size() > 0) map.clear();

        map.putAll(newValues);
        RecipeMapper.mapRecipes(recipeManager);
        cleanEssenceMap();

        NetworkingHandler.updateEssence();
    }



    private static class RecipeMapper{
        private static final Map<String, Long> craftingCost = new HashMap<>();

        private static final Map<Item, List<ItemStack>> customRecipes = new HashMap<>();
        private static final Map<Item, List<Recipe<?>>> itemRecipes = new HashMap<>();

        static final ArrayList<Item> no_value = new ArrayList<>();

        private static void mapRecipes(RecipeManager recipeManager){
            long startTime = System.currentTimeMillis();
            if(recipeManager == null) return;

            for(Recipe<?> recipe: recipeManager.values()){
                if(!itemRecipes.containsKey(recipe.getOutput().getItem())){
                    itemRecipes.put(recipe.getOutput().getItem(), new ArrayList<>());
                }
                itemRecipes.get(recipe.getOutput().getItem()).add(recipe);
            }

            itemRecipes.forEach(RecipeMapper::calculateValue);
            customRecipes.forEach(RecipeMapper::calculateCustomRecipeValue);

            Aequitas.LOGGER.info("Finished mapping recipes. Time elapsed: {}ms", System.currentTimeMillis() - startTime);
            Aequitas.LOGGER.error("Could not calculate essence values: "+ no_value );
        }


        private static final ArrayList<Item> current_run = new ArrayList<>();
        private static long calculateValue(Item item, List<Recipe<?>> recipes){

            //Item has already been mapped
            if(getEssenceValue(item) > 0){
                return getEssenceValue(item);
            }

            long lowest_recipe_cost = 0L;

            if(recipes==null){
                if(!no_value.contains(item)) no_value.add(item);
                return lowest_recipe_cost;
            }

            if(current_run.contains(item)){
                return  0;
            }

            current_run.add(item);
            for(Recipe<?> recipe: recipes){
                DefaultedList<Ingredient> inputs = recipe.getIngredients();

                long recipe_cost = 0;

                for(Ingredient ingredient: inputs){
                    ItemStack[] stacks = ingredient.getMatchingStacksClient();

                    long lowest_stack_cost = 0;
                    for(ItemStack stack: stacks){
                        long l = calculateValue(stack.getItem(), itemRecipes.get(stack.getItem()));
                        if(lowest_stack_cost == 0 || l < lowest_stack_cost) lowest_stack_cost = l;
                    }
                    recipe_cost += lowest_stack_cost;
                }


                //Adding crafting costs for specific crafting type like e.g. smelting
                recipe_cost += craftingCost.getOrDefault(recipe.getType().toString(), 0L);
                if(recipe.getOutput().getCount() != 0){
                    recipe_cost = recipe_cost / recipe.getOutput().getCount();
                }

                if(lowest_recipe_cost == 0 || recipe_cost < lowest_recipe_cost) lowest_recipe_cost = recipe_cost;
            }

            map.put(item, lowest_recipe_cost);
            current_run.remove(item);
            return lowest_recipe_cost;
        }

        private static long calculateCustomRecipeValue(Item item, List<ItemStack> inputs){

            if(getEssenceValue(item) > 0){
                return getEssenceValue(item);
            }

            if(inputs == null){
                return 0L;
            }

            long recipe_cost = 0;
            for(ItemStack stack: inputs){
                long l = calculateCustomRecipeValue(stack.getItem(), customRecipes.get(stack.getItem()));
                if(l <= 0){
                    recipe_cost = 0;
                    break;
                }

                recipe_cost += l * stack.getCount();
            }

            if(recipe_cost > 0){
                map.put(item, recipe_cost);
                no_value.remove(item);
            }
            return recipe_cost;
        }
    }


    public static long getEssenceValue(Item item){
        return map.getOrDefault(item, -1L);
    }

    public static long getEssenceValue(ItemStack stack) {
        return getEssenceValue(stack.getItem())*stack.getCount();
    }

    public static int size(){
        return map.size();
    }
}
