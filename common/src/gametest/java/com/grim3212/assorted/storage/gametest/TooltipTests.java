package com.grim3212.assorted.storage.gametest;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.StorageDataComponents;
import com.grim3212.assorted.storage.common.item.StorageInfo;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.item.UpgradeModeInfo;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.storage.gametest.StorageTestSupport.*;

/**
 * Item tooltips, which come from data components now that {@code Item#appendHoverText} is deprecated.
 */
final class TooltipTests {

    private TooltipTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("storage_tooltips_come_from_components", TooltipTests::storageTooltipsComeFromComponents);
    }

    /**
     * Each storage item's lock, level and mode lines come from its {@code storage_info} or {@code
     * upgrade_mode_info} component: bags only say they are locked, keys and storage blocks show the
     * code. NeoForge also builds the full tooltip on the server, so there it is checked too; {@code
     * StorageClientGameTests} covers Fabric.
     */
    private static void storageTooltipsComeFromComponents(GameTestHelper helper) {
        DataComponentType<StorageInfo> info = StorageDataComponents.STORAGE_INFO.get();
        DataComponentType<UpgradeModeInfo> modeInfo = StorageDataComponents.UPGRADE_MODE_INFO.get();
        String locked = Constants.MOD_ID + ".info.locked";
        String combo = Constants.MOD_ID + ".info.combo";
        String level = Constants.MOD_ID + ".info.level_upgrade_level";
        String mode = Constants.MOD_ID + ".info.upgrade.mode";

        ItemStack lockedBag = StorageUtil.setCodeOnStack("1234", new ItemStack(StorageItems.BAG.get()));
        helper.assertValueEqual(tooltipKeys(helper, lockedBag, info), List.of(locked, level), "a locked bag's tooltip");
        helper.assertValueEqual(tooltipKeys(helper, new ItemStack(StorageItems.BAG.get()), info), List.of(level), "an unlocked bag's tooltip");
        helper.assertValueEqual(tooltipKeys(helper, StorageUtil.setCodeOnStack("1234", new ItemStack(StorageItems.ENDER_BAG.get())), info), List.of(locked), "a locked ender bag's tooltip");
        helper.assertValueEqual(tooltipKeys(helper, StorageUtil.setCodeOnStack("1234", new ItemStack(StorageItems.LOCKSMITH_KEY.get())), info), List.of(combo), "a cut key's tooltip");

        ItemStack lockedBarrel = StorageUtil.setCodeOnStack("1234", new ItemStack(StorageBlocks.BARRELS.values().iterator().next().get()));
        helper.assertValueEqual(tooltipKeys(helper, lockedBarrel, info), List.of(combo, level), "a locked barrel item's tooltip");
        ItemStack levelUpgrade = new ItemStack(StorageItems.LEVEL_UPGRADES.values().iterator().next().get());
        helper.assertValueEqual(tooltipKeys(helper, levelUpgrade, info), List.of(level), "a level upgrade's tooltip");

        helper.assertValueEqual(tooltipKeys(helper, new ItemStack(StorageItems.AMOUNT_UPGRADE.get()), modeInfo), List.of(mode), "an amount upgrade's tooltip");
        helper.assertValueEqual(tooltipKeys(helper, new ItemStack(StorageItems.REDSTONE_UPGRADE.get()), modeInfo), List.of(mode), "a redstone upgrade's tooltip");

        if (onNeoForge()) {
            helper.assertTrue(fullTooltipKeys(helper, lockedBag).contains(locked), "the bag's lock line is missing from its tooltip");
            helper.assertTrue(fullTooltipKeys(helper, new ItemStack(StorageItems.AMOUNT_UPGRADE.get())).contains(mode), "the upgrade's mode line is missing from its tooltip");
        }
        helper.succeed();
    }
}
