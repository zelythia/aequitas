package net.zelythia.aequitas;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

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

    public ResourceLoader(){
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
        CompletableFuture<Map<Item, List<SimplifiedIngredient>>> completableFuture3 = customRecipeLoader.prepareReload(manager, prepareExecutor);
        CompletableFuture futures = CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3);
        synchronizer.getClass();
        return futures.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((void_) -> {
            customEssenceLoader.applyReload((Map)completableFuture.join());
            customCraftingCostLoader.applyReload((Map)completableFuture2.join());
            customRecipeLoader.applyReload((Map)completableFuture3.join());
        }, applyExecutor);
    }


    private class CustomCraftingCostLoader {
        public CompletableFuture<Map<String, Long>> prepareReload(ResourceManager manager, Executor prepareExecutor) {
            return CompletableFuture.supplyAsync(() -> {
                Map<String, Long> map = new HashMap<>();

                try {
                    List<Resource> resources = manager.getAllResources(new Identifier(Aequitas.MOD_ID, "essence/values.json"));
                    Iterator<Resource> iterator = resources.iterator();

                    while (iterator.hasNext()){
                        Resource resource = iterator.next();
                        InputStream inputStream = resource.getInputStream();

                        Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                        JsonObject jsonObject = JsonHelper.deserialize(GSON, reader, JsonObject.class);

                        JsonObject crafting_cost = jsonObject.getAsJsonObject("crafting_cost");
                        if(crafting_cost != null){
                            crafting_cost.entrySet().forEach(entry -> {
                                try{
                                    map.put(entry.getKey(), entry.getValue().getAsLong());
                                }
                                catch (ClassCastException | IllegalStateException  e){
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
        }
    }

    private class CustomEssenceLoader {
        public CompletableFuture<Map<String, Long>> prepareReload(ResourceManager manager, Executor prepareExecutor) {
            return CompletableFuture.supplyAsync(() -> {
                List<JsonObject> objects = new ArrayList<>();

                try {
                    List<Resource> resources = manager.getAllResources(new Identifier(Aequitas.MOD_ID, "essence/values.json"));
                    Iterator<Resource> iterator = resources.iterator();

                    while (iterator.hasNext()){
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

                for (JsonObject o: objects){
                    o.getAsJsonObject("values").entrySet().forEach(element -> {
                        try{
                            map.put(element.getKey(), element.getValue().getAsLong());
                        }catch (ClassCastException | IllegalStateException  e){
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
                if(key.startsWith("#")){
                    Tag<Item> tag = ServerTagManagerHolder.getTagManager().getItems().getTag(new Identifier(key.replace("#", "")));
                    if(tag != null){
                        tag.values().forEach(item -> {
                            map2.put(item, value);
                        });
                    }
                    else{
                        Aequitas.LOGGER.error("Unknown tag {}", key);
                    }
                }
                else{
                    Item item = Registry.ITEM.get(new Identifier(key));
                    if(item != Items.AIR){
                        map2.put(item, value);
                    }
                    else{
                        Aequitas.LOGGER.error("Unknown item {}", key);
                    }
                }
            });

            EssenceHandler.reloadEssenceValues(map2);
        }
    }

    private class CustomRecipeLoader {
        public CompletableFuture<Map<Item, List<SimplifiedIngredient>>> prepareReload(ResourceManager manager, Executor prepareExecutor) {
            return CompletableFuture.supplyAsync(() -> {
                Map<Item, List<SimplifiedIngredient>> map = new HashMap<>();

                try {
                    List<Resource> resources = manager.getAllResources(new Identifier(Aequitas.MOD_ID, "essence/values.json"));
                    Iterator<Resource> iterator = resources.iterator();

                    while (iterator.hasNext()){
                        Resource resource = iterator.next();
                        InputStream inputStream = resource.getInputStream();

                        try {
                            Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                            JsonObject jsonObject = JsonHelper.deserialize(GSON, reader, JsonObject.class);

                            JsonObject recipes = jsonObject.getAsJsonObject("recipes");
                            if(recipes != null){
                                recipes.entrySet().forEach(entry -> {
                                    Item output = Registry.ITEM.get(new Identifier(entry.getKey()));
                                    if(output != Items.AIR){
                                        List<SimplifiedIngredient> ingredients = new ArrayList<>();

                                        entry.getValue().getAsJsonObject().entrySet().forEach(entry2 -> {
                                            float count = entry2.getValue().getAsFloat();

                                            if(entry2.getKey().equals("_")){
                                                ingredients.add(new SimplifiedIngredient(null, count));
                                            }
                                            else{
                                                Item item = Registry.ITEM.get(new Identifier(entry2.getKey()));
                                                if(item != Items.AIR){
                                                    if(count > 0){
                                                        ingredients.add(new SimplifiedIngredient(item, count));
                                                    }
                                                    else{
                                                        Aequitas.LOGGER.error("Item count must be greater than 0 for {} in {} recipe", entry2.getKey(), entry.getKey());
                                                    }
                                                }
                                                else{
                                                    Aequitas.LOGGER.error("Unknown ingredient {} in {}", entry2.getKey(), entry.getKey());
                                                }
                                            }
                                        });

                                        if(ingredients.size() > 0){
                                            map.put(output, ingredients);
                                        }
                                    }
                                    else{
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

        public void applyReload(Map<Item, List<SimplifiedIngredient>> map) {
            EssenceHandler.setCustomRecipes(map);
        }
    }

}
