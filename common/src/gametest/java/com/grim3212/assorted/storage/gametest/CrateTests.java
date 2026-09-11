package com.grim3212.assorted.storage.gametest;

import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateCompactingBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateControllerBlockEntity;
import com.grim3212.assorted.storage.common.inventory.crates.CompactingCrateInventory;
import com.grim3212.assorted.storage.common.inventory.crates.CrateSidedInv;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.storage.gametest.StorageTestSupport.*;

/**
 * Crates: bridges, upgrades, compacting and the redstone upgrade.
 */
final class CrateTests {

    private CrateTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("crates_join_through_a_bridge", CrateTests::cratesJoinThroughABridge);
        out.accept("crate_upgrades_apply_and_read_back", CrateTests::crateUpgradesApplyAndReadBack);
        out.accept("compacting_crate_compacts_up_a_tier", CrateTests::compactingCrateCompactsUpATier);
        out.accept("redstone_upgrade_emits_a_signal", CrateTests::redstoneUpgradeEmitsASignal);
    }

    /**
     * A controller reaches crates through a bridge and hands them the items it is given. A bridge
     * carries the network but holds nothing itself, so it has to be walked through rather than
     * counted as a crate.
     */
    private static void cratesJoinThroughABridge(GameTestHelper helper) {
        BlockPos controllerPos = new BlockPos(3, 1, 4);
        BlockPos bridgePos = controllerPos.east();
        BlockPos cratePos = bridgePos.east();

        helper.setBlock(controllerPos, StorageBlocks.CRATE_CONTROLLER.get());
        helper.setBlock(bridgePos, StorageBlocks.CRATE_BRIDGE.get());
        helper.setBlock(cratePos, oakCrate());

        CrateControllerBlockEntity controller = helper.getBlockEntity(controllerPos, CrateControllerBlockEntity.class);
        controller.tick();

        helper.assertTrue(controller.hasConnectedCrates(), "the controller found no crates through a bridge");
        helper.assertValueEqual(controller.getItemStackStorageHandler().getSlots(), 1, "the controller's slot count for one single crate");

        ItemStack leftover = controller.getItemStackStorageHandler().insertItem(0, new ItemStack(Items.DIAMOND, 1), false);
        helper.assertTrue(leftover.isEmpty(), "the controller rejected an item with a crate connected");

        CrateSidedInv crate = helper.getBlockEntity(cratePos, CrateBlockEntity.class).getItemStackStorageHandler();
        helper.assertTrue(crate.getLargeItemStack(0).getStack().is(Items.DIAMOND), "the item did not reach the crate behind the bridge");
        helper.assertValueEqual(crate.getLargeItemStack(0).getAmount(), 1, "the amount that reached the crate");

        // A crate slot holds far more than a stack once it knows what it is holding.
        helper.assertTrue(controller.getItemStackStorageHandler().insertItem(0, new ItemStack(Items.DIAMOND, 64), false).isEmpty(),
                "the controller rejected a full stack into a crate that was nowhere near full");
        helper.assertValueEqual(crate.getLargeItemStack(0).getAmount(), 65, "the crate's running total");
        helper.succeed();
    }

    /** Upgrades dropped into a crate's enhancement slots take effect and read back off the crate. */
    private static void crateUpgradesApplyAndReadBack(GameTestHelper helper) {
        helper.setBlock(BLOCK, oakCrate());
        CrateSidedInv crate = helper.getBlockEntity(BLOCK, CrateBlockEntity.class).getItemStackStorageHandler();

        // Capacity is a multiple of what the held item stacks to, so the slot needs an item first.
        crate.setItem(0, new LargeItemStack(new ItemStack(Items.DIAMOND), 1));
        int base = crate.getMaxStackSizeForSlot(0);
        helper.assertValueEqual(base, 64 * 32, "a single crate's base capacity");
        helper.assertValueEqual(crate.getStorageModifier(), 1, "the storage modifier with no upgrade");

        // A stone level upgrade is storage level 0, worth five times the base capacity.
        crate.getEnhancements().set(1, new ItemStack(StorageItems.LEVEL_UPGRADES.get(StorageMaterial.STONE).get()));
        helper.assertValueEqual(crate.getMaxStackSizeForSlot(0), base * 6, "the capacity after a stone level upgrade");
        helper.assertValueEqual(crate.getStorageModifier(), 6, "the storage modifier after a stone level upgrade");

        helper.assertFalse(crate.hasVoidUpgrade(), "the crate claimed a void upgrade before one was fitted");
        helper.assertFalse(crate.hasGlowUpgrade(), "the crate claimed a glow upgrade before one was fitted");
        crate.getEnhancements().set(2, new ItemStack(StorageItems.VOID_UPGRADE.get()));
        crate.getEnhancements().set(3, new ItemStack(StorageItems.GLOW_UPGRADE.get()));
        helper.assertTrue(crate.hasVoidUpgrade(), "the void upgrade did not read back off the crate");
        helper.assertTrue(crate.hasGlowUpgrade(), "the glow upgrade did not read back off the crate");

        // With a void upgrade fitted a full slot swallows the overflow instead of handing it back.
        int full = crate.getMaxStackSizeForSlot(0);
        crate.setItem(0, new LargeItemStack(new ItemStack(Items.DIAMOND), full));
        helper.assertValueEqual(crate.addItem(0, new ItemStack(Items.DIAMOND, 64)), 0, "the void upgrade handed back a remainder");
        helper.assertValueEqual(crate.getLargeItemStack(0).getAmount(), full, "the void upgrade changed the stored amount");
        helper.succeed();
    }

    /**
     * A compactor works out the crafting tiers of what it is given and keeps every tier in step.
     * The whole search runs off the recipe manager, which is server side only in 26.x and reaches
     * results and ingredients through {@code RecipeDisplay} rather than off the recipe itself.
     */
    private static void compactingCrateCompactsUpATier(GameTestHelper helper) {
        helper.setBlock(BLOCK, StorageBlocks.CRATE_COMPACTING.get());
        CompactingCrateInventory compactor = (CompactingCrateInventory) helper.getBlockEntity(BLOCK, CrateCompactingBlockEntity.class).getItemStackStorageHandler();

        helper.assertValueEqual(compactor.addItem(0, new ItemStack(Items.IRON_INGOT, 9)), 0, "the compactor did not take nine iron ingots");

        helper.assertTrue(compactor.getLargeItemStack(1).getStack().is(Items.IRON_INGOT), "the middle tier is not the item that was inserted");
        helper.assertValueEqual(compactor.getLargeItemStack(1).getAmount(), 9, "the ingot tier amount");

        helper.assertTrue(compactor.getLargeItemStack(0).getStack().is(Items.IRON_BLOCK), "nine iron ingots did not compact up into a block");
        helper.assertValueEqual(compactor.getLargeItemStack(0).getAmount(), 1, "the block tier amount");

        helper.assertTrue(compactor.getLargeItemStack(2).getStack().is(Items.IRON_NUGGET), "the compactor did not find the nugget tier");
        helper.assertValueEqual(compactor.getLargeItemStack(2).getAmount(), 81, "the nugget tier amount");
        helper.succeed();
    }

    /**
     * A redstone upgrade turns how full a crate is into a signal. Its default mode reads slot 0,
     * and the signal is the fraction of that slot's capacity, so an untouched crate is silent, a
     * full one is 15 and a half full one is 8.
     */
    private static void redstoneUpgradeEmitsASignal(GameTestHelper helper) {
        helper.setBlock(BLOCK, oakCrate());
        CrateSidedInv crate = helper.getBlockEntity(BLOCK, CrateBlockEntity.class).getItemStackStorageHandler();

        crate.setItem(0, new LargeItemStack(new ItemStack(Items.DIAMOND), 1));
        int full = crate.getMaxStackSizeForSlot(0);
        crate.setItem(0, new LargeItemStack(new ItemStack(Items.DIAMOND), full));
        helper.assertRedstoneSignal(BLOCK, Direction.UP, signal -> signal == 0, () -> Component.literal("a full crate with no redstone upgrade should be silent"));

        crate.getEnhancements().set(1, new ItemStack(StorageItems.REDSTONE_UPGRADE.get()));
        helper.assertRedstoneSignal(BLOCK, Direction.UP, signal -> signal == 15, () -> Component.literal("a full crate with a redstone upgrade should read 15"));

        crate.setItem(0, new LargeItemStack(new ItemStack(Items.DIAMOND), full / 2));
        helper.assertRedstoneSignal(BLOCK, Direction.UP, signal -> signal == 8, () -> Component.literal("a half full crate with a redstone upgrade should read 8"));
        helper.succeed();
    }
}
