package net.zelythia.aequitas.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.ImplementedInventory;
import org.jetbrains.annotations.Nullable;

public class SamplingPedestalBlockEntity extends BlockEntity implements ImplementedInventory {


    public static final int TRANSFER_RATE = 32; //Max Essence transferred per tick
    public static final int CONSUMPTION_DELAY = 15;

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    private long storedEssence = 0;
    private Item currentlyConsuming = Items.AIR;
    private int consumptionDelay = 0;

    public SamplingPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.SAMPLING_PEDESTAL_BLOCK_ENTITY, pos, state);
    }

    /**
     * Decrements the stored essence and consumes item if necessary
     *
     * @return Essence that was transferred
     */
    public long transferEssence() {
        if(!world.getBlockState(pos.add(0,1,0)).isAir()) return 0;


        if (storedEssence == 0) {
            //Need to consume new item:
            if (consumptionDelay == 0) {
                long v = EssenceHandler.getEssenceValue(this.getStack(0).getItem());
                if (v > 0) {
                    this.storedEssence += v;
                    currentlyConsuming = this.getStack(0).getItem();
                    consumptionDelay = CONSUMPTION_DELAY;
                    this.getStack(0).decrement(1);
                    this.markDirty();
                }
            } else {
                return 0;
            }
        }

        if (storedEssence >= TRANSFER_RATE) {
            storedEssence -= TRANSFER_RATE;
            if (this.getStack(0).isEmpty()) {
                currentlyConsuming = Items.AIR;
            }
            return TRANSFER_RATE;
        }

        long temp = storedEssence;
        storedEssence = 0;
        if (this.getStack(0).isEmpty()) {
            currentlyConsuming = Items.AIR;
        }
        return temp;
    }

    public boolean hasEssence() {
        return !this.getStack(0).isEmpty() || storedEssence > 0;
    }

    //Serverside
    public Item getCurrentlyConsuming() {
        if (world.isClient) return currentlyConsuming;

        if (storedEssence == 0) return inventory.get(0).getItem();
        return currentlyConsuming == null ? Items.AIR : currentlyConsuming;
    }

    public static void tick(World world, BlockPos pos, BlockState state, SamplingPedestalBlockEntity be) {
        if (!(world instanceof ServerWorld)) return;

        if (be.consumptionDelay > 0) be.consumptionDelay--;
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
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        tag.putLong("Essence", storedEssence);
        tag.putString("displayItem", Registries.ITEM.getId(getCurrentlyConsuming()).toString());

        NbtList listTag = new NbtList();
        NbtCompound compoundTag = new NbtCompound();
        Identifier identifier = Registries.ITEM.getId(this.inventory.get(0).getItem());
        compoundTag.putString("id", identifier.toString());
        compoundTag.putInt("Count", this.inventory.get(0).getCount());
        listTag.add(compoundTag);
        tag.put("Items", listTag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        this.storedEssence = tag.getLong("Essence");
        if (tag.contains("displayItem"))
            this.currentlyConsuming = Registries.ITEM.get(new Identifier(tag.getString("displayItem")));

        NbtList listTag = tag.getList("Items", 10);
        NbtCompound compoundTag = listTag.getCompound(0);
        ItemStack stack = new ItemStack((Item) Registries.ITEM.get(new Identifier(compoundTag.getString("id"))));
        stack.setCount(compoundTag.getInt("Count"));
        this.inventory.set(0, stack);
    }


    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null) world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }

}
