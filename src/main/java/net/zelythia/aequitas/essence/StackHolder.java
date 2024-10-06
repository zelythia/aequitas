package net.zelythia.aequitas.essence;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

record StackHolder(Item item, int count, ItemStack stack) {

    public static StackHolder of(ItemStack stack) {
        return new StackHolder(stack.getItem(), stack.getCount(), stack);
    }

    @Override
    public String toString() {
        return count + " " + item.getName();
    }
}
