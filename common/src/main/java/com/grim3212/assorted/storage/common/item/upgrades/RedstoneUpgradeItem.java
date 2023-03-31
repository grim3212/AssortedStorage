package com.grim3212.assorted.storage.common.item.upgrades;


import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class RedstoneUpgradeItem extends ModeCrateUpgradeItem {

    private final static int MAX_MODE = 7;

    public RedstoneUpgradeItem(Properties props) {
        super(props);
    }

    // Modes:
    // 0: Based on slot 0
    // 1: Based on slot 1
    // 2: Based on slot 2
    // 3: Based on slot 3
    // 4: Based on all slots
    // 5: Based on most full slot
    // 6: Based on least full slot

    @Override
    protected void cycleMode(ItemStack stack) {
        int currentMode = NBTHelper.getInt(stack, "Mode", 4);
        int newMode = currentMode + 1;
        NBTHelper.putInt(stack, "Mode", newMode >= MAX_MODE ? 0 : newMode);
    }

    @Override
    protected Component modeDisplay(ItemStack stack) {
        int currentMode = NBTHelper.getInt(stack, "Mode", 4);
        String modeKey = ".info.upgrade_redstone.mode.slot";
        if (currentMode == 4) {
            modeKey = ".info.upgrade_redstone.mode.all";
        } else if (currentMode == 5) {
            modeKey = ".info.upgrade_redstone.mode.most";
        } else if (currentMode == 6) {
            modeKey = ".info.upgrade_redstone.mode.least";
        }
        return Component.translatable(Constants.MOD_ID + ".info.upgrade.mode", Component.translatable(Constants.MOD_ID + modeKey, currentMode).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.AQUA);
    }

    @Override
    protected int startingMode() {
        return 4;
    }
}
