package net.zelythia.aequitas.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.ImplementedInventory;
import net.zelythia.aequitas.block.ConduitBlock;
import net.zelythia.aequitas.screen.CollectionBowlScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CollectionBowlBlockEntity extends BlockEntity implements ImplementedInventory, Tickable, ExtendedScreenHandlerFactory {

    private final DefaultedList<ItemStack> inventory;

    private final List<BlockPos> conduitBlocks = new ArrayList<>();
    private final List<BlockPos> catalystBlocks = new ArrayList<>();

    private int collectionTime;
    private int collectionTimeTotal;

    public CollectionBowlBlockEntity(int inventorySize) {
        super(inventorySize==15?Aequitas.COLLECTION_BOWL_BLOCK_ENTITY_III:inventorySize==9?Aequitas.COLLECTION_BOWL_BLOCK_ENTITY_II:Aequitas.COLLECTION_BOWL_BLOCK_ENTITY_I);
        this.inventory  = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        updateStructurePositions();
        setCollectionTimeTotal();
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.aequitas.collection_bowl");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CollectionBowlScreenHandler(syncId, inv, this);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.inventory);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        Inventories.fromTag(tag, this.inventory);
        updateStructurePositions();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeInt(inventory.size());
    }


    private void setCollectionTimeTotal(){
        if(this.inventory.size()==1){
            this.collectionTimeTotal = (int) ((Math.random() * 200) + 1100);
        }
        else if(this.inventory.size() == 9){
            this.collectionTimeTotal = (int) ((Math.random() * 200) + 500);
        }
        else{
            this.collectionTimeTotal = (int) ((Math.random() * 200) + 100);
        }
    }


    @Override
    public void tick() {
        if(world == null) return;
        if(world.isClient) return;


        if(getEmptySlot() != -1 && checkStructure()){

            if(collectionTime == 0){
                setBlockProperties(true);
            }
            ++this.collectionTime;


            if(this.collectionTime >= this.collectionTimeTotal){
                this.collectionTime = 0;
                setCollectionTimeTotal();

                LootContextType lootContextType = new LootContextType.Builder().require(LootContextParameters.ORIGIN).build();
                LootContext lootContext = new LootContext.Builder((ServerWorld) this.world).parameter(LootContextParameters.ORIGIN, new Vec3d(pos.getX(), pos.getY(), pos.getZ())).build(lootContextType);

                List<ItemStack> items = this.world.getServer().getLootManager().getTable(new Identifier("aequitas", "gameplay/biomes")).generateLoot(lootContext);

                if(items.size() > 0){
                    ItemStack item = items.get(world.random.nextInt(items.size()));

                    if(this.inventory.size() == 9 && Math.random()*100 < 30){
                        item.setCount(Math.min(item.getMaxCount(), item.getCount() * 2));
                    }
                    else if(this.inventory.size() == 15 && Math.random()*100 < 50){
                        if(Math.random()*100 < 50){
                            item.setCount(Math.min(item.getMaxCount(), item.getCount() * 3));
                        }
                        else{
                            item.setCount(Math.min(item.getMaxCount(), item.getCount() * 2));
                        }
                    }

                    this.insertStack(item);
                }
                else{
                    Aequitas.LOGGER.error("Broken Loot table for biome {}", this.world.getBiome(pos));
                }
            }
        }
        else{
            collectionTime = 0;
            setBlockProperties(false);
        }
    }


    public void updateStructurePositions(){
        conduitBlocks.clear();
        catalystBlocks.clear();

        int r = 3;

        if(inventory.size() == 1){
            conduitBlocks.add(pos.add(3,0,0));
            conduitBlocks.add(pos.add(-3,0,0));
            conduitBlocks.add(pos.add(0,0,3));
            conduitBlocks.add(pos.add(0,0,-3));

            catalystBlocks.add(pos.add(3,1,0));
            catalystBlocks.add(pos.add(-3,1,0));
            catalystBlocks.add(pos.add(0,1,3));
            catalystBlocks.add(pos.add(0,1,-3));
        }
        else if(inventory.size() == 9){
            r = 4;

            //Pillar 1
            conduitBlocks.add(pos.add(4,0,0));
            conduitBlocks.add(pos.add(-4,0,0));
            conduitBlocks.add(pos.add(0,0,4));
            conduitBlocks.add(pos.add(0,0,-4));

            //Pillar 2
            conduitBlocks.add(pos.add(3,0,3));
            conduitBlocks.add(pos.add(3,1,3));

            conduitBlocks.add(pos.add(3,0,-3));
            conduitBlocks.add(pos.add(3,1,-3));

            conduitBlocks.add(pos.add(-3,0,3));
            conduitBlocks.add(pos.add(-3,1,3));

            conduitBlocks.add(pos.add(-3,0,-3));
            conduitBlocks.add(pos.add(-3,1,-3));

            //Catalysts
            catalystBlocks.add(pos.add(4,1,0));
            catalystBlocks.add(pos.add(-4,1,0));
            catalystBlocks.add(pos.add(0,1,4));
            catalystBlocks.add(pos.add(0,1,-4));

            catalystBlocks.add(pos.add(3,2,3));
            catalystBlocks.add(pos.add(3,2,-3));
            catalystBlocks.add(pos.add(-3,2,3));
            catalystBlocks.add(pos.add(-3,2,-3));
        }
        else if(inventory.size() == 15){
            r = 6;

            //Pillar 1
            conduitBlocks.add(pos.add(6,0,0));
            conduitBlocks.add(pos.add(-6,0,0));
            conduitBlocks.add(pos.add(0,0,6));
            conduitBlocks.add(pos.add(0,0,-6));

            //Pillar 2
            conduitBlocks.add(pos.add(5,0,2));
            conduitBlocks.add(pos.add(5,1,2));

            conduitBlocks.add(pos.add(5,0,-2));
            conduitBlocks.add(pos.add(5,1,-2));

            conduitBlocks.add(pos.add(-5,0,2));
            conduitBlocks.add(pos.add(-5,1,2));

            conduitBlocks.add(pos.add(-5,0,-2));
            conduitBlocks.add(pos.add(-5,1,-2));

            //Pillar 3
            conduitBlocks.add(pos.add(4,0,4));
            conduitBlocks.add(pos.add(4,1,4));

            conduitBlocks.add(pos.add(4,0,-4));
            conduitBlocks.add(pos.add(4,1,-4));

            conduitBlocks.add(pos.add(-4,0,4));
            conduitBlocks.add(pos.add(-4,1,4));

            conduitBlocks.add(pos.add(-4,0,-4));
            conduitBlocks.add(pos.add(-4,1,-4));

            //Catalysts
            catalystBlocks.add(pos.add(6,1,0));
            catalystBlocks.add(pos.add(-6,1,0));
            catalystBlocks.add(pos.add(0,1,6));
            catalystBlocks.add(pos.add(0,1,-6));

            catalystBlocks.add(pos.add(5,2,2));
            catalystBlocks.add(pos.add(5,2,-2));
            catalystBlocks.add(pos.add(-5,2,2));
            catalystBlocks.add(pos.add(-5,2,-2));

            catalystBlocks.add(pos.add(4,3,4));
            catalystBlocks.add(pos.add(4,3,-4));
            catalystBlocks.add(pos.add(-4,3,4));
            catalystBlocks.add(pos.add(-4,3,-4));
        }

        catalystBlocks.add(pos.up(3));

        //Adding ground blocks
        conduitBlocks.add(pos.down());
        for(int i = 1; i<= r; i++){
            conduitBlocks.add(pos.add(i, -1, 0));
            conduitBlocks.add(pos.add(-i, -1, 0));
            conduitBlocks.add(pos.add(0, -1, i));
            conduitBlocks.add(pos.add(0, -1, -i));
        }
    }

    private void setBlockProperties(boolean value){
        for(BlockPos pos: conduitBlocks){
            if(world.getBlockState(pos).getBlock().equals(Aequitas.CONDUIT_BLOCK)){
                world.setBlockState(pos, world.getBlockState(pos).with(Aequitas.ACTIVE_BLOCK_PROPERTY, value));
            }
        }
        for(BlockPos pos: catalystBlocks){
            if(world.getBlockState(pos).getBlock().equals(Aequitas.CATALYST_BLOCK)){
                world.setBlockState(pos, world.getBlockState(pos).with(Aequitas.ACTIVE_BLOCK_PROPERTY, value));
            }
        }
    }

    @Override
    public void setPos(BlockPos pos) {
        super.setPos(pos);
        this.updateStructurePositions();
    }

    @Override
    public void setLocation(World world, BlockPos pos) {
        super.setLocation(world, pos);
        this.updateStructurePositions();
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        for(BlockPos pos: conduitBlocks){
            if(world.getBlockState(pos).getBlock().equals(Aequitas.CONDUIT_BLOCK)){
                world.setBlockState(pos, world.getBlockState(pos).with(Aequitas.ACTIVE_BLOCK_PROPERTY, false));
            }
        }
    }

    public boolean checkStructure(){

        for(BlockPos pos: conduitBlocks){
            if(!world.getBlockState(pos).getBlock().equals(Aequitas.CONDUIT_BLOCK)) return false;
        }
        for(BlockPos pos: catalystBlocks){
            if(!world.getBlockState(pos).getBlock().equals(Aequitas.CATALYST_BLOCK)) return false;
        }

        return true;
    }


    public void insertStack(ItemStack stack){
        int i;
        do {
            i = stack.getCount();
            stack.setCount(this.addStack(stack));
        } while(!stack.isEmpty() && stack.getCount() < i);
    }

    private int addStack(ItemStack stack) {
        int i = this.getOccupiedSlotWithRoomForStack(stack);
        if (i == -1) {
            i = this.getEmptySlot();
        }

        return i == -1 ? stack.getCount() : this.addStack(i, stack);
    }

    public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        for(int i = 0; i < this.inventory.size(); ++i) {
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
        return stack1.getItem() == stack2.getItem() && ItemStack.areTagsEqual(stack1, stack2);
    }

    public int getEmptySlot() {
        for(int i = 0; i < this.inventory.size(); ++i) {
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
            if (stack.hasTag()) {
                itemStack.setTag(stack.getTag().copy());
            }

            this.setStack(slot, itemStack);
        }

        int j = i;
        if (i > itemStack.getMaxCount() - itemStack.getCount()) {
            j = itemStack.getMaxCount() - itemStack.getCount();
        }

        if (j > this.getMaxCountPerStack() - itemStack.getCount()) {
            j = this.getMaxCountPerStack() - itemStack.getCount();
        }

        if (j != 0) {
            i -= j;
            itemStack.increment(j);
            itemStack.setCooldown(5);
        }
        return i;
    }

}
