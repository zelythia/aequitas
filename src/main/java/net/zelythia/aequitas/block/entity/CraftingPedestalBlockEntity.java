package net.zelythia.aequitas.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.ImplementedInventory;
import net.zelythia.aequitas.networking.NetworkingHandler;
import net.zelythia.aequitas.screen.CraftingPedestalScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CraftingPedestalBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory, Tickable {

    // 0 = Sampling slot
    // 1 = Output slot
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    private final List<SamplingPedestalBlockEntity> samplingPedestals = new ArrayList<>();

    private static final int detectionRadius = 3;
    private static final int maxSamplingPedestals = 8;


    private int craft_delay;

    private long stored_essence;


    public CraftingPedestalBlockEntity() {
        super(Aequitas.CRAFTING_PEDESTAL_BLOCK_ENTITY);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("block.craftingPedestal");
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
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.inventory);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        Inventories.fromTag(tag, this.inventory);
    }

    @Override
    public void tick() {

        if(!(world instanceof ServerWorld)) return;


        if(this.getStack(0).isEmpty()) return;

        if(!this.getStack(1).isEmpty()){
            if(!this.getStack(1).isStackable()) return;
            if(this.getStack(1).getCount() >= this.getStack(1).getMaxCount()) return;
        }



        samplingPedestals.clear();
        collecting:
        for(int r = 2; r<= detectionRadius; r++){
            int x = -r;
            while(x<=2*r){
                for(int z = -r; z <=r; z++){
                    BlockEntity be = world.getBlockEntity(pos.add(x, 0, z));
                    if(be instanceof SamplingPedestalBlockEntity){
                        if(!((SamplingPedestalBlockEntity) be).getStack(0).isEmpty()) this.samplingPedestals.add((SamplingPedestalBlockEntity) be);
                    }
                    if(samplingPedestals.size()==maxSamplingPedestals) break collecting;

                    BlockEntity be2 = world.getBlockEntity(pos.add(z, 0, x));
                    if(be2 instanceof SamplingPedestalBlockEntity){
                        if(!((SamplingPedestalBlockEntity) be2).getStack(0).isEmpty()) this.samplingPedestals.add((SamplingPedestalBlockEntity) be2);
                    }
                    if(samplingPedestals.size()==maxSamplingPedestals) break collecting;
                }

                x+=2*r;
            }
        }


        long required_value = EssenceHandler.getEssenceValue(this.getStack(0).getItem());

        if(required_value > 0){

            if(craft_delay == 0){

                for(SamplingPedestalBlockEntity samplingPedestal: samplingPedestals){

                    if(!samplingPedestal.getStack(0).isEmpty()){

                        samplingPedestal.addTransferableValue(100);

                        long value = samplingPedestal.consumeItem();
                        if(value != -1){
                            NetworkingHandler.sendParticle(this, samplingPedestal.getPos(), this.getPos(), samplingPedestal.getStack(0));
                            this.stored_essence += value;
                        }


                    }
                }

            }
            else{
                craft_delay--;
            }


            if(this.stored_essence >= required_value){
                if(this.getStack(1).isEmpty()){
                    this.setStack(1, this.getStack(0).copy());
                }
                else{
                    this.getStack(1).increment(1);
                }

                this.stored_essence -= required_value;
                craft_delay = 10;
            }

        }

    }
}
