package net.zelythia.aequitas.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.zelythia.aequitas.Aequitas;

public class CollectionBowlScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    public int inventorySize;

    //Called from client, inventory is synced later
    public CollectionBowlScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(buf.readInt()));
    }

    //This constructor gets directly called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    //and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public CollectionBowlScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(Aequitas.COLLECTION_BOWL_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.inventorySize = inventory.size();
        inventory.onOpen(playerInventory.player);

        int m;
        int l;

        //Adding the slots

        if(inventorySize == 1){
            this.addSlot(new OutputSlot(inventory, 0, 80, 35));
        }
        else if(inventorySize == 9){
            for(m = 0; m < 3; ++m) {
                for(l = 0; l < 3; ++l) {
                    this.addSlot(new OutputSlot(inventory, l + m * 3, 62 + l * 18, 17 + m * 18));
                }
            }
        }
        else{
            for(m = 0; m < 3; ++m) {
                for(l = 0; l < 5; ++l) {
                    this.addSlot(new OutputSlot(inventory, l + m * 5, 44 + l * 18, 17 + m * 18));
                }
            }
        }


        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    public int getSize(){
        return inventorySize;
    }
}
