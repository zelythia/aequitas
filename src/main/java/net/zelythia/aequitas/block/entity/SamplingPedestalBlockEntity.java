package net.zelythia.aequitas.block.entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.ImplementedInventory;

public class SamplingPedestalBlockEntity extends BlockEntity implements ImplementedInventory, BlockEntityClientSerializable {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private long stored_value = 0;
    private ItemStack lastConsumedItem = ItemStack.EMPTY;

    public SamplingPedestalBlockEntity() {
        super(Aequitas.SAMPLING_PEDESTAL_BLOCK_ENTITY);
    }


    public void consumeItem(int amount){
        if(inventory.get(0).isEmpty()) return;

        long v = EssenceHandler.getEssenceValue(inventory.get(0).getItem());
        if(v > 0){
            lastConsumedItem = inventory.get(0).copy();

            if(inventory.get(0).getCount() >= amount){
                inventory.get(0).decrement(amount);
                updateListeners();
                this.stored_value += v*amount;
            }
            else{
                inventory.get(0).decrement(inventory.get(0).getCount());
                updateListeners();
                this.stored_value += v*inventory.get(0).getCount();
            }
        }
    }

    public long transferEssence(long amount){
        if(this.stored_value >= amount){
            this.stored_value -= amount;
            return amount;
        }

        this.lastConsumedItem = ItemStack.EMPTY;
        long v = this.stored_value;
        this.stored_value = 0;
        updateListeners();
        return v;
    }

    public long getValue(){
        return stored_value;
    }

    public ItemStack getLastConsumedItem(){
        return lastConsumedItem;
    }


    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public int getMaxCountPerStack() {
        return 256;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        ListTag listTag = new ListTag();
        CompoundTag compoundTag = new CompoundTag();
        Identifier identifier = Registry.ITEM.getId(this.inventory.get(0).getItem());
        compoundTag.putString("id", identifier.toString());
        compoundTag.putInt("Count", this.inventory.get(0).getCount());
        listTag.add(compoundTag);
        tag.put("Items", listTag);

        tag.putString("lastItem", Registry.ITEM.getId(this.lastConsumedItem.getItem()).toString());

        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        ListTag listTag = tag.getList("Items", 10);
        CompoundTag compoundTag = listTag.getCompound(0);
        ItemStack stack = new ItemStack((Item)Registry.ITEM.get(new Identifier(compoundTag.getString("id"))));
        stack.setCount(compoundTag.getInt("Count"));
        this.inventory.set(0, stack);

        this.lastConsumedItem = new ItemStack(Registry.ITEM.get(new Identifier(tag.getString("lastItem"))));
    }


    @Override
    public void fromClientTag(CompoundTag tag) {
        this.inventory.clear();

        ListTag listTag = tag.getList("Items", 10);
        CompoundTag compoundTag = listTag.getCompound(0);
        ItemStack stack = new ItemStack((Item)Registry.ITEM.get(new Identifier(compoundTag.getString("id"))));
        stack.setCount(compoundTag.getInt("Count"));
        this.inventory.set(0, stack);

        this.lastConsumedItem = new ItemStack(Registry.ITEM.get(new Identifier(tag.getString("lastItem"))));
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return toTag(tag);
    }

    public void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

}
