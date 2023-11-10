package net.zelythia.aequitas.block.entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

    private float transferable_value = 0;

    public SamplingPedestalBlockEntity() {
        super(Aequitas.SAMPLING_PEDESTAL_BLOCK_ENTITY);
    }

    public long consumeItem(){
        long v = EssenceHandler.getEssenceValue(this.getStack(0));
        if(v > 0){
            if(this.transferable_value >= v){
                this.transferable_value = 0;
                this.getStack(0).decrement(1);
                this.updateListeners();

                return v;
            }
            return 0;
        }

        this.transferable_value = 0;
        return -1;
    }

    public void addTransferableValue(long v){
        this.transferable_value += v;
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

        NbtList listTag = tag.getList("Items", 10);
        NbtCompound compoundTag = listTag.getCompound(0);
        ItemStack stack = new ItemStack((Item)Registry.ITEM.get(new Identifier(compoundTag.getString("id"))));
        stack.setCount(compoundTag.getInt("Count"));
        this.inventory.set(0, stack);
    }


    @Override
    public void fromClientTag(NbtCompound tag) {
        this.inventory.clear();

        NbtList listTag = tag.getList("Items", 10);
        NbtCompound compoundTag = listTag.getCompound(0);
        ItemStack stack = new ItemStack((Item)Registry.ITEM.get(new Identifier(compoundTag.getString("id"))));
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
