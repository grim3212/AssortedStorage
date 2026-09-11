package com.grim3212.assorted.storage.gametest;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Storage item tooltips as Fabric builds them, which only happens on the client. Run with
 * {@code ./gradlew :fabric:runClientGameTest}; it exits non-zero on a failure.
 */
public class StorageClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        // Inside a world: an ItemStack cannot be made on the title screen, because an item's default
        // components are only bound once a world's registries have loaded.
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            context.runOnClient(client -> {
                List<String> bag = tooltipKeys(client, StorageUtil.setCodeOnStack("1234", new ItemStack(StorageItems.BAG.get())));
                if (!bag.contains(Constants.MOD_ID + ".info.locked") || !bag.contains(Constants.MOD_ID + ".info.level_upgrade_level")) {
                    throw new AssertionError("a locked bag's tooltip is " + bag);
                }

                List<String> upgrade = tooltipKeys(client, new ItemStack(StorageItems.AMOUNT_UPGRADE.get()));
                if (!upgrade.contains(Constants.MOD_ID + ".info.upgrade.mode")) {
                    throw new AssertionError("an amount upgrade's tooltip is " + upgrade);
                }
            });
        }
    }

    private static List<String> tooltipKeys(Minecraft client, ItemStack stack) {
        return stack.getTooltipLines(Item.TooltipContext.of(client.level), client.player, TooltipFlag.NORMAL).stream()
                .map(line -> line.getContents() instanceof TranslatableContents translatable ? translatable.getKey() : line.getString())
                .toList();
    }
}
