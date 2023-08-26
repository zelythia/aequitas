package net.zelythia.aequitas;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
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

    public ResourceLoader(){
        GSON = new Gson();
        customEssenceLoader = new CustomEssenceLoader();
        customCraftingCostLoader = new CustomCraftingCostLoader();
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Aequitas.MOD_ID, "essence_loader");
    }

    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        CompletableFuture<Map<String, Long>> completableFuture = customEssenceLoader.prepareReload(manager, prepareExecutor);
        CompletableFuture<Map<String, Long>> completableFuture2 = customCraftingCostLoader.prepareReload(manager, prepareExecutor);
        CompletableFuture futures = CompletableFuture.allOf(completableFuture, completableFuture2);
        synchronizer.getClass();

        return futures.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((void_) -> {
            customEssenceLoader.applyReload((Map)completableFuture.join());
            customCraftingCostLoader.applyReload((Map)completableFuture2.join());
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

                        try {
                            Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                            JsonObject jsonObject = JsonHelper.deserialize(GSON, reader, JsonObject.class);

                            JsonObject crafting_cost = jsonObject.getAsJsonObject("crafting_cost");
                            if(crafting_cost != null){
                                crafting_cost.entrySet().forEach(entry -> {
                                    map.put(entry.getKey(), entry.getValue().getAsLong());
                                });
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
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
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Map<String, Long> map = new HashMap<>();
                objects.removeIf(jsonObject -> !jsonObject.has("priority") || !jsonObject.has("values"));
                objects.sort(Comparator.comparingInt(o -> o.get("priority").getAsInt()));

                for (JsonObject o: objects){
                    o.getAsJsonObject("values").entrySet().forEach(element -> {
                        map.put(element.getKey(), element.getValue().getAsLong());
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
                }
                else{
                    Item item = Registry.ITEM.get(new Identifier(key));
                    if(item != Items.AIR){
                        map2.put(item, value);
                    }
                }
            });

            EssenceHandler.reloadEssenceValues(map2);
        }
    }

}
