package com.grim3212.assorted.storage.common.item.upgrades;

import com.grim3212.assorted.storage.common.item.UpgradeModeInfo;
import com.grim3212.assorted.storage.common.item.StorageDataComponents;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class AmountUpgradeItem extends ModeCrateUpgradeItem {

    public AmountUpgradeItem(Properties props) {
        super(props.component(StorageDataComponents.UPGRADE_MODE_INFO.get(), new UpgradeModeInfo(UpgradeModeInfo.Kind.AMOUNT)));
    }

    @Override
    protected void cycleMode(ItemStack stack) {
        int currentMode = NBTHelper.getInt(stack, "Mode", 0);
        NBTHelper.putInt(stack, "Mode", currentMode == 0 ? 1 : 0);
    }

    @Override
    protected Component modeDisplay(ItemStack stack) {
        return describeMode(NBTHelper.getInt(stack, "Mode"));
    }

    /** The mode line: the chat message when the mode is cycled, and the tooltip. */
    public static Component describeMode(int currentMode) {
        return Component.translatable(Constants.MOD_ID + ".info.upgrade.mode", Component.translatable(Constants.MOD_ID + (currentMode == 0 ? ".info.upgrade_amount.mode.simple" : ".info.upgrade_amount.mode.full")).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.AQUA);
    }

    @Override
    protected int startingMode() {
        return 0;
    }

}
