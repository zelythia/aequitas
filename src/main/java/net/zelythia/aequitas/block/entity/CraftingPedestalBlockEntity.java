package net.zelythia.aequitas.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.zelythia.aequitas.ImplementedInventory;
import net.zelythia.aequitas.PortablePedestalInventory;
import net.zelythia.aequitas.advancement.PlayerStatistics;
import net.zelythia.aequitas.essence.EssenceHandler;
import net.zelythia.aequitas.item.AequitasItems;
import net.zelythia.aequitas.networking.NetworkingHandler;
import net.zelythia.aequitas.screen.CraftingPedestalScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CraftingPedestalBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory, SidedInventory {

    // 0 = Sampling slot
    // 1 = Output slot
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private long storedEssence;

    private final List<SamplingPedestalBlockEntity> samplingPedestals = new ArrayList<>();
    private static final int DETECTION_RADIUS = 3;
    private static final int MAX_SAMPLING_PEDESTALS = 800;

    private static ServerPlayerEntity player = null;

    public CraftingPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.CRAFTING_PEDESTAL_BLOCK_ENTITY, pos, state);
    }

    public static final int CRAFTING_DELAY = 10;
    private int craftingDelay = 0;


    @Override
    public Text getDisplayName() {
        return Text.translatable("block.aequitas.crafting_pedestal");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (player instanceof ServerPlayerEntity player1)
            CraftingPedestalBlockEntity.player = player1; //This should always be a ServerPlayerEntity
        return new CraftingPedestalScreenHandler(syncId, inv, this);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        Inventories.writeNbt(tag, inventory);
        tag.putLong("Essence", storedEssence);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        this.inventory.clear();
        Inventories.readNbt(tag, this.inventory);
        this.storedEssence = tag.getLong("Essence");
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


    public static void tick(World world, BlockPos pos, BlockState state, CraftingPedestalBlockEntity be){
        if (!(world instanceof ServerWorld)) return;


        //Don't do anything if there's no item or the pedestal is blocked
        if (be.getStack(0).isEmpty() || !world.getBlockState(pos.add(0,1,0)).isAir()) return;

        //Don't do anything when the output slot os already full
        if (!be.getStack(1).isEmpty()) {
            if (!be.getStack(1).isStackable()) return;
            if (be.getStack(1).getCount() >= be.getStack(1).getMaxCount()) return;
        }


        //Getting all Sampling-Pedestals in range starting at r = 2, prioritizing closer ones
        be.samplingPedestals.clear();
        collecting:
        for (int r = 2; r <= DETECTION_RADIUS; r++) {
            int x = -r;
            while (x <= 2 * r) {
                for (int z = -r; z <= r; z++) {
                    BlockEntity be1 = world.getBlockEntity(pos.add(x, 0, z));
                    if (be1 instanceof SamplingPedestalBlockEntity) {
                        be.samplingPedestals.add((SamplingPedestalBlockEntity) be1);
                    }
                    if (be.samplingPedestals.size() >= MAX_SAMPLING_PEDESTALS) break collecting;

                    BlockEntity be2 = world.getBlockEntity(pos.add(z, 0, x));
                    if (be2 instanceof SamplingPedestalBlockEntity) {
                        be.samplingPedestals.add((SamplingPedestalBlockEntity) be2);
                    }
                    if (be.samplingPedestals.size() >= MAX_SAMPLING_PEDESTALS) break collecting;
                }

                x += 2 * r;
            }
        }


        long required_value = EssenceHandler.getEssenceValue(be.getStack(0));
        if (be.getStack(0).getItem() == AequitasItems.PORTABLE_PEDESTAL) required_value = 1;

        if (required_value > 0) {

            if (be.craftingDelay == 0 && be.storedEssence < required_value) {
                for (SamplingPedestalBlockEntity samplingPedestal : be.samplingPedestals) {

                    if (samplingPedestal.hasEssence()) {
                        NetworkingHandler.sendParticle(be, samplingPedestal.getPos(), be.getPos(), new ItemStack(samplingPedestal.getCurrentlyConsuming()));
                    }

                    long value = samplingPedestal.transferEssence();
                    if (value > 0) {
                        if (be.getStack(0).getItem() == AequitasItems.PORTABLE_PEDESTAL) {
                            PortablePedestalInventory portablePedestalInventory = new PortablePedestalInventory(be.getStack(0));
                            portablePedestalInventory.storedEssence += value;
                            portablePedestalInventory.essenceToTag();
                        } else {
                            be.storedEssence += value;
                        }
                    }
                }
            } else {
                be.craftingDelay--;
            }


            if (be.storedEssence >= required_value) {

                if (be.getStack(1).isEmpty()) {
                    be.setStack(1, be.getStack(0).copy());
                } else if (be.getStack(1).getItem() == be.getStack(0).getItem()) {
                    be.getStack(1).increment(1);
                }

                if (be.getStack(1).getItem() == be.getStack(0).getItem()) {
                    //Trigger advancement for nearby players
                    for (PlayerEntity player : world.getPlayers()) {
                        if (Box.from(pos.toCenterPos()).expand(10).contains(player.getX(), player.getY(), player.getZ())) {
                            PlayerStatistics.ITEM_DUPLICATED_CRITERION.trigger((ServerPlayerEntity) player, be.getStack(0).copy());
                        }
                    }
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 1f);

                    be.storedEssence -= required_value;
                    be.craftingDelay = CRAFTING_DELAY;

                    be.markDirty();
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
