package com.grim3212.assorted.storage.common.item.upgrades;


import com.grim3212.assorted.storage.common.item.UpgradeModeInfo;
import com.grim3212.assorted.storage.common.item.StorageDataComponents;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class RedstoneUpgradeItem extends ModeCrateUpgradeItem {

    private final static int MAX_MODE = 7;

    public RedstoneUpgradeItem(Properties props) {
        super(props.component(StorageDataComponents.UPGRADE_MODE_INFO.get(), new UpgradeModeInfo(UpgradeModeInfo.Kind.REDSTONE)));
    }

    // Modes: 0-3 a single slot, 4 all slots, 5 the most full slot, 6 the least full slot.

    @Override
    protected void cycleMode(ItemStack stack) {
        int currentMode = NBTHelper.getInt(stack, "Mode", 4);
        int newMode = currentMode + 1;
        NBTHelper.putInt(stack, "Mode", newMode >= MAX_MODE ? 0 : newMode);
    }

    @Override
    protected Component modeDisplay(ItemStack stack) {
        return describeMode(NBTHelper.getInt(stack, "Mode", 4));
    }

    /** The mode line: the chat message when the mode is cycled, and the tooltip. */
    public static Component describeMode(int currentMode) {
        String modeKey = ".info.upgrade_redstone.mode.slot";
        if (currentMode == 4) {
            modeKey = ".info.upgrade_redstone.mode.all";
        } else if (currentMode == 5) {
            modeKey = ".info.upgrade_redstone.mode.most";
        } else if (currentMode == 6) {
            modeKey = ".info.upgrade_redstone.mode.least";
        }
        // Modes and slots are indices everywhere but the label, which counts from 1 as the player does.
        return Component.translatable(Constants.MOD_ID + ".info.upgrade.mode", Component.translatable(Constants.MOD_ID + modeKey, currentMode + 1).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.AQUA);
    }

    @Override
    protected int startingMode() {
        return 4;
    }
}
