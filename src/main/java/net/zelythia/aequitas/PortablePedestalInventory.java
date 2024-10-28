package net.zelythia.aequitas;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.zelythia.aequitas.component.Components;
import net.zelythia.aequitas.essence.EssenceHandler;
import net.zelythia.aequitas.item.AequitasItems;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PortablePedestalInventory implements Inventory {

    public ItemStack item;

    public long storedEssence;
    public List<Item> unlockedItems = new ArrayList<>();

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(11, ItemStack.EMPTY);
    private String filter = "";
    private int page = 0;
    public int maxPage;

    public PortablePedestalInventory(ItemStack item) {
        if (item.getItem() != AequitasItems.PORTABLE_PEDESTAL) return;

        this.item = item;

        storedEssence = item.getOrDefault(Components.STORED_ESSENCE, 0L);

        List<Identifier> unlocked = item.getOrDefault(Components.UNLOCKED_ITEMS, List.of());
        for (Identifier i : unlocked) {
            Item item1 = Registries.ITEM.get(i);
            if (item1 != Items.AIR) unlockedItems.add(item1);
        }


        updateFilter("", 0);
    }

    public void updateFilter(String filter, int page) {
        this.filter = filter;
        this.page = page;
        this.maxPage = unlockedItems.size() / 10;

        List<Item> list = unlockedItems.stream().filter(item1 -> Registries.ITEM.getId(item1).toString().contains(filter)).collect(Collectors.toList());

        items.clear();

        for (int i = 0; i + 10 * page < list.size() && i < 10; i++) {
            items.set(i + 1, new ItemStack(list.get(i + 10 * page)));
        }

        markDirty();
    }

    @Override
    public int size() {
        return 11;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        long e = EssenceHandler.getEssenceValue(items.get(slot));
        if (storedEssence < e) return ItemStack.EMPTY;

        if (e * amount <= storedEssence) {
            storedEssence -= e * amount;
            essenceToTag();
            return new ItemStack(items.get(slot).getItem(), amount);
        }

        int newAmount = (int) (storedEssence / e);
        storedEssence -= e * newAmount;
        essenceToTag();
        return new ItemStack(items.get(slot).getItem(), newAmount);
    }


    @Override
    public ItemStack removeStack(int slot) {
        if (slot == 0) return Inventories.removeStack(items, slot);

        long e = EssenceHandler.getEssenceValue(items.get(slot));
        if (e <= storedEssence) {
            storedEssence -= e;
            essenceToTag();
            return items.get(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot == 0) {
            long e = EssenceHandler.getEssenceValue(stack);
            if (e > 0) {
                this.storedEssence += e;
                if (!unlockedItems.contains(stack.getItem())) {
                    this.unlockedItems.add(stack.getItem());
                    updateFilter(this.filter, this.page);
                }
                markDirty();
            }
        } else {
            items.set(slot, stack);
        }
    }


    public void essenceToTag() {
        if (item == null) return;
        this.item.set(Components.STORED_ESSENCE, storedEssence);
    }


    @Override
    public void markDirty() {
        if (item == null) return;

        this.item.set(Components.STORED_ESSENCE, storedEssence);
        this.item.set(Components.UNLOCKED_ITEMS, unlockedItems.stream().map(Registries.ITEM::getId).toList());
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
