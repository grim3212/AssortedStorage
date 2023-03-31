package com.grim3212.assorted.storage.common.item.upgrades;

import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class ModeCrateUpgradeItem extends BasicCrateUpgradeItem {

    public ModeCrateUpgradeItem(Properties props) {
        super(props);
    }

    protected abstract void cycleMode(ItemStack stack);

    protected abstract Component modeDisplay(ItemStack stack);

    protected abstract int startingMode();

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stackInHand = player.getItemInHand(hand);
        if (stackInHand.getItem() == this) {
            this.cycleMode(stackInHand);
            this.sendMessage(player, modeDisplay(stackInHand));
            return InteractionResultHolder.success(stackInHand);
        }

        return super.use(level, player, hand);
    }

    protected void sendMessage(Player player, Component message) {
        if (!player.level.isClientSide) {
            player.sendSystemMessage(message);
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        NBTHelper.putInt(stack, "Mode", this.startingMode());
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(modeDisplay(stack));
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
