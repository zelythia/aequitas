package net.zelythia.aequitas;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.zelythia.aequitas.networking.EssencePacket;
import net.zelythia.aequitas.networking.NetworkingHandler;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;
import java.util.function.Function;

public class EssenceHandler {

    public static final Map<Item, Long> map = new HashMap<>();

    private static RecipeManager recipeManager;
    private static ResourceManager resourceManager;

    public static void registerRecipeManager(RecipeManager r){
        recipeManager = r;
    }

    public static void registerResourceManager(ResourceManager r){
        resourceManager = r;
    }

    public static void map(){
        map.clear();

        mapDefaultValues();
        mapDatapacks();



        RecipeMapper.mapRecipes(recipeManager);
        NetworkingHandler.updateEssence();
    }

    private static void mapDefaultValues(){
        map.put(Items.STONE, 8L);
        map.put(Items.GRANITE, 8L);
        map.put(Items.DIORITE, 8L);
        map.put(Items.ANDESITE, 8L);
        map.put(Items.DIRT, 1L);
        map.put(Items.GRASS_BLOCK, 8L);
        map.put(Items.PODZOL, 8L);
        map.put(Items.COBBLESTONE, 1L);
        map.put(Items.SAND, 2L);
        map.put(Items.RED_SAND, 2L);
        map.put(Items.GRAVEL, 2L);


        map.put(Items.BAMBOO, 32L);
        map.put(Items.CLAY_BALL, 32L);
        map.put(Items.EGG, 32L);
        map.put(Items.ICE, 1L);
        map.put(Items.BEEF, 64L);
        map.put(Items.SPIDER_EYE, 128L);
        map.put(Items.SOUL_SAND, 49L);
        map.put(Items.SOUL_SOIL, 49L);
        map.put(Items.PORKCHOP, 64L);
        map.put(Items.PRISMARINE_SHARD, 256L);
        map.put(Items.PRISMARINE_CRYSTALS, 512L);
        map.put(Items.HONEY_BOTTLE, 48L);
        map.put(Items.NETHER_STAR, 139264L);
        map.put(Items.GHAST_TEAR, 4096L);
        map.put(Items.WARPED_FUNGUS, 32L);
        map.put(Items.MELON_SLICE, 16L);
        map.put(Items.OBSIDIAN, 64L);
        map.put(Items.SCUTE, 96L);
        map.put(Items.MUTTON, 64L);
        map.put(Items.BASALT, 8L);
        map.put(Items.SHULKER_SHELL, 2048L);
        map.put(Items.HEART_OF_THE_SEA, 32768L);
        map.put(Items.NAUTILUS_SHELL, 1024L);
        map.put(Items.ENDER_PEARL, 1024L);
        map.put(Items.APPLE, 128L);
        map.put(Items.VINE, 16L);
        map.put(Items.COD, 64L);
        map.put(Items.SALMON, 64L);
        map.put(Items.WHEAT, 24L);
        map.put(Items.CHICKEN, 64L);
        map.put(Items.RED_MUSHROOM, 32L);
        map.put(Items.CARROT, 64L);
        map.put(Items.POTATO, 64L);
        map.put(Items.RABBIT, 64L);
        map.put(Items.PUMPKIN, 144L);
        map.put(Items.COCOA_BEANS, 64L);
        map.put(Items.END_STONE, 8L);
        map.put(Items.CRYING_OBSIDIAN, 768L);
        map.put(Items.RABBIT_HIDE, 16L);
        map.put(Items.RABBIT_FOOT, 128L);
        map.put(Items.KELP, 4L);
        map.put(Items.INK_SAC, 16L);
        map.put(Items.BLACKSTONE, 8L);
        map.put(Items.FEATHER, 48L);
        map.put(Items.CACTUS, 8L);
        map.put(Items.STRING, 12L);
        map.put(Items.BEETROOT, 64L);
        map.put(Items.CHORUS_FLOWER, 96L);
        map.put(Items.CHORUS_PLANT, 64L);
        map.put(Items.CHORUS_FRUIT, 192L);
        map.put(Items.BLAZE_ROD, 1536L);
        map.put(Items.SLIME_BALL, 32L);
        map.put(Items.SEA_PICKLE, 16L);
        map.put(Items.BONE, 144L);
        map.put(Items.GUNPOWDER, 192L);
        map.put(Items.BROWN_MUSHROOM, 32L);
        map.put(Items.NETHERRACK, 8L);
        map.put(Items.SUGAR_CANE, 32L);
        map.put(Items.NETHER_WART, 24L);
        map.put(Items.GLOWSTONE_DUST, 384L);
        map.put(Items.SNOWBALL, 1L);
        map.put(Items.FLINT, 8L);
        map.put(Items.CREEPER_HEAD, 256L);



        map.put(Items.COAL, 128L);
        map.put(Items.IRON_INGOT, 256L);
        map.put(Items.GOLD_INGOT, 256L);
        map.put(Items.DIAMOND, 8192L);
        map.put(Items.NETHERITE_SCRAP, 12288L);

        //map tags

        for(Item item: ItemTags.LOGS.values()){
            map.put(item, 32L);
        }

        for(Item item: ItemTags.SAPLINGS.values()){
            map.put(item, 64L);
        }


        for(Item item: ItemTags.SMALL_FLOWERS.values()){
            map.put(item, 16L);
        }

        for(Item item: ItemTags.TALL_FLOWERS.values()){
            map.put(item, 32L);
        }

        for(Item item: ItemTags.LEAVES.values()){
            map.put(item, 1L);
        }
    }

