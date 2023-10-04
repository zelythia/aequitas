package net.zelythia.aequitas;

import net.minecraft.item.Item;

public class SimplifiedIngredient {

    private final Item item;
    private final float count;

    public SimplifiedIngredient(Item item, float count){
        this.item = item;
        this.count = count;
    }

    public Item getItem(){
        return item;
    }

    public float getCount(){
        return count;
    }
}
