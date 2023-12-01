package net.zelythia.aequitas.block.entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.ImplementedInventory;
import net.zelythia.aequitas.PortablePedestalInventory;
import net.zelythia.aequitas.networking.NetworkingHandler;
import net.zelythia.aequitas.screen.CraftingPedestalScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CraftingPedestalBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory, SidedInventory, Tickable, BlockEntityClientSerializable {

    // 0 = Sampling slot
    // 1 = Output slot
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private long storedEssence;

    private final List<SamplingPedestalBlockEntity> samplingPedestals = new ArrayList<>();
    private static final int detectionRadius = 3;
    private static final int maxSamplingPedestals = 800;

//    private int craftingDelay = 0;


    public CraftingPedestalBlockEntity() {
        super(Aequitas.CRAFTING_PEDESTAL_BLOCK_ENTITY);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.aequitas.crafting_pedestal");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CraftingPedestalScreenHandler(syncId, inv, this);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        Inventories.writeNbt(tag, inventory);
        tag.putLong("Essence", storedEssence);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, NbtCompound tag) {
        super.fromTag(state, tag);

        this.inventory.clear();
        Inventories.readNbt(tag, this.inventory);
        this.storedEssence = tag.getLong("Essence");
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        this.inventory.clear();
        Inventories.readNbt(tag, this.inventory);
        this.storedEssence = tag.getLong("Essence");
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeNbt(tag);
    }


    public long getStoredEssence() {
        return storedEssence;
    }

    public Item getTargetItem() {
        return this.getStack(0).getItem();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null) world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
    }

    @Override
    public void tick() {
        if (!(world instanceof ServerWorld)) return;


        //Don't do anything if there's no item or the pedestal is blocked
        if (this.getStack(0).isEmpty() || !world.getBlockState(pos.add(0,1,0)).isAir()) return;

        //Don't do anything when the output slot os already full
        if (!this.getStack(1).isEmpty()) {
            if (!this.getStack(1).isStackable()) return;
            if (this.getStack(1).getCount() >= this.getStack(1).getMaxCount()) return;
        }


        samplingPedestals.clear();
        collecting:
        for (int r = 2; r <= detectionRadius; r++) {
            int x = -r;
            while (x <= 2 * r) {
                for (int z = -r; z <= r; z++) {
                    BlockEntity be = world.getBlockEntity(pos.add(x, 0, z));
                    if (be instanceof SamplingPedestalBlockEntity) {
                        this.samplingPedestals.add((SamplingPedestalBlockEntity) be);
                    }
                    if (samplingPedestals.size() == maxSamplingPedestals) break collecting;

                    BlockEntity be2 = world.getBlockEntity(pos.add(z, 0, x));
                    if (be2 instanceof SamplingPedestalBlockEntity) {
                        this.samplingPedestals.add((SamplingPedestalBlockEntity) be2);
                    }
                    if (samplingPedestals.size() == maxSamplingPedestals) break collecting;
                }

                x += 2 * r;
            }
        }


        long required_value = EssenceHandler.getEssenceValue(this.getStack(0));
        if (this.getStack(0).getItem() == Aequitas.PORTABLE_PEDESTAL_ITEM) required_value = 1;

        if (required_value > 0) {

            //if(craftingDelay == 0 && this.storedEssence < required_value)
            if (this.storedEssence < required_value) {

                for (SamplingPedestalBlockEntity samplingPedestal : samplingPedestals) {

                    long value = samplingPedestal.transferEssence();
                    if (value > 0) {
                        if (this.getStack(0).getItem() == Aequitas.PORTABLE_PEDESTAL_ITEM) {
                            PortablePedestalInventory portablePedestalInventory = new PortablePedestalInventory(this.getStack(0));
                            portablePedestalInventory.storedEssence += value;
                            portablePedestalInventory.essenceToTag();
                        } else {
                            this.storedEssence += value;
                        }

                        NetworkingHandler.sendParticle(this, samplingPedestal.getPos(), this.getPos(), new ItemStack(samplingPedestal.getDisplayItem()));
                    }

                }

            } else {
//                craftingDelay--;
            }


            if (this.storedEssence >= required_value) {

                if (this.getStack(1).isEmpty()) {
                    this.setStack(1, this.getStack(0).copy());
                } else if (this.getStack(1).getItem() == this.getStack(0).getItem()) {
                    this.getStack(1).increment(1);
                }

                if (this.getStack(1).getItem() == this.getStack(0).getItem()) {
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 1f);
                    this.storedEssence -= required_value;
//                    craftingDelay = 10;

                    this.markDirty();
                }
            }

        }

    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0, 1};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0 && inventory.get(0).getCount() == 0;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 1;
    }
}
