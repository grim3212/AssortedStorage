package com.grim3212.assorted.storage.gametest;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.api.StorageAccessUtil;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.BaseLockedBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.LockedEnderChestBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.WoodCabinetBlockEntity;
import com.grim3212.assorted.storage.common.inventory.LocksmithWorkbenchContainer;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingItemHandler;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.network.SetLockPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.storage.gametest.StorageTestSupport.*;

/**
 * Locks and keys: codes, locked doors, the key ring, the locksmith workbench, locked ender chests and lock sync.
 */
final class LockTests {

    private LockTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("lock_and_key_share_a_code", LockTests::lockAndKeyShareACode);
        out.accept("locked_blocks_send_their_lock_to_clients", LockTests::lockedBlocksSendTheirLockToClients);
        out.accept("locked_ender_chests_share_by_code", LockTests::lockedEnderChestsShareByCode);
        out.accept("key_ring_holds_keys_and_opens_a_lock", LockTests::keyRingHoldsKeysAndOpensALock);
        out.accept("locked_doors_open_only_with_the_right_key", LockTests::lockedDoorsOpenOnlyWithTheRightKey);
        out.accept("locksmith_workbench_codes_a_key_and_a_lock", LockTests::locksmithWorkbenchCodesAKeyAndALock);
    }

    /** A padlock puts its code on the block, and only a key carrying that same code opens it. */
    private static void lockAndKeyShareACode(GameTestHelper helper) {
        helper.setBlock(BLOCK, StorageBlocks.WOOD_CABINET.get());
        BlockPos pos = helper.absolutePos(BLOCK);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, StorageUtil.setCodeOnStack(CODE, new ItemStack(StorageItems.LOCKSMITH_LOCK.get())));
        helper.assertTrue(BaseStorageBlock.tryPlaceLock(helper.getLevel(), pos, player, InteractionHand.MAIN_HAND), "the cabinet refused a coded padlock");

        WoodCabinetBlockEntity cabinet = helper.getBlockEntity(BLOCK, WoodCabinetBlockEntity.class);
        helper.assertTrue(cabinet.isLocked(), "the cabinet did not lock");
        helper.assertValueEqual(cabinet.getLockCode(), CODE, "the code the padlock put on the cabinet");

        player.getInventory().clearContent();
        helper.assertFalse(StorageAccessUtil.canAccess(helper.getLevel(), pos, player), "a player carrying no key opened a locked cabinet");

        player.getInventory().setItem(0, StorageUtil.setCodeOnStack("9999", new ItemStack(StorageItems.LOCKSMITH_KEY.get())));
        helper.assertFalse(StorageAccessUtil.canAccess(helper.getLevel(), pos, player), "the wrong key opened a locked cabinet");

        player.getInventory().setItem(0, StorageUtil.setCodeOnStack(CODE, new ItemStack(StorageItems.LOCKSMITH_KEY.get())));
        helper.assertTrue(StorageAccessUtil.canAccess(helper.getLevel(), pos, player), "the matching key did not open a locked cabinet");

        helper.assertFalse(StorageAccessUtil.canStackAccess(new ItemStack(StorageItems.KEY_RING.get()), CODE), "an empty key ring opened a locked cabinet");
        helper.succeed();
    }

    /**
     * The barrel, hopper and crate controller send their lock to clients in the update tag, or
     * other players keep seeing the unlocked face.
     */
    private static void lockedBlocksSendTheirLockToClients(GameTestHelper helper) {
        assertLockReachesClients(helper, new BlockPos(2, 1, 4), StorageBlocks.LOCKED_BARREL.get());
        assertLockReachesClients(helper, new BlockPos(4, 1, 4), StorageBlocks.LOCKED_HOPPER.get());
        assertLockReachesClients(helper, new BlockPos(6, 1, 4), StorageBlocks.CRATE_CONTROLLER.get());
        helper.succeed();
    }

    /**
     * Locked ender chests are one inventory per lock code, held in level saved data, so two chests
     * sharing a code share their contents and a chest on another code sees none of it.
     */
    private static void lockedEnderChestsShareByCode(GameTestHelper helper) {
        LockedEnderChestBlockEntity first = enderChest(helper, new BlockPos(2, 1, 2), "shared-a");
        LockedEnderChestBlockEntity second = enderChest(helper, new BlockPos(6, 1, 2), "shared-a");
        LockedEnderChestBlockEntity other = enderChest(helper, new BlockPos(2, 1, 6), "shared-b");

        first.getItemStackStorageHandler().setStackInSlot(0, new ItemStack(Items.DIAMOND, 3));

        ItemStack shared = second.getItemStackStorageHandler().getStackInSlot(0);
        helper.assertTrue(shared.is(Items.DIAMOND), "a second chest on the same code did not see the contents");
        helper.assertValueEqual(shared.getCount(), 3, "the shared stack size");
        helper.assertTrue(other.getItemStackStorageHandler().getStackInSlot(0).isEmpty(), "a chest on a different code saw another code's contents");
        helper.succeed();
    }

    /** A key ring holds keys, and hands the matching one to a lock that asks. */
    private static void keyRingHoldsKeysAndOpensALock(GameTestHelper helper) {
        helper.setBlock(BLOCK, StorageBlocks.WOOD_CABINET.get());
        BlockPos pos = helper.absolutePos(BLOCK);
        helper.getBlockEntity(BLOCK, WoodCabinetBlockEntity.class).setLockCode(CODE);

        ItemStack ring = new ItemStack(StorageItems.KEY_RING.get());
        KeyRingItemHandler keys = new KeyRingItemHandler(ring);
        keys.load();
        helper.assertValueEqual(keys.getSlots(), KeyRingItemHandler.KEY_RING_SIZE, "a key ring's slot count");

        keys.setStackInSlot(0, StorageUtil.setCodeOnStack("9999", new ItemStack(StorageItems.LOCKSMITH_KEY.get())));
        keys.setStackInSlot(keys.getSlots() - 1, StorageUtil.setCodeOnStack(CODE, new ItemStack(StorageItems.LOCKSMITH_KEY.get())));

        helper.assertFalse(StorageAccessUtil.canStackAccess(ring, "1234"), "a key ring opened a lock it holds no key for");
        helper.assertTrue(StorageAccessUtil.canStackAccess(ring, "9999"), "a key ring did not hand over the key in its first slot");
        helper.assertTrue(StorageAccessUtil.canStackAccess(ring, CODE), "a key ring did not hand over the key in its last slot");

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertFalse(StorageAccessUtil.canAccess(helper.getLevel(), pos, player), "an empty handed player opened a locked cabinet");
        player.getInventory().setItem(0, ring);
        helper.assertTrue(StorageAccessUtil.canAccess(helper.getLevel(), pos, player), "a key ring in the inventory did not open the cabinet");
        helper.succeed();
    }

    /**
     * All fourteen locked doors: the right key opens them, no key and the wrong key do not. What
     * they look like while doing it is a manual check.
     */
    private static void lockedDoorsOpenOnlyWithTheRightKey(GameTestHelper helper) {
        Block[] doors = StorageBlocks.lockedDoors();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        for (int i = 0; i < doors.length; i++) {
            Block door = doors[i];
            BlockPos lower = spread(i);
            BlockPos upper = lower.above();

            helper.setBlock(lower, door.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
            helper.setBlock(upper, door.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
            helper.getBlockEntity(lower, BaseLockedBlockEntity.class).setLockCode(CODE);
            helper.getBlockEntity(upper, BaseLockedBlockEntity.class).setLockCode(CODE);
            helper.assertFalse(helper.getBlockState(lower).getValue(DoorBlock.OPEN), name(door) + " was open before anyone touched it");

            player.getInventory().clearContent();
            helper.assertFalse(knock(helper, player, lower).consumesAction(), name(door) + " answered a player with no key");
            helper.assertFalse(helper.getBlockState(lower).getValue(DoorBlock.OPEN), name(door) + " opened for a player with no key");

            player.getInventory().setItem(0, StorageUtil.setCodeOnStack("9999", new ItemStack(StorageItems.LOCKSMITH_KEY.get())));
            helper.assertFalse(knock(helper, player, lower).consumesAction(), name(door) + " answered the wrong key");
            helper.assertFalse(helper.getBlockState(lower).getValue(DoorBlock.OPEN), name(door) + " opened for the wrong key");

            player.getInventory().setItem(0, StorageUtil.setCodeOnStack(CODE, new ItemStack(StorageItems.LOCKSMITH_KEY.get())));
            helper.assertTrue(knock(helper, player, lower).consumesAction(), name(door) + " refused the matching key");
            helper.assertTrue(helper.getBlockState(lower).getValue(DoorBlock.OPEN), name(door) + " did not open for the matching key");
            helper.assertTrue(helper.getBlockState(upper).getValue(DoorBlock.OPEN), name(door) + " opened only one of its halves");
        }

        helper.succeed();
    }

    /**
     * The locksmith loop: iron crafts into blank keys and locks, a code typed into the workbench
     * goes on the item in the slot, and the lock and key that come out match. The code arrives as a
     * {@link SetLockPacket}, whose handler is called directly.
     */
    private static void locksmithWorkbenchCodesAKeyAndALock(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ItemStack iron = new ItemStack(Items.IRON_INGOT);
        ItemStack none = ItemStack.EMPTY;

        ItemStack blankKey = crafted(helper, CraftingInput.of(2, 3, List.of(iron, iron, iron, iron, iron, none)));
        helper.assertTrue(blankKey.is(StorageItems.LOCKSMITH_KEY.get()), "iron did not craft into a locksmith key");
        ItemStack blankLock = crafted(helper, CraftingInput.of(3, 3, List.of(none, iron, none, iron, none, iron, iron, iron, iron)));
        helper.assertTrue(blankLock.is(StorageItems.LOCKSMITH_LOCK.get()), "iron did not craft into a padlock");
        helper.assertTrue(StorageUtil.getCode(blankKey).isEmpty(), "a freshly crafted key already carried a code");

        helper.setBlock(BLOCK, StorageBlocks.LOCKSMITH_WORKBENCH.get());
        ServerPlayer player = survivalPlayer(helper, ItemStack.EMPTY);

        // The menu is built and handed to the player directly rather than by right clicking the
        // block. Both loaders route the block's own open through their extended-menu plumbing -
        // NeoForge sends a payload, Fabric demands an ExtendedMenuProvider - and neither survives a
        // test player's embedded connection. What is under test is the code, not the opening.
        Vec3 standing = helper.absoluteVec(Vec3.atBottomCenterOf(BLOCK.above()));
        player.snapTo(standing.x, standing.y, standing.z, 0.0F, 0.0F);

        ContainerLevelAccess at = ContainerLevelAccess.create(level, helper.absolutePos(BLOCK));
        player.containerMenu = LocksmithWorkbenchContainer.createContainer(1, player.getInventory(), at);
        helper.runBeforeTestEnd(() -> player.containerMenu.removed(player));
        helper.assertTrue(player.containerMenu.stillValid(player), "the workbench menu did not accept the block it was opened on");

        LocksmithWorkbenchContainer menu = (LocksmithWorkbenchContainer) player.containerMenu;
        menu.getSlot(0).set(blankKey);
        helper.assertTrue(menu.getSlot(1).getItem().isEmpty(), "the workbench made something before a code was typed");
        menu.getSlot(0).set(ItemStack.EMPTY);

        ItemStack codedKey = typeCode(helper, player, blankKey);
        ItemStack codedLock = typeCode(helper, player, blankLock);
        helper.assertValueEqual(StorageUtil.getCode(codedKey), CODE, "the code the workbench put on the key");
        helper.assertValueEqual(StorageUtil.getCode(codedLock), CODE, "the code the workbench put on the lock");

        BlockPos cabinet = new BlockPos(4, 1, 6);
        helper.setBlock(cabinet, StorageBlocks.WOOD_CABINET.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, codedLock);
        helper.assertTrue(BaseStorageBlock.tryPlaceLock(level, helper.absolutePos(cabinet), player, InteractionHand.MAIN_HAND), "the cabinet refused the workbench's padlock");
        helper.assertValueEqual(helper.getBlockEntity(cabinet, WoodCabinetBlockEntity.class).getLockCode(), CODE, "the code the workbench's padlock put on the cabinet");

        player.getInventory().clearContent();
        player.getInventory().setItem(0, codedKey);
        helper.assertTrue(StorageAccessUtil.canAccess(level, helper.absolutePos(cabinet), player), "the key the workbench made did not open the lock it made");
        helper.succeed();
    }
}
