package net.zelythia.aequitas.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.zelythia.aequitas.Aequitas;

public class CraftingPedestalScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    //Called from client, inventory is synced later
    public CraftingPedestalScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(2));
    }

    //This constructor gets directly called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    //and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public CraftingPedestalScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(Aequitas.CRAFTING_PEDESTAL_SCREEN_HANDLER, syncId);
        checkSize(inventory, 2);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        int m;
        int l;

        //Adding the slots

        //Sampling slot
        this.addSlot(new Slot(inventory, 0, 124, 36){
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });

        //Output slot
        this.addSlot(new OutputSlot(inventory, 1, 80, 36));

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
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            //Block to inventory
            if (index < this.inventory.size()) {
                if (!this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            //Inventory to Block
            } else {
                if(!this.slots.get(0).hasStack()){
                    ItemStack s = itemStack2.copy();
                    s.setCount(1);
                    this.slots.get(0).setStack(s);

                    itemStack2.decrement(1);
                    slot.markDirty();
                }
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return itemStack;
    }
}
