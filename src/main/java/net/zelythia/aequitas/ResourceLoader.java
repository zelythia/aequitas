package net.zelythia.aequitas;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.profiler.Profiler;
import net.zelythia.aequitas.essence.EssenceHandler;
import net.zelythia.aequitas.essence.SimplifiedIngredient;
import net.zelythia.aequitas.essence.SimplifiedRecipe;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static net.zelythia.aequitas.item.AequitasItems.ESSENCE_HOLDER;

public class ResourceLoader implements IdentifiableResourceReloadListener {
    private final Gson GSON;
    private final CustomEssenceLoader customEssenceLoader;
    private final CustomCraftingCostLoader customCraftingCostLoader;
    private final CustomRecipeLoader customRecipeLoader;
    private final CustomCollectionBowlLootLoader customCollectionBowlLootLoader;

    public ResourceLoader() {
        GSON = new Gson();
        customEssenceLoader = new CustomEssenceLoader();
        customCraftingCostLoader = new CustomCraftingCostLoader();
        customRecipeLoader = new CustomRecipeLoader();
        customCollectionBowlLootLoader = new CustomCollectionBowlLootLoader();
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(Aequitas.MOD_ID, "essence_loader");
    }


    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        CompletableFuture<Map<String, Long>> completableFuture = customEssenceLoader.prepareReload(manager, prepareExecutor);
        CompletableFuture<Map<RecipeType<?>, Long>> completableFuture2 = customCraftingCostLoader.prepareReload(manager, prepareExecutor);
        CompletableFuture<Map<Item, List<SimplifiedRecipe>>> completableFuture3 = customRecipeLoader.prepareReload(manager, prepareExecutor);
        CompletableFuture<Map<Identifier, List<LootPoolEntry>>> completableFuture4 = customCollectionBowlLootLoader.prepareReload(manager, prepareExecutor);
        CompletableFuture<Void> futures = CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3, completableFuture4);
        return futures.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((void_) -> {
            customEssenceLoader.applyReload(completableFuture.join());
            customCraftingCostLoader.applyReload(completableFuture2.join());
            customRecipeLoader.applyReload(completableFuture3.join());
            customCollectionBowlLootLoader.applyReload(completableFuture4.join());
        }, applyExecutor);
    }


    private class CustomCraftingCostLoader {
        public CompletableFuture<Map<RecipeType<?>, Long>> prepareReload(ResourceManager manager, Executor prepareExecutor) {
            return CompletableFuture.supplyAsync(() -> {
                Map<RecipeType<?>, Long> map = new HashMap<>();

                try {
                    List<Resource> resources = manager.getAllResources(Identifier.of(Aequitas.MOD_ID, "essence/crafting_cost.json"));
                    Iterator<Resource> iterator = resources.iterator();

                    while (iterator.hasNext()) {
                        Resource resource = iterator.next();
                        InputStream inputStream = resource.getInputStream();

                        Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                        JsonObject modCraftingCost = JsonHelper.deserialize(GSON, reader, JsonObject.class);
                        if (modCraftingCost != null) {
                            modCraftingCost.entrySet().forEach(modEntry -> {

                                if (FabricLoader.getInstance().isModLoaded(modEntry.getKey())) {
                                    JsonObject craftingCost = modEntry.getValue().getAsJsonObject();

                                    craftingCost.entrySet().forEach(entry -> {
                                        try {
                                            map.put(Registries.RECIPE_TYPE.get(Identifier.of(modEntry.getKey(), entry.getKey())), entry.getValue().getAsLong());
                                        } catch (ClassCastException | IllegalStateException e) {
                                            Aequitas.LOGGER.error("Incorrect value for recipe type {} in {}", entry.getKey(), modEntry.getKey());
                                        }
                                    });
                                } else {
                                    Aequitas.LOGGER.debug("Mod not loaded: {}", modEntry.getKey());
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

        public void applyReload(Map<RecipeType<?>, Long> map) {
            EssenceHandler.setCraftingCost(map);
            Aequitas.LOGGER.info("Loaded custom crafting costs for {} crafting types", map.size());
        }
    }

    private class CustomEssenceLoader {
        public CompletableFuture<Map<String, Long>> prepareReload(ResourceManager manager, Executor prepareExecutor) {
            return CompletableFuture.supplyAsync(() -> {
                List<JsonObject> objects = new ArrayList<>();

                try {
                    List<Resource> resources = manager.getAllResources(Identifier.of(Aequitas.MOD_ID, "essence/values.json"));
                    Iterator<Resource> iterator = resources.iterator();

                    while (iterator.hasNext()) {
                        Resource resource = iterator.next();
                        InputStream inputStream = resource.getInputStream();

                        Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                        JsonObject modVales = JsonHelper.deserialize(GSON, reader, JsonObject.class);

                        if (modVales != null) {
                            modVales.entrySet().forEach(modEntry -> {
                                if (FabricLoader.getInstance().isModLoaded(modEntry.getKey())) {
                                    objects.add(modEntry.getValue().getAsJsonObject());
                                } else {
                                    Aequitas.LOGGER.debug("Mod not loaded: {}", modEntry.getKey());
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    Aequitas.LOGGER.error("Critical error while loading custom essence values");
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
            EssenceHandler.setCustomValues(map);
            Aequitas.LOGGER.info("Loaded {} custom item values", map.size());
        }
    }

    private class CustomRecipeLoader {
        public CompletableFuture<Map<Item, List<SimplifiedRecipe>>> prepareReload(ResourceManager manager, Executor prepareExecutor) {
            return CompletableFuture.supplyAsync(() -> {
                Map<Item, List<SimplifiedRecipe>> map = new HashMap<>();

                try {
                    List<Resource> resources = manager.getAllResources(Identifier.of(Aequitas.MOD_ID, "essence/recipes.json"));
                    Iterator<Resource> iterator = resources.iterator();

                    while (iterator.hasNext()) {
                        Resource resource = iterator.next();
                        InputStream inputStream = resource.getInputStream();

                        Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                        JsonObject modRecipes = JsonHelper.deserialize(GSON, reader, JsonObject.class);

                        if (modRecipes != null) {
                            modRecipes.entrySet().forEach(modEntry -> {
                                if (FabricLoader.getInstance().isModLoaded(modEntry.getKey())) {
                                    JsonObject recipes = modEntry.getValue().getAsJsonObject();

                                    recipes.entrySet().forEach(entry -> {
                                        Item output = Registries.ITEM.get(Identifier.of(entry.getKey()));
                                        if (output != Items.AIR) {
                                            DefaultedList<SimplifiedIngredient> ingredients = DefaultedList.of();
                                            int outputCount = entry.getValue().getAsJsonObject().get("count").getAsInt();

                                            for (Map.Entry<String, JsonElement> jsonIngredient : entry.getValue().getAsJsonObject().get("ingredients").getAsJsonObject().entrySet()) {
                                                int count = jsonIngredient.getValue().getAsInt();

                                                if (jsonIngredient.getKey().equals("_")) {
                                                    ingredients.add(SimplifiedIngredient.of(new ItemStack(ESSENCE_HOLDER, count)));
                                                } else {
                                                    Item item = Registries.ITEM.get(Identifier.of(jsonIngredient.getKey()));
                                                    if (item != Items.AIR) {
                                                        if (count > 0) {
                                                            ingredients.add(SimplifiedIngredient.of(new ItemStack(item, count)));
                                                        } else {
                                                            Aequitas.LOGGER.error("Item count must be greater than 0 for {} in {} recipe", jsonIngredient.getKey(), entry.getKey());
                                                        }
                                                    } else {
                                                        Aequitas.LOGGER.error("Unknown ingredient {} in {} for mod {}", jsonIngredient.getKey(), entry.getKey(), modEntry.getKey());
                                                    }
                                                }
                                            }

                                            if (ingredients.size() > 0) {
                                                if (!map.containsKey(output)) {
                                                    map.put(output, new ArrayList<>());
                                                }

                                                map.get(output).add(new SimplifiedRecipe(ingredients, new ItemStack(output, outputCount), null, false));
                                            }
                                        } else {
                                            Aequitas.LOGGER.error("Unknown output {}", entry.getKey());
                                        }
                                    });
                                } else {
                                    Aequitas.LOGGER.debug("Mod not loaded: {}", modEntry.getKey());
                                }
                            });
                        }

                    }
                } catch (Exception e) {
                    Aequitas.LOGGER.error("Critical error while loading custom crafting recipes");
                }

                return map;
            }, prepareExecutor);
        }

        public void applyReload(Map<Item, List<SimplifiedRecipe>> map) {
            EssenceHandler.setCustomRecipes(map);
            Aequitas.LOGGER.info("Loaded {} custom recipes", map.size());
        }
    }

    private class CustomCollectionBowlLootLoader {
        public CompletableFuture<Map<Identifier, List<LootPoolEntry>>> prepareReload(ResourceManager manager, Executor prepareExecutor) {
            return CompletableFuture.supplyAsync(() -> {
                Map<Identifier, List<LootPoolEntry>> map = new HashMap<>();

                try {
                    List<Resource> resources = manager.getAllResources(Identifier.of(Aequitas.MOD_ID, "essence/collection_bowl.json"));
                    Iterator<Resource> iterator = resources.iterator();

                    while (iterator.hasNext()) {
                        Resource resource = iterator.next();
                        InputStream inputStream = resource.getInputStream();

                        Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                        JsonObject modLoot = JsonHelper.deserialize(GSON, reader, JsonObject.class);
                        if (modLoot != null) {
                            modLoot.entrySet().forEach(modEntry -> {

                                if (FabricLoader.getInstance().isModLoaded(modEntry.getKey())) {
                                    try {
                                        JsonObject lootTables = modEntry.getValue().getAsJsonObject();
                                        lootTables.entrySet().forEach(entry -> {
                                            List<LootPoolEntry> lootPoolEntries = new ArrayList<>();
                                            for (JsonElement lootEntry : entry.getValue().getAsJsonArray()) {
                                                try{

                                                    DataResult<Pair<LootPoolEntry, JsonElement>> result = LootPoolEntryTypes.CODEC.decode(JsonOps.INSTANCE, lootEntry);
                                                    lootPoolEntries.add(result.getPartialOrThrow().getFirst());

                                                }
                                                catch (Exception e){
                                                    Aequitas.LOGGER.error("Error while loading loot pool {} for {} :", entry.getKey(), modEntry.getKey());
                                                    Aequitas.LOGGER.error(e);
                                                }
                                            }
                                            map.computeIfAbsent(Identifier.of("aequitas", entry.getKey()), k -> new ArrayList<>()).addAll(lootPoolEntries);
                                        });
                                    } catch (Exception e) {
                                        Aequitas.LOGGER.error("Critical error while loading custom loot tables for mod: " + modEntry.getKey());
                                    }
                                } else {
                                    Aequitas.LOGGER.debug("Mod not loaded: {}", modEntry.getKey());
                                }
                            });
                        }
                    }
                } catch (RuntimeException | IOException e) {
                    Aequitas.LOGGER.error(e);
                }

                //Not very clean but needs to happen here to be registered before loot tables are modified
                LootTableModifier.setCustomCollectionBowlLoot(map);
                return map;
            }, prepareExecutor);
        }

        public void applyReload(Map<Identifier, List<LootPoolEntry>> map) {
            Aequitas.LOGGER.info("Loaded custom collection bowl loot for {} mods", map.size());
        }
    }
}
