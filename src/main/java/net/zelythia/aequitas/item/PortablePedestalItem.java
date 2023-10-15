package net.zelythia.aequitas.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.zelythia.aequitas.PortablePedestalInventory;
import net.zelythia.aequitas.screen.PortablePedestalScreenHandler;
import org.jetbrains.annotations.Nullable;

public class PortablePedestalItem extends Item implements NamedScreenHandlerFactory {

    public PortablePedestalItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if(hand == Hand.MAIN_HAND){
            player.openHandledScreen(this);
        }

        return super.use(world, player, hand);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("item.aequitas.portable_pedestal");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new PortablePedestalScreenHandler(syncId, inv, new PortablePedestalInventory(player.getStackInHand(Hand.MAIN_HAND)));
    }
}
