package net.zelythia.aequitas.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.ImplementedInventory;
import net.zelythia.aequitas.Sounds;
import net.zelythia.aequitas.advancement.PlayerStatistics;
import net.zelythia.aequitas.block.AequitasBlocks;
import net.zelythia.aequitas.client.config.AequitasConfig;
import net.zelythia.aequitas.networking.NetworkingHandler;
import net.zelythia.aequitas.screen.CollectionBowlScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CollectionBowlBlockEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory {

    private final DefaultedList<ItemStack> inventory;

    public final int tier;
    private final List<BlockPos> infused = new ArrayList<>();
    private final List<BlockPos> chiseled = new ArrayList<>();
    private final List<BlockPos> pillar = new ArrayList<>();
    private final List<BlockPos> catalystBlocks1 = new ArrayList<>();
    private final List<BlockPos> catalystBlocks2 = new ArrayList<>();
    private final List<BlockPos> catalystBlocks3 = new ArrayList<>();

    //How long the bowl has been collecting essence for
    private int collectionTime;
    //The time required to generate an item
    private int collectionTimeTotal;

    //Used for rendering on the client
    private float collectionProgress;
    private Sounds.CollectionBowlSoundInstance s;

    public CollectionBowlBlockEntity(BlockPos pos, BlockState state, int inventorySize) {
        super(inventorySize == 15 ? BlockEntityTypes.COLLECTION_BOWL_BLOCK_ENTITY_III : inventorySize == 9 ? BlockEntityTypes.COLLECTION_BOWL_BLOCK_ENTITY_II : BlockEntityTypes.COLLECTION_BOWL_BLOCK_ENTITY_I, pos, state);
        this.tier = inventorySize == 15 ? 3 : inventorySize == 9 ? 2 : 1;
        this.inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        setCollectionTimeTotal();
    }

    private boolean structureBlockProperties = false;

    public static void tick(World world, BlockPos pos, BlockState state, CollectionBowlBlockEntity be){
        if (world == null) return;

        if (world.isClient) {
            be.playSound();
            return;
        }

        //Server logic

        if (be.getEmptySlot() != -1 && be.checkStructure()) {
            for (PlayerEntity player : world.getPlayers()) {
                if (Box.from(pos.toCenterPos()).expand(10).contains(player.getX(), player.getY(), player.getZ())) {
                    PlayerStatistics.COLLECTION_BOWL_CONSTRUCTED_CRITERION.trigger((ServerPlayerEntity) player, be.tier);
                }
            }


            be.setStructureBlockProperties(true);
            ++be.collectionTime;

            if (be.collectionTime >= be.collectionTimeTotal) {
                be.collectionTime = 0;
                be.setCollectionTimeTotal();

                LootContextType lootContextType = new LootContextType.Builder().require(LootContextParameters.ORIGIN).build();
                LootContextParameterSet lootContext = new LootContextParameterSet.Builder((ServerWorld) world).add(LootContextParameters.ORIGIN, new Vec3d(pos.getX(), pos.getY(), pos.getZ())).build(lootContextType);


                List<ItemStack> items = be.world.getServer().getLootManager().getLootTable(new Identifier("aequitas", "gameplay/biomes")).generateLoot(lootContext);

                if (!items.isEmpty()) {
                    ItemStack item = items.get(world.random.nextInt(items.size()));

                    if (be.inventory.size() == 9 && Math.random() * 100 < 30) {
                        item.setCount(Math.min(item.getMaxCount(), item.getCount() * 2));
                    } else if (be.inventory.size() == 15 && Math.random() * 100 < 50) {
                        if (Math.random() * 100 < 50) {
                            item.setCount(Math.min(item.getMaxCount(), item.getCount() * 3));
                        } else {
                            item.setCount(Math.min(item.getMaxCount(), item.getCount() * 2));
                        }
                    }

                    be.insertStack(item);
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 1f);
                } else {
                    Aequitas.LOGGER.error("Broken Loot table for biome {}", world.getBiome(pos));
                }
            }
        } else {
            be.setStructureBlockProperties(false);
        }

        NetworkingHandler.updateCollectionBowl(be);
    }


    public void updateStructurePositions() {
        infused.clear();
        int r = 3;

        if (tier == 1) {
            //Diagonals
            infused.add(pos.add(2, -1, 2));
            infused.add(pos.add(2, -1, -2));
            infused.add(pos.add(-2, -1, 2));
            infused.add(pos.add(-2, -1, -2));
            //T
            infused.add(pos.add(3, -1, 1));
            infused.add(pos.add(3, -1, -1));
            infused.add(pos.add(-3, -1, 1));
            infused.add(pos.add(-3, -1, -1));
            infused.add(pos.add(1, -1, 3));
            infused.add(pos.add(-1, -1, 3));
            infused.add(pos.add(1, -1, -3));
            infused.add(pos.add(-1, -1, -3));

            catalystBlocks1.clear();
            catalystBlocks1.add(pos.add(3, 1, 0));
            catalystBlocks1.add(pos.add(-3, 1, 0));
            catalystBlocks1.add(pos.add(0, 1, 3));
            catalystBlocks1.add(pos.add(0, 1, -3));
            catalystBlocks1.add(pos.up(3));
        } else if (tier == 2) {
            catalystBlocks1.clear();
            catalystBlocks2.clear();
            r = 4;

            //T
            infused.add(pos.add(4, -1, 1));
            infused.add(pos.add(4, -1, 2));
            infused.add(pos.add(4, -1, -1));
            infused.add(pos.add(4, -1, -2));
            infused.add(pos.add(-4, -1, 1));
            infused.add(pos.add(-4, -1, 2));
            infused.add(pos.add(-4, -1, -1));
            infused.add(pos.add(-4, -1, -2));

            infused.add(pos.add(1, -1, 4));
            infused.add(pos.add(2, -1, 4));
            infused.add(pos.add(-1, -1, 4));
            infused.add(pos.add(-2, -1, 4));
            infused.add(pos.add(1, -1, -4));
            infused.add(pos.add(2, -1, -4));
            infused.add(pos.add(-1, -1, -4));
            infused.add(pos.add(-2, -1, -4));
            //Diagonals
            infused.add(pos.add(3, -1, 3));
            infused.add(pos.add(3, -1, -3));
            infused.add(pos.add(-3, -1, 3));
            infused.add(pos.add(-3, -1, -3));

            infused.add(pos.add(3, -1, 2));
            infused.add(pos.add(3, -1, -2));
            infused.add(pos.add(-3, -1, 2));
            infused.add(pos.add(-3, -1, -2));
            infused.add(pos.add(2, -1, 3));
            infused.add(pos.add(-2, -1, 3));
            infused.add(pos.add(2, -1, -3));
            infused.add(pos.add(-2, -1, -3));

            //Pillar 2
            pillar.add(pos.add(3, 0, 3));
            pillar.add(pos.add(3, 1, 3));

            pillar.add(pos.add(3, 0, -3));
            pillar.add(pos.add(3, 1, -3));

            pillar.add(pos.add(-3, 0, 3));
            pillar.add(pos.add(-3, 1, 3));

            pillar.add(pos.add(-3, 0, -3));
            pillar.add(pos.add(-3, 1, -3));

            //Catalysts
            catalystBlocks1.add(pos.add(4, 1, 0));
            catalystBlocks1.add(pos.add(-4, 1, 0));
            catalystBlocks1.add(pos.add(0, 1, 4));
            catalystBlocks1.add(pos.add(0, 1, -4));

            catalystBlocks2.add(pos.add(3, 2, 3));
            catalystBlocks2.add(pos.add(3, 2, -3));
            catalystBlocks2.add(pos.add(-3, 2, 3));
            catalystBlocks2.add(pos.add(-3, 2, -3));

            catalystBlocks2.add(pos.up(3));
        } else if (tier == 3) {
            catalystBlocks1.clear();
            catalystBlocks2.clear();
            catalystBlocks3.clear();
            r = 6;


            //Bigger circle
            infused.add(pos.add(2, -1, 1));
            infused.add(pos.add(2, -1, -1));
            infused.add(pos.add(-2, -1, 1));
            infused.add(pos.add(-2, -1, -1));
            infused.add(pos.add(1, -1, 2));
            infused.add(pos.add(-1, -1, 2));
            infused.add(pos.add(1, -1, -2));
            infused.add(pos.add(-1, -1, -2));


            //T
            infused.add(pos.add(6, -1, 1));
            infused.add(pos.add(6, -1, -1));
            infused.add(pos.add(-6, -1, 1));
            infused.add(pos.add(-6, -1, -1));
            infused.add(pos.add(1, -1, 6));
            infused.add(pos.add(-1, -1, 6));
            infused.add(pos.add(1, -1, -6));
            infused.add(pos.add(-1, -1, -6));

            //Diagonals
            infused.add(pos.add(4, -1, 4));
            infused.add(pos.add(4, -1, -4));
            infused.add(pos.add(-4, -1, 4));
            infused.add(pos.add(-4, -1, -4));
            //+1
            infused.add(pos.add(4, -1, 3));
            infused.add(pos.add(4, -1, -3));
            infused.add(pos.add(-4, -1, 3));
            infused.add(pos.add(-4, -1, -3));
            infused.add(pos.add(3, -1, 4));
            infused.add(pos.add(3, -1, -4));
            infused.add(pos.add(-3, -1, 4));
            infused.add(pos.add(-3, -1, -4));
            //+2
            infused.add(pos.add(5, -1, 2));
            infused.add(pos.add(5, -1, 3));
            infused.add(pos.add(5, -1, -2));
            infused.add(pos.add(5, -1, -3));
            infused.add(pos.add(-5, -1, 2));
            infused.add(pos.add(-5, -1, 3));
            infused.add(pos.add(-5, -1, -2));
            infused.add(pos.add(-5, -1, -3));
            infused.add(pos.add(2, -1, 5));
            infused.add(pos.add(3, -1, 5));
            infused.add(pos.add(-2, -1, 5));
            infused.add(pos.add(-3, -1, 5));
            infused.add(pos.add(2, -1, -5));
            infused.add(pos.add(3, -1, -5));
            infused.add(pos.add(-2, -1, -5));
            infused.add(pos.add(-3, -1, -5));


            //Pillar 2
            pillar.add(pos.add(5, 0, 2));
            pillar.add(pos.add(5, 1, 2));

            pillar.add(pos.add(5, 0, -2));
            pillar.add(pos.add(5, 1, -2));

            pillar.add(pos.add(-5, 0, 2));
            pillar.add(pos.add(-5, 1, 2));

            pillar.add(pos.add(-5, 0, -2));
            pillar.add(pos.add(-5, 1, -2));

            pillar.add(pos.add(2, 0, 5));
            pillar.add(pos.add(2, 1, 5));

            pillar.add(pos.add(-2, 0, 5));
            pillar.add(pos.add(-2, 1, 5));

            pillar.add(pos.add(2, 0, -5));
            pillar.add(pos.add(2, 1, -5));

            pillar.add(pos.add(-2, 0, -5));
            pillar.add(pos.add(-2, 1, -5));

            //Pillar 3
            pillar.add(pos.add(4, 0, 4));
            pillar.add(pos.add(4, 1, 4));
            pillar.add(pos.add(4, 2, 4));

            pillar.add(pos.add(4, 0, -4));
            pillar.add(pos.add(4, 1, -4));
            pillar.add(pos.add(4, 2, -4));

            pillar.add(pos.add(-4, 0, 4));
            pillar.add(pos.add(-4, 1, 4));
            pillar.add(pos.add(-4, 2, 4));

            pillar.add(pos.add(-4, 0, -4));
            pillar.add(pos.add(-4, 1, -4));
            pillar.add(pos.add(-4, 2, -4));

            //Catalysts
            catalystBlocks1.add(pos.add(6, 1, 0));
            catalystBlocks1.add(pos.add(-6, 1, 0));
            catalystBlocks1.add(pos.add(0, 1, 6));
            catalystBlocks1.add(pos.add(0, 1, -6));

            catalystBlocks2.add(pos.add(5, 2, 2));
            catalystBlocks2.add(pos.add(5, 2, -2));
            catalystBlocks2.add(pos.add(-5, 2, 2));
            catalystBlocks2.add(pos.add(-5, 2, -2));

            catalystBlocks3.add(pos.add(4, 3, 4));
            catalystBlocks3.add(pos.add(4, 3, -4));
            catalystBlocks3.add(pos.add(-4, 3, 4));
            catalystBlocks3.add(pos.add(-4, 3, -4));

            catalystBlocks3.add(pos.up(3));
        }

        //Adding ground blocks
        //Middle Square
        infused.add(pos.down());
        infused.add(pos.add(1, -1, 1));
        infused.add(pos.add(1, -1, -1));
        infused.add(pos.add(-1, -1, 1));
        infused.add(pos.add(-1, -1, -1));

        //Ground lines
        for (int i = 1; i <= r; i++) {
            infused.add(pos.add(i, -1, 0));
            infused.add(pos.add(-i, -1, 0));
            infused.add(pos.add(0, -1, i));
            infused.add(pos.add(0, -1, -i));

            //Adding chiseled pillars
            if (i == r) {
                chiseled.add(pos.add(i, 0, 0));
                chiseled.add(pos.add(-i, 0, 0));
                chiseled.add(pos.add(0, 0, i));
                chiseled.add(pos.add(0, 0, -i));
            }
        }
    }

    public boolean checkStructure() {

        updateStructurePositions();

        for (BlockPos pos : infused) {
            if (!world.getBlockState(pos).isIn(AequitasBlocks.INFUSED_BLOCKS)) return false;
        }
        for (BlockPos pos : chiseled) {
            if (!world.getBlockState(pos).getBlock().equals(AequitasBlocks.CHISELED_INFUSED_STONE)) return false;
        }
        for (BlockPos pos : pillar) {
            if (!world.getBlockState(pos).getBlock().equals(AequitasBlocks.INFUSED_STONE_PILLAR)) return false;
        }

        for (BlockPos pos : catalystBlocks1) {
            if (!world.getBlockState(pos).getBlock().equals(AequitasBlocks.CATALYST_I)) return false;
        }
        for (BlockPos pos : catalystBlocks2) {
            if (!world.getBlockState(pos).getBlock().equals(AequitasBlocks.CATALYST_II)) return false;
        }
        for (BlockPos pos : catalystBlocks3) {
            if (!world.getBlockState(pos).getBlock().equals(AequitasBlocks.CATALYST_III)) return false;
        }

        return true;
    }

    private void setStructureBlockProperties(boolean value) {
        if (!value && !structureBlockProperties) return;
        structureBlockProperties = value;

        List<BlockPos> activeBlocks = new ArrayList<>();
        activeBlocks.addAll(infused);
        activeBlocks.addAll(chiseled);
        activeBlocks.addAll(pillar);
        activeBlocks.addAll(catalystBlocks1);
        activeBlocks.addAll(catalystBlocks2);
        activeBlocks.addAll(catalystBlocks3);

        for (BlockPos pos : activeBlocks) {
            if (world.getBlockState(pos).getOrEmpty(AequitasBlocks.ACTIVE_BLOCK_PROPERTY).isPresent()) {
                world.setBlockState(pos, world.getBlockState(pos).with(AequitasBlocks.ACTIVE_BLOCK_PROPERTY, value));
            }
        }
    }

    private void setCollectionTimeTotal() {
        if (this.tier == 1) {
            this.collectionTimeTotal = (int) ((Math.random() * 200) + 1100);
        } else if (this.tier == 2) {
            this.collectionTimeTotal = (int) ((Math.random() * 200) + 500);
        } else {
            this.collectionTimeTotal = (int) ((Math.random() * 200) + 200);
        }
    }

    @Environment(EnvType.CLIENT)
    private void playSound() {
        if (AequitasConfig.config.getOrDefault("playAmbientSound", true) && world.getBlockState(pos.down()).getOrEmpty(AequitasBlocks.ACTIVE_BLOCK_PROPERTY).orElse(false)) {
            MinecraftClient client = MinecraftClient.getInstance();

            if (s == null || !client.getSoundManager().isPlaying(s)) {
                s = new Sounds.CollectionBowlSoundInstance(client.player, pos, tier == 1 ? 3 : tier == 2 ? 4 : 6, world.random);
                client.getSoundManager().play(s);
            }
        } else if (s != null) {
            s.setDone();
            s = null;
        }
    }


    //Screen functions

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.aequitas.collection_bowl_" + tier);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CollectionBowlScreenHandler(syncId, inv, this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(inventory.size());
    }


    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }


    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        Inventories.writeNbt(tag, this.inventory);
        tag.putInt("collection_time", collectionTime);
        tag.putInt("collection_time_total", collectionTimeTotal);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        Inventories.readNbt(tag, this.inventory);
        this.collectionTime = tag.getInt("collection_time");
        this.collectionTimeTotal = tag.getInt("collection_time_total");
        updateStructurePositions();
    }

    public float getServerCollectionProgress() {
        return (float) this.collectionTime / this.collectionTimeTotal;
    }

    public float getClientCollectionProgress() {
        return collectionProgress;
    }

    public void setClientCollectionProgress(float progress) {
        collectionProgress = progress;
    }


    @Override
    public void markDirty() {
        super.markDirty();
        updateStructurePositions();
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        setStructureBlockProperties(false);
        if (s != null) {
            s.setDone();
            s = null;
        }
    }


    //-----Inventory helper methods

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void insertStack(ItemStack stack) {
        int i;
        do {
            i = stack.getCount();
            stack.setCount(this.addStack(stack));
        } while (!stack.isEmpty() && stack.getCount() < i);
    }

    private int addStack(ItemStack stack) {
        int i = this.getOccupiedSlotWithRoomForStack(stack);
        if (i == -1) {
            i = this.getEmptySlot();
        }

        return i == -1 ? stack.getCount() : this.addStack(i, stack);
    }

    public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        for (int i = 0; i < this.inventory.size(); ++i) {
            if (this.canStackAddMore(this.inventory.get(i), stack)) {
                return i;
            }
        }

        return -1;
    }

    private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() && this.areItemsEqual(existingStack, stack) && existingStack.isStackable() && existingStack.getCount() < existingStack.getMaxCount() && existingStack.getCount() < this.getMaxCountPerStack();
    }

    private boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areEqual(stack1, stack2);
    }

    public int getEmptySlot() {
        for (int i = 0; i < this.inventory.size(); ++i) {
            if (this.inventory.get(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    private int addStack(int slot, ItemStack stack) {
        Item item = stack.getItem();
        int i = stack.getCount();
        ItemStack itemStack = this.getStack(slot);
        if (itemStack.isEmpty()) {
            itemStack = new ItemStack(item, 0);
            if (stack.hasNbt()) {
                itemStack.setNbt(stack.getNbt().copy());
            }

            this.setStack(slot, itemStack);
        }

        int j = Math.min(i, itemStack.getMaxCount() - itemStack.getCount());

        if (j > this.getMaxCountPerStack() - itemStack.getCount()) {
            j = this.getMaxCountPerStack() - itemStack.getCount();
        }

        if (j != 0) {
            i -= j;
            itemStack.increment(j);
        }
        return i;
    }

}
