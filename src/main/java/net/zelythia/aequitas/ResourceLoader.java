package net.zelythia.aequitas;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import net.fabricmc.fabric.mixin.resource.conditions.TagManagerLoaderMixin;
import net.minecraft.data.server.tag.ItemTagProvider;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.TagManagerLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.profiler.Profiler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ResourceLoader implements IdentifiableResourceReloadListener {

    private final Gson GSON;
    private final CustomEssenceLoader customEssenceLoader;
    private final CustomCraftingCostLoader customCraftingCostLoader;
    private final CustomRecipeLoader customRecipeLoader;

    public ResourceLoader() {
        GSON = new Gson();
        customEssenceLoader = new CustomEssenceLoader();
        customCraftingCostLoader = new CustomCraftingCostLoader();
        customRecipeLoader = new CustomRecipeLoader();
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Aequitas.MOD_ID, "essence_loader");
    }


    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        CompletableFuture<Map<String, Long>> completableFuture = customEssenceLoader.prepareReload(manager, prepareExecutor);
        CompletableFuture<Map<String, Long>> completableFuture2 = customCraftingCostLoader.prepareReload(manager, prepareExecutor);
        CompletableFuture<Map<Item, List<Recipe<?>>>> completableFuture3 = customRecipeLoader.prepareReload(manager, prepareExecutor);
        CompletableFuture futures = CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3);
        synchronizer.getClass();
        return futures.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((void_) -> {
            customEssenceLoader.applyReload((Map) completableFuture.join());
            customCraftingCostLoader.applyReload((Map) completableFuture2.join());
            customRecipeLoader.applyReload((Map) completableFuture3.join());
        }, applyExecutor);
    }


    private class CustomCraftingCostLoader {
        public CompletableFuture<Map<String, Long>> prepareReload(ResourceManager manager, Executor prepareExecutor) {
            return CompletableFuture.supplyAsync(() -> {
                Map<String, Long> map = new HashMap<>();

                try {
                    List<Resource> resources = manager.getAllResources(new Identifier(Aequitas.MOD_ID, "essence/crafting_cost.json"));
                    Iterator<Resource> iterator = resources.iterator();

                    while (iterator.hasNext()) {
                        Resource resource = iterator.next();
                        InputStream inputStream = resource.getInputStream();

                        Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                        JsonObject craftingCost = JsonHelper.deserialize(GSON, reader, JsonObject.class);

                        if (craftingCost != null) {
                            craftingCost.entrySet().forEach(entry -> {
                                try {
                                    map.put(entry.getKey(), entry.getValue().getAsLong());
                                } catch (ClassCastException | IllegalStateException e) {
                                    Aequitas.LOGGER.error("Incorrect value for recipe type {}", entry.getKey());
                                }
                            });
                        }
                    }
                } catch (RuntimeException | IOException e) {
                    Aequitas.LOGGER.error(e);
                }

                return map;
            }, prepareExecutor);
        }

        public void applyReload(Map<String, Long> map) {
            EssenceHandler.setCraftingCost(map);
            Aequitas.LOGGER.info("Loaded custom crafting costs for {} crafting types", map.size());
        }
    }

    private class CustomEssenceLoader {
        public CompletableFuture<Map<String, Long>> prepareReload(ResourceManager manager, Executor prepareExecutor) {
            return CompletableFuture.supplyAsync(() -> {
                List<JsonObject> objects = new ArrayList<>();

                try {
                    List<Resource> resources = manager.getAllResources(new Identifier(Aequitas.MOD_ID, "essence/values.json"));
                    Iterator<Resource> iterator = resources.iterator();

                    while (iterator.hasNext()) {
                        Resource resource = iterator.next();
                        InputStream inputStream = resource.getInputStream();

                        try {
                            Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                            JsonObject jsonObject = JsonHelper.deserialize(GSON, reader, JsonObject.class);
                            objects.add(jsonObject);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (RuntimeException | IOException e) {
                    throw new RuntimeException(e);
                }

                Map<String, Long> map = new HashMap<>();
                objects.removeIf(jsonObject -> !jsonObject.has("priority") || !jsonObject.has("values"));
                objects.sort(Comparator.comparingInt(o -> o.get("priority").getAsInt()));

                for (JsonObject o : objects) {
                    o.getAsJsonObject("values").entrySet().forEach(element -> {
                        try {
                            map.put(element.getKey(), element.getValue().getAsLong());
                        } catch (ClassCastException | IllegalStateException e) {
                            Aequitas.LOGGER.error("Incorrect value for {}", element.getKey());
                        }
                    });
                }

                return map;
            }, prepareExecutor);
        }

        public void applyReload(Map<String, Long> map) {
            Map<Item, Long> map2 = new HashMap<>();

            map.forEach((key, value) -> {
                if (key.startsWith("#")) {

                    Collection<RegistryEntry<?>> registryEntries = ResourceConditionsImpl.LOADED_TAGS.get().get(RegistryKeys.ITEM).get(new Identifier(key.replace("#", "")));

                    if(registryEntries != null){
                        for (RegistryEntry<?> registryEntry : registryEntries) {
                            registryEntry.getKey().ifPresent(registryKey -> {
                                registryKey.tryCast(RegistryKeys.ITEM).ifPresent(itemRegistryKey -> {
                                    map2.put(Registries.ITEM.get(itemRegistryKey), value);
                                });
                            });
                        }
                    }
                    else{
                        Aequitas.LOGGER.error("Unknown tag {}", key);
                    }

                } else {
                    Item item = Registries.ITEM.get(new Identifier(key));
                    if (item != Items.AIR) {
                        map2.put(item, value);
                    } else {
                        Aequitas.LOGGER.error("Unknown item {}", key);
                    }
                }
            });

            EssenceHandler.reloadEssenceValues(map2);
            Aequitas.LOGGER.info("Loaded {} custom item values", map2.size());
        }
    }

    private class CustomRecipeLoader {
        public CompletableFuture<Map<Item, List<Recipe<?>>>> prepareReload(ResourceManager manager, Executor prepareExecutor) {
            return CompletableFuture.supplyAsync(() -> {
                Map<Item, List<Recipe<?>>> map = new HashMap<>();

                try {
                    List<Resource> resources = manager.getAllResources(new Identifier(Aequitas.MOD_ID, "essence/recipes.json"));
                    Iterator<Resource> iterator = resources.iterator();

                    while (iterator.hasNext()) {
                        Resource resource = iterator.next();
                        InputStream inputStream = resource.getInputStream();

                        try {
                            Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                            JsonObject recipes = JsonHelper.deserialize(GSON, reader, JsonObject.class);

//                            JsonObject recipes = jsonObject.getAsJsonObject("recipes");
                            if (recipes != null) {
                                recipes.entrySet().forEach(entry -> {
                                    Item output = Registries.ITEM.get(new Identifier(entry.getKey()));
                                    if (output != Items.AIR) {
                                        DefaultedList<Ingredient> ingredients = DefaultedList.of();
                                        int outputCount = entry.getValue().getAsJsonObject().get("count").getAsInt();

                                        for (Map.Entry<String, JsonElement> jsonIngredient : entry.getValue().getAsJsonObject().get("ingredients").getAsJsonObject().entrySet()) {
                                            int count = jsonIngredient.getValue().getAsInt();

                                            if(jsonIngredient.getKey().equals("_")){
                                                ingredients.add(Ingredient.EMPTY);
                                            }else {
                                                Item item = Registries.ITEM.get(new Identifier(jsonIngredient.getKey()));
                                                if (item != Items.AIR) {
                                                    if (count > 0) {
                                                        ingredients.add(Ingredient.ofStacks(new ItemStack(item, count)));
                                                    } else {
                                                        Aequitas.LOGGER.error("Item count must be greater than 0 for {} in {} recipe", jsonIngredient.getKey(), entry.getKey());
                                                    }
                                                } else {
                                                    Aequitas.LOGGER.error("Unknown ingredient {} in {}", jsonIngredient.getKey(), entry.getKey());
                                                }
                                            }
                                        }

                                        if (ingredients.size() > 0) {
                                            if (!map.containsKey(output)) {
                                                map.put(output, new ArrayList<>());
                                            }

                                            map.get(output).add(new ShapelessRecipe("custom", CraftingRecipeCategory.MISC, new ItemStack(output, outputCount), ingredients));
                                        }
                                    } else {
                                        Aequitas.LOGGER.error("Unknown output {}", entry.getKey());
                                    }
                                });
                            }

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (RuntimeException | IOException e) {
                    throw new RuntimeException(e);
                }

                return map;
            }, prepareExecutor);
        }

        public void applyReload(Map<Item, List<Recipe<?>>> map) {
            EssenceHandler.setCustomRecipes(map);
            Aequitas.LOGGER.info("Loaded {} custom recipes", map.size());
        }
    }

}
