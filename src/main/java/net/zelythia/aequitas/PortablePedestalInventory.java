package net.zelythia.aequitas;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PortablePedestalInventory implements Inventory {

    public ItemStack item;

    public long storedEssence;
    private List<Item> unlockedItems = new ArrayList<>();

    private DefaultedList<ItemStack> items = DefaultedList.ofSize(11, ItemStack.EMPTY);
    private String filter = "";
    private int page = 0;

    public PortablePedestalInventory(ItemStack item){
        if(item.getItem() != Aequitas.PORTABLE_PEDESTAL_ITEM) return;

        this.item = item;

        NbtCompound nbt = item.getOrCreateTag();

        if(!nbt.contains("essence")){
            nbt.putLong("essence", 0);
        }

        storedEssence = nbt.getLong("essence");

        if(nbt.getType("unlocked") == NbtType.LIST){
            NbtList nbtList = (NbtList) nbt.get("unlocked");
            for (int i = 0; i < nbtList.size(); ++i) {
                Item item1 = Registry.ITEM.get(new Identifier(nbtList.getString(i)));
                if(item1 != Items.AIR) unlockedItems.add(item1);
            }
        }

        updateFilter("", 0);
    }

    public void updateFilter(String filter, int page){
        this.filter = filter;
        this.page = page;

        List<Item> list = unlockedItems.stream().filter(item1 -> Registry.ITEM.getId(item1).toString().contains(filter)).collect(Collectors.toList());

        items.clear();

        for(int i = 0; i+10*page < list.size() && i < 10; i++){
            items.set(i+1, new ItemStack(list.get(i+10*page)));
        }

        markDirty();
    }

    @Override
    public int size() {
        return 13;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        long e = EssenceHandler.getEssenceValue(items.get(slot).getItem());
        if(storedEssence<e) return ItemStack.EMPTY;

        if(e*amount <= storedEssence){
            storedEssence -= e*amount;
            essenceToTag();
            return new ItemStack(items.get(slot).getItem(), amount);
        }

        int newAmount = (int) (storedEssence/e);
        storedEssence -= e*newAmount;
        essenceToTag();
        return new ItemStack(items.get(slot).getItem(), newAmount);
    }


    @Override
    public ItemStack removeStack(int slot) {
        if(slot == 0) return Inventories.removeStack(items, slot);

        long e = EssenceHandler.getEssenceValue(items.get(slot).getItem());
        if(e <= storedEssence){
            storedEssence -= e;
            essenceToTag();
            return items.get(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if(slot == 0){
            long e = EssenceHandler.getEssenceValue(stack);
            if(e > 0){
                this.storedEssence += e;
                if(!unlockedItems.contains(stack.getItem())){
                    this.unlockedItems.add(stack.getItem());
                    updateFilter(this.filter, this.page);
                }
                markDirty();
            }
        }
        else{
            items.set(slot, stack);
        }
    }


    private void essenceToTag(){
        if(item == null) return;
        this.item.getTag().putLong("essence", storedEssence);
    }


    @Override
    public void markDirty() {
        if(item == null) return;

        this.item.getTag().putLong("essence", storedEssence);

        NbtList nbtList = new NbtList();

        for(Item item1: unlockedItems){
            Identifier identifier = Registry.ITEM.getId(item1);
            nbtList.add(NbtString.of((identifier == null ? "minecraft:air" : identifier.toString())));
        }

        this.item.getTag().put("unlocked", nbtList);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        items.clear();
    }
}
