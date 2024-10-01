package net.zelythia.aequitas.screen;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.zelythia.aequitas.Aequitas;
import net.zelythia.aequitas.EssenceHandler;
import net.zelythia.aequitas.PortablePedestalInventory;
import net.zelythia.aequitas.item.AequitasItems;

public class PortablePedestalScreenHandler extends ScreenHandler {
    public final PortablePedestalInventory inventory;
    private final PlayerInventory playerInventory;


    public PortablePedestalScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new PortablePedestalInventory(MinecraftClient.getInstance().player == null ? ItemStack.EMPTY : MinecraftClient.getInstance().player.getMainHandStack()));
    }

    //This constructor gets directly called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    //and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public PortablePedestalScreenHandler(int syncId, PlayerInventory playerInventory, PortablePedestalInventory inventory) {
        super(Aequitas.PORTABLE_PEDESTAL_SCREEN_HANDLER, syncId);
        inventory.onOpen(playerInventory.player);
        this.inventory = inventory;
        this.playerInventory = playerInventory;

        int m;
        int l;

        //Adding the slots


        //Input slot
        this.addSlot(new Slot(inventory, 0, 22, 28) {
            @Override
            public void setStack(ItemStack stack) {
                long e = EssenceHandler.getEssenceValue(stack);
                if (e > 0 && stack != PortablePedestalScreenHandler.this.inventory.item) {
                    super.setStack(stack);
                } else {
                    PortablePedestalScreenHandler.this.playerInventory.insertStack(stack);
                }
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                if (stack.getItem() == AequitasItems.PORTABLE_PEDESTAL && stack.hasNbt() && stack.getNbt().getType("unlocked") == NbtType.LIST) {
                    if (((NbtList) stack.getNbt().get("unlocked")).size() > 0) return false;
                }

                return EssenceHandler.getEssenceValue(stack) > 0 && !ItemStack.areEqual(stack, PortablePedestalScreenHandler.this.inventory.item);
            }
        });

        for (m = 0; m < 2; ++m) {
            for (l = 0; l < 5; l++) {
                this.addSlot(new OutputSlot(inventory, l + m * 5 + 1, 62 + l * 18, 32 + m * 18) {
                    @Override
                    public void onTakeItem(PlayerEntity player, ItemStack stack) {
                        if (this.getStack().isEmpty()) {
                            ItemStack newStack = stack.copy();
                            newStack.setCount(1);
                            this.setStack(newStack);
                        }
                        super.onTakeItem(player, stack);
                    }

                });
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


    public void updateSearchProperties(String filter, int page) {
        inventory.updateFilter(filter, page);
        this.sendContentUpdates();
    }


    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (player.getWorld().isClient) return;
        inventory.markDirty();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            //Block to inventory
            if (index < this.inventory.size()) {

                long e = EssenceHandler.getEssenceValue(originalStack);
                if (e * newStack.getMaxCount() <= inventory.storedEssence) {
                    inventory.storedEssence -= e * newStack.getMaxCount();
                    newStack.setCount(newStack.getMaxCount());
                    player.getInventory().insertStack(newStack);
                    return ItemStack.EMPTY;
                }

                int amount = (int) (inventory.storedEssence / e);
                inventory.storedEssence -= e * amount;
                newStack.setCount(amount);
                player.getInventory().insertStack(newStack);
                return ItemStack.EMPTY;

                //Inventory to Block
            } else if (!this.insertItem(originalStack, 0, 1, false)) {
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


    //Override is needed in insert essence check
    @Override
    public void onSlotClick(int i, int j, SlotActionType slotActionType, PlayerEntity playerEntity) {
        ItemStack itemStack = ItemStack.EMPTY;

        if (slotActionType != SlotActionType.QUICK_CRAFT && slotActionType != SlotActionType.QUICK_MOVE) {
            if (!(slotActionType != SlotActionType.PICKUP || j != 0 && j != 1) && i != -999) {
                if (i < 0) {
                    return;
                }
                Slot slot3 = this.slots.get(i);
                if (slot3 != null) {
                    ItemStack itemStack3 = slot3.getStack();
                    ItemStack itemStack2 = this.getCursorStack();
                    if (!itemStack3.isEmpty()) {
                        itemStack = itemStack3.copy();
                    }

                    if (!itemStack3.isEmpty() && slot3.canTakeItems(playerEntity)) {
                        int o;
                        if (!itemStack2.isEmpty() && !slot3.canInsert(itemStack2)) {
                            if (itemStack2.getMaxCount() > 1 && ItemStack.canCombine(itemStack3, itemStack2) && !itemStack3.isEmpty() && (o = itemStack3.getCount()) + itemStack2.getCount() <= itemStack2.getMaxCount()) {
                                long e = EssenceHandler.getEssenceValue(itemStack3);
                                if (e <= inventory.storedEssence) {
                                    itemStack2.increment(o);
                                    itemStack3 = slot3.takeStack(o);
                                    if (itemStack3.isEmpty()) {
                                        slot3.setStack(ItemStack.EMPTY);
                                    }
                                    slot3.onTakeItem(playerEntity, this.getCursorStack());

                                    slot3.markDirty();
                                }

                                return;
                            }
                        }
                    }
                }
            }
        }

        super.onSlotClick(i, j, slotActionType, playerEntity);
    }

}
