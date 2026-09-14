package com.grim3212.assorted.storage.gametest;

import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateControllerBlockEntity;
import com.grim3212.assorted.storage.common.inventory.crates.CrateSidedInv;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.storage.gametest.StorageTestSupport.oakCrate;

/**
 * A crate as other mods reach it, through {@code ItemStorage.SIDED}. The shared tests cover the
 * handler; these cover the Fabric wrapper around it, which the Transfer API rolls back on every
 * simulation.
 */
final class CrateTransferTests {

    private CrateTransferTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("fabric_crate_survives_a_rolled_back_transaction", CrateTransferTests::crateSurvivesARolledBackTransaction);
        out.accept("fabric_controller_storage_follows_the_network", CrateTransferTests::controllerStorageFollowsTheNetwork);
    }

    /**
     * A controller's storage keeps up with its network. It only finds its crates on a server tick, so
     * a lookup can land before it has found any, and the count moves as crates come and go.
     */
    private static void controllerStorageFollowsTheNetwork(GameTestHelper helper) {
        BlockPos controllerPos = new BlockPos(3, 1, 4);
        BlockPos cratePos = controllerPos.east();

        helper.setBlock(controllerPos, StorageBlocks.CRATE_CONTROLLER.get());
        CrateControllerBlockEntity controller = helper.getBlockEntity(controllerPos, CrateControllerBlockEntity.class);

        // Looked up before the controller has ticked.
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(controllerPos), null);
        helper.assertTrue(storage instanceof SlottedStorage<ItemVariant>, "the controller exposed no slotted item storage");
        SlottedStorage<ItemVariant> slotted = (SlottedStorage<ItemVariant>) storage;
        helper.assertValueEqual(slotted.getSlotCount(), 0, "the controller's slot count before it found any crates");

        // Now give it a crate and let it find one.
        helper.setBlock(cratePos, oakCrate());
        CrateSidedInv crate = helper.getBlockEntity(cratePos, CrateBlockEntity.class).getItemStackStorageHandler();
        crate.setItem(0, new LargeItemStack(new ItemStack(Items.COBBLESTONE), 300));
        controller.tick();

        helper.assertValueEqual(slotted.getSlotCount(), 1, "the controller's slot count once it found a crate");
        helper.assertValueEqual(slotted.getSlot(0).getAmount(), 300L, "what the controller reports the crate holding");

        // And the crate going away takes its slot with it.
        helper.setBlock(cratePos, Blocks.AIR);
        controller.tick();
        helper.assertValueEqual(slotted.getSlotCount(), 0, "the controller's slot count after the crate was removed");
        helper.succeed();
    }

    private static void crateSurvivesARolledBackTransaction(GameTestHelper helper) {
        final BlockPos pos = new BlockPos(4, 1, 4);
        helper.setBlock(pos, oakCrate());

        CrateSidedInv crate = helper.getBlockEntity(pos, CrateBlockEntity.class).getItemStackStorageHandler();
        crate.setItem(0, new LargeItemStack(new ItemStack(Items.COBBLESTONE), 2000));

        Storage<ItemVariant> storage = ItemStorage.SIDED.find(helper.getLevel(), helper.absolutePos(pos), null);
        helper.assertTrue(storage instanceof SlottedStorage<ItemVariant>, "the crate exposed no slotted item storage");
        SlottedStorage<ItemVariant> slotted = (SlottedStorage<ItemVariant>) storage;

        // What a readout mod asks for: the slot's real amount.
        helper.assertValueEqual(slotted.getSlot(0).getAmount(), 2000L, "the amount the crate reports to the Transfer API");

        // A simulated extract is a nested transaction that is always aborted.
        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = storage.extract(ItemVariant.of(Items.COBBLESTONE), 64, transaction);
            helper.assertValueEqual(extracted, 64L, "what the extract took inside the transaction");
            transaction.abort();
        }

        helper.assertValueEqual(crate.getLargeItemStack(0).getAmount(), 2000, "the amount left after a rolled back extract");
        helper.assertValueEqual(slotted.getSlot(0).getAmount(), 2000L, "the amount reported after a rolled back extract");

        // A committed one really does take its items.
        try (Transaction transaction = Transaction.openOuter()) {
            storage.extract(ItemVariant.of(Items.COBBLESTONE), 64, transaction);
            transaction.commit();
        }

        helper.assertValueEqual(crate.getLargeItemStack(0).getAmount(), 1936, "the amount left after a committed extract");
        helper.succeed();
    }
}