    private static void mapDatapacks(){

    }



    private static class RecipeMapper{
        private static Map<RecipeType<?>, Long> crafting_cost = new HashMap<>();


        private static Map<Item, List<Recipe<?>>> item_recipes = new HashMap<>();


        static {
//            crafting_cost.put(RecipeType.CRAFTING, () -> 0L;);
        }


        private static void mapRecipes(RecipeManager recipeManager){
            if(recipeManager == null) return;

            //Remap recipes by their output item
//            recipes:
            for(Recipe<?> recipe: recipeManager.values()){

                //Checking if an item crafts itself e.g. for clearing nbt-data
//                Item output_item = recipe.getOutput().getItem();
//                DefaultedList<Ingredient> inputs = recipe.getPreviewInputs();
//                for(Ingredient ingredient: inputs){
//                    ItemStack[] stacks = ingredient.getMatchingStacksClient();
//
//                    for(ItemStack stack: stacks){
//                        if(stack.getItem()==output_item){
//                            continue recipes;
//                        }
//
//                        Item item1 = stack.getItem();
//                        if(item_recipes.containsKey(item1)){
//                            for(Recipe<?> recipe1: item_recipes.get(item1)){
//                                for(Ingredient ingredient1: recipe1.getPreviewInputs()){
//                                    for(ItemStack stack1: ingredient1.getMatchingStacksClient()){
//                                        if(stack1.getItem() == output_item){
//                                            continue recipes;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }

                if(!item_recipes.containsKey(recipe.getOutput().getItem())){
                    item_recipes.put(recipe.getOutput().getItem(), new ArrayList<>());
                }
                item_recipes.get(recipe.getOutput().getItem()).add(recipe);
            }


//            getItemValue(Items.OAK_STAIRS, item_recipes.get(Items.OAK_STAIRS));


            item_recipes.forEach( (item, recipes) -> {
                long cost = getItemValue(item, recipes);
            });

            Aequitas.LOGGER.error("These items have no value and cannot be crafted: "+ no_value );
        }


        static ArrayList<Item> no_value = new ArrayList<Item>();

        private static final ArrayList<Item> current_run = new ArrayList<>();
        private static long getItemValue(Item item, List<Recipe<?>> recipes){
            Aequitas.LOGGER.info("Started: "+item);

            //Item has already been mapped
            if(getEssenceValue(item) > 0){
                Aequitas.LOGGER.info("Already mapped");
                return getEssenceValue(item);
            }

            long lowest_recipe_cost = 0L;

            if(recipes==null){
                if(!no_value.contains(item)) no_value.add(item);
                Aequitas.LOGGER.error("no recipes for item");
                return lowest_recipe_cost;
            }

            if(current_run.contains(item)){
                return  0;
            }

            current_run.add(item);
            for(Recipe<?> recipe: recipes){
                DefaultedList<Ingredient> inputs = recipe.getPreviewInputs();

                long recipe_cost = 0;

                for(Ingredient ingredient: inputs){
                    ItemStack[] stacks = ingredient.getMatchingStacksClient();

                    long lowest_stack_cost = 0;
                    for(ItemStack stack: stacks){
                        long l = getItemValue(stack.getItem(), item_recipes.get(stack.getItem()));
                        if(lowest_stack_cost == 0 || l < lowest_stack_cost) lowest_stack_cost = l;
                    }
                    recipe_cost += lowest_stack_cost;
                }

                //Adding crafting costs for specific crafting type like e.g. smelting
                recipe_cost += 0; //Recipe Type cost map.get("crafing")
                if(recipe.getOutput().getCount() != 0){
                    recipe_cost = recipe_cost / recipe.getOutput().getCount();
                }

                if(lowest_recipe_cost == 0 || recipe_cost < lowest_recipe_cost) lowest_recipe_cost = recipe_cost;
            }

            Aequitas.LOGGER.warn(item+": "+lowest_recipe_cost);
            map.put(item, lowest_recipe_cost);
            current_run.remove(item);
            return lowest_recipe_cost;
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
