package com.grim3212.assorted.storage.common.item.upgrades;

import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public abstract class ModeCrateUpgradeItem extends BasicCrateUpgradeItem {

    public ModeCrateUpgradeItem(Properties props) {
        super(props);
    }

    protected abstract void cycleMode(ItemStack stack);

    protected abstract Component modeDisplay(ItemStack stack);

    protected abstract int startingMode();

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stackInHand = player.getItemInHand(hand);
        if (stackInHand.getItem() == this) {
            this.cycleMode(stackInHand);
            this.sendMessage(player, modeDisplay(stackInHand));
            return InteractionResult.SUCCESS;
        }

        return super.use(level, player, hand);
    }

    protected void sendMessage(Player player, Component message) {
        if (!player.level().isClientSide()) {
            player.sendSystemMessage(message);
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, Player player) {
        NBTHelper.putInt(stack, "Mode", this.startingMode());
    }

    /**
     * {@code Item.appendHoverText} is marked deprecated in 26.x - tooltips are meant to come from
     * data components implementing {@code TooltipProvider} - but it is still the only per item
     * hook, and vanilla's own items (DiscFragmentItem, HangingEntityItem, SmithingTemplateItem)
     * still override it.
     * <p>
     * TODO(26.2): moving this text onto a component would mean giving the lock its own
     * DataComponentType instead of the CUSTOM_DATA tag AssortedLib's StorageUtil writes.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flagIn) {
        tooltip.accept(modeDisplay(stack));
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack current, ItemStack tryStack, Slot slot, ClickAction action, Player player, SlotAccess slotAccess) {
        // In the upgrade slot they are only allowed stack size 1
        if (action == ClickAction.SECONDARY && current.getCount() == 1) {
            if (current.getItem() == this) {
                this.cycleMode(current);
                player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);
                return true;
            }
        }

        return super.overrideOtherStackedOnMe(current, tryStack, slot, action, player, slotAccess);
    }
}
