package net.zelythia.aequitas.block.entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.ImplementedInventory;

public class SamplingPedestalBlockEntity extends BlockEntity implements ImplementedInventory, BlockEntityClientSerializable {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private long storedEssence = 0;
    private Item displayItem;

    public SamplingPedestalBlockEntity() {
        super(Aequitas.SAMPLING_PEDESTAL_BLOCK_ENTITY);
    }

    public long transferEssence() {

        if (storedEssence == 0) {
            //Need to consume new item:
            long v = EssenceHandler.getEssenceValue(this.getStack(0).getItem());
            if (v > 0) {
                this.storedEssence += v;
                displayItem = this.getStack(0).getItem();
                this.getStack(0).decrement(1);
            }
            this.updateListeners();
        }

        if (storedEssence >= 100) {
            storedEssence -= 100;
            return 100;
        }

        long temp = storedEssence;
        storedEssence = 0;
        return temp;
    }

    //Serverside
    public Item getDisplayItem() {
        if (world.isClient) return displayItem;

        if (storedEssence == 0) return inventory.get(0).getItem();
        return displayItem == null ? Items.AIR : displayItem;
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
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        tag.putLong("Essence", storedEssence);
        tag.putString("displayItem", Registry.ITEM.getId(getDisplayItem()).toString());

        NbtList listTag = new NbtList();
        NbtCompound compoundTag = new NbtCompound();
        Identifier identifier = Registry.ITEM.getId(this.inventory.get(0).getItem());
        compoundTag.putString("id", identifier.toString());
        compoundTag.putInt("Count", this.inventory.get(0).getCount());
        listTag.add(compoundTag);
        tag.put("Items", listTag);


        return tag;
    }

    @Override
    public void fromTag(BlockState state, NbtCompound tag) {
        super.fromTag(state, tag);

        this.storedEssence = tag.getLong("Essence");
        if (tag.contains("displayItem"))
            this.displayItem = Registry.ITEM.get(new Identifier(tag.getString("displayItem")));

        NbtList listTag = tag.getList("Items", 10);
        NbtCompound compoundTag = listTag.getCompound(0);
        ItemStack stack = new ItemStack((Item) Registry.ITEM.get(new Identifier(compoundTag.getString("id"))));
        stack.setCount(compoundTag.getInt("Count"));
        this.inventory.set(0, stack);
    }


    @Override
    public void fromClientTag(NbtCompound tag) {
        this.inventory.clear();

        this.storedEssence = tag.getLong("Essence");
        if (tag.contains("displayItem"))
            this.displayItem = Registry.ITEM.get(new Identifier(tag.getString("displayItem")));

        NbtList listTag = tag.getList("Items", 10);
        NbtCompound compoundTag = listTag.getCompound(0);
        ItemStack stack = new ItemStack((Item) Registry.ITEM.get(new Identifier(compoundTag.getString("id"))));
        stack.setCount(compoundTag.getInt("Count"));
        this.inventory.set(0, stack);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeNbt(tag);
    }

    public void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

}
