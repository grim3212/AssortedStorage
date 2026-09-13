package com.grim3212.assorted.storage.gametest;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.storage.api.StorageAccessUtil;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.LockedCopperDoorBlock;
import com.grim3212.assorted.storage.common.block.LockedDoorBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.BaseLockedBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.LockedEnderChestBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.WoodCabinetBlockEntity;
import com.grim3212.assorted.storage.common.inventory.LocksmithWorkbenchContainer;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingItemHandler;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.network.SetLockPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
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
        out.accept("every_vanilla_door_can_be_locked", LockTests::everyVanillaDoorCanBeLocked);
        out.accept("locked_doors_drop_the_door_they_stand_in_for", LockTests::lockedDoorsDropTheDoorTheyStandInFor);
        out.accept("locked_doors_are_picked_as_the_door_they_stand_in_for", LockTests::lockedDoorsArePickedAsTheDoorTheyStandInFor);
        out.accept("locked_copper_doors_oxidise_keeping_their_lock", LockTests::lockedCopperDoorsOxidiseKeepingTheirLock);
        out.accept("locked_copper_doors_scrape_and_wax", LockTests::lockedCopperDoorsScrapeAndWax);
        out.accept("a_locked_copper_door_oxidises_on_its_own", LockTests::aLockedCopperDoorOxidisesOnItsOwn);
    }

    /**
     * The whole path a real world takes: a random tick reaching {@code changeOverTime}, its scan of
     * the copper nearby, its roll and the block swap that follows, driven by ticking rather than by
     * calling {@code getNext} directly. The unaffected door is the one to use: the scan refuses to
     * progress a block with less oxidised copper within four blocks, so a neighbouring test's copper
     * doors can only speed this up, never stall it.
     */
    private static void aLockedCopperDoorOxidisesOnItsOwn(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Block unaffected = StorageBlocks.VANILLA_DOORS.get(Blocks.COPPER_DOOR.weathering().unaffected()).get();
        Block exposed = StorageBlocks.VANILLA_DOORS.get(Blocks.COPPER_DOOR.weathering().exposed()).get();

        BlockPos lower = new BlockPos(4, 1, 4);
        helper.setBlock(lower, unaffected.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        helper.setBlock(lower.above(), unaffected.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        helper.getBlockEntity(lower, BaseLockedBlockEntity.class).setLockCode(CODE);
        helper.getBlockEntity(lower.above(), BaseLockedBlockEntity.class).setLockCode(CODE);

        // ~4% a tick, so 2000 tries is a certainty many times over without being a slow test.
        BlockPos pos = helper.absolutePos(lower);
        for (int tick = 0; tick < 2000 && helper.getBlockState(lower).is(unaffected); tick++) {
            level.getBlockState(pos).randomTick(level, pos, level.getRandom());
        }

        helper.assertTrue(helper.getBlockState(lower).is(exposed), "the locked copper door never oxidised in 2000 random ticks, it is still " + name(helper.getBlockState(lower).getBlock()));
        helper.assertTrue(helper.getBlockState(lower.above()).is(exposed), "the upper half did not follow the lower half as it oxidised, it is still " + name(helper.getBlockState(lower.above()).getBlock()));
        helper.assertValueEqual(helper.getBlockEntity(lower, BaseLockedBlockEntity.class).getLockCode(), CODE, "the lock after oxidising");
        helper.succeed();
    }

    /**
     * A locked copper door goes on oxidising, and arrives at the next stage still locked. The step
     * itself is what vanilla's {@code changeOverTime} does - the roll that schedules it is vanilla's
     * and not worth a probabilistic test - so this drives the change and checks what it leaves
     * behind: both halves at the new stage, the lock still on, and no padlock on the floor.
     */
    private static void lockedCopperDoorsOxidiseKeepingTheirLock(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        List<String> wrong = new ArrayList<>();

        int i = 0;
        for (Map.Entry<Block, IRegistryObject<LockedDoorBlock>> entry : copperDoors().entrySet()) {
            Block locked = entry.getValue().get();
            boolean shouldOxidise = locked instanceof LockedCopperDoorBlock copper && copper.getAge() != WeatherState.OXIDIZED;

            BlockPos lower = spread(i++);
            BlockPos upper = lower.above();
            helper.setBlock(lower, locked.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
            helper.setBlock(upper, locked.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
            helper.getBlockEntity(lower, BaseLockedBlockEntity.class).setLockCode(CODE);
            helper.getBlockEntity(upper, BaseLockedBlockEntity.class).setLockCode(CODE);

            // The baked property that actually schedules the change. It cannot be worked out from
            // the loaders' oxidation registries, which load later, so it is the thing to assert.
            if (helper.getBlockState(lower).isRandomlyTicking() != shouldOxidise) {
                wrong.add(name(locked) + (shouldOxidise ? " is not randomly ticked, so it never oxidises" : " is randomly ticked but has nowhere to go"));
            }

            Optional<BlockState> next = shouldOxidise ? ((LockedCopperDoorBlock) locked).getNext(helper.getBlockState(lower)) : Optional.empty();
            if (!shouldOxidise) {
                continue;
            }
            if (next.isEmpty()) {
                wrong.add(name(locked) + " has no next oxidation stage");
                continue;
            }

            // Exactly what ChangeOverTimeBlock#changeOverTime does once its roll succeeds.
            level.setBlockAndUpdate(helper.absolutePos(lower), next.get());

            Block expected = next.get().getBlock();
            if (!helper.getBlockState(lower).is(expected)) {
                wrong.add(name(locked) + " lower half became " + name(helper.getBlockState(lower).getBlock()) + ", not " + name(expected));
            }
            if (!helper.getBlockState(upper).is(expected)) {
                wrong.add(name(locked) + " upper half stayed " + name(helper.getBlockState(upper).getBlock()) + " while the lower half became " + name(expected));
            }
            for (BlockPos half : List.of(lower, upper)) {
                BlockEntity be = level.getBlockEntity(helper.absolutePos(half));
                if (!(be instanceof BaseLockedBlockEntity locking) || !CODE.equals(locking.getLockCode())) {
                    wrong.add(name(locked) + " lost its lock on the " + (half.equals(lower) ? "lower" : "upper") + " half when it oxidised");
                }
            }
        }

        List<ItemEntity> dropped = helper.getEntities(EntityTypes.ITEM);
        if (dropped.stream().anyMatch(item -> item.getItem().is(StorageItems.LOCKSMITH_LOCK.get()))) {
            wrong.add("a padlock was dropped while oxidising - the block entity was removed rather than kept");
        }

        helper.assertTrue(wrong.isEmpty(), wrong.size() + " oxidation problem(s): " + String.join("; ", wrong));
        helper.succeed();
    }

    /**
     * Axe scraping and honeycomb waxing are vanilla's own item code, reached through the loaders'
     * oxidation registries - NeoForge's {@code neoforge:oxidizables} and {@code neoforge:waxables}
     * data maps, Fabric's {@code OxidizableBlocksRegistry}. Miss one and the door simply cannot be
     * scraped or waxed, silently. Asked the way the axe and the honeycomb ask, because NeoForge
     * ignores the vanilla {@code WAXABLES} / {@code NEXT_BY_BLOCK} fields.
     */
    private static void lockedCopperDoorsScrapeAndWax(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();

        Blocks.COPPER_DOOR.weathering().progressMapping((from, to) -> {
            Block lockedFrom = StorageBlocks.VANILLA_DOORS.get(from).get();
            Block lockedTo = StorageBlocks.VANILLA_DOORS.get(to).get();
            Optional<BlockState> scraped = WeatheringCopper.getPrevious(lockedTo.defaultBlockState());
            if (scraped.filter(state -> state.is(lockedFrom)).isEmpty()) {
                wrong.add(name(lockedTo) + " scrapes back to " + scraped.map(state -> name(state.getBlock())).orElse("nothing") + ", not " + name(lockedFrom));
            }
        });

        Blocks.COPPER_DOOR.zipUnwaxedWaxed((unwaxed, waxed) -> {
            Block lockedUnwaxed = StorageBlocks.VANILLA_DOORS.get(unwaxed).get();
            Block lockedWaxed = StorageBlocks.VANILLA_DOORS.get(waxed).get();
            Optional<BlockState> waxedState = HoneycombItem.getWaxed(lockedUnwaxed.defaultBlockState());
            if (waxedState.filter(state -> state.is(lockedWaxed)).isEmpty()) {
                wrong.add(name(lockedUnwaxed) + " waxes into " + waxedState.map(state -> name(state.getBlock())).orElse("nothing") + ", not " + name(lockedWaxed));
            }
        });

        helper.assertTrue(wrong.isEmpty(), wrong.size() + " scrape/wax problem(s) on " + Services.PLATFORM.getPlatformName() + ": " + String.join("; ", wrong));
        helper.succeed();
    }

    /** The eight locked copper doors, keyed on the vanilla door each stands in for. */
    private static Map<Block, IRegistryObject<LockedDoorBlock>> copperDoors() {
        Map<Block, IRegistryObject<LockedDoorBlock>> doors = new LinkedHashMap<>();
        Blocks.COPPER_DOOR.forEach(door -> doors.put(door, StorageBlocks.VANILLA_DOORS.get(door)));
        return doors;
    }

    /**
     * A padlock works on every door vanilla has. The only symptom of a missing one is the padlock
     * doing nothing when a player right clicks that door, which is how cherry, pale oak, bamboo and
     * the eight copper doors were all silently unlockable.
     */
    private static void everyVanillaDoorCanBeLocked(GameTestHelper helper) {
        List<String> missing = BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(entry -> "minecraft".equals(entry.getKey().identifier().getNamespace()))
                .filter(entry -> entry.getValue() instanceof DoorBlock)
                .filter(entry -> !StorageBlocks.VANILLA_DOORS.containsKey(entry.getValue()))
                .map(entry -> entry.getKey().identifier().toString())
                .sorted()
                .toList();

        helper.assertTrue(missing.isEmpty(), "vanilla doors with no locked stand-in: " + missing);
        helper.succeed();
    }

    /**
     * Breaking a locked door with the right tool gives back the door the padlock went on. The
     * interesting half is the copper doors: like vanilla's they need a correct tool, so leaving one
     * out of a {@code #minecraft:mineable/*} tag makes it break at normal speed and drop nothing at
     * all, which no log or datagen run reports.
     */
    /**
     * A locked door is picked as the door the padlock went on; it has no item of its own, so a block
     * that does not answer here hands back nothing at all. Asked through
     * {@code BlockState#getCloneItemStack}, which is what vanilla's pick block calls on both loaders.
     */
    private static void lockedDoorsArePickedAsTheDoorTheyStandInFor(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        List<String> wrong = new ArrayList<>();

        int i = 0;
        for (Map.Entry<Block, IRegistryObject<LockedDoorBlock>> entry : StorageBlocks.VANILLA_DOORS.entrySet()) {
            Block locked = entry.getValue().get();
            BlockPos lower = spread(i++);
            helper.setBlock(lower, locked.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));

            ItemStack picked = helper.getBlockState(lower).getCloneItemStack(level, helper.absolutePos(lower), true);
            if (!picked.is(entry.getKey().asItem())) {
                wrong.add(name(locked) + " was picked as " + picked + " rather than " + name(entry.getKey()));
            }
        }

        helper.assertTrue(wrong.isEmpty(), wrong.size() + " locked door pick problem(s): " + String.join("; ", wrong));
        helper.succeed();
    }

    private static void lockedDoorsDropTheDoorTheyStandInFor(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        List<String> wrong = new ArrayList<>();

        int i = 0;
        for (Map.Entry<Block, IRegistryObject<LockedDoorBlock>> entry : StorageBlocks.VANILLA_DOORS.entrySet()) {
            Block locked = entry.getValue().get();
            BlockPos lower = spread(i++);
            helper.setBlock(lower, locked.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
            helper.setBlock(lower.above(), locked.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));

            if (helper.getBlockState(lower).requiresCorrectToolForDrops() && MINEABLE.stream().noneMatch(tag -> helper.getBlockState(lower).is(tag))) {
                wrong.add(name(locked) + " needs a correct tool but is in no mineable tag, so it drops nothing");
                continue;
            }

            level.destroyBlock(helper.absolutePos(lower), true);
            List<ItemEntity> dropped = helper.getEntities(EntityTypes.ITEM);
            if (dropped.stream().noneMatch(item -> item.getItem().is(entry.getKey().asItem()))) {
                wrong.add(name(locked) + " dropped " + dropped.stream().map(item -> item.getItem().toString()).toList() + " rather than " + name(entry.getKey()));
            }
            dropped.forEach(ItemEntity::discard);
        }

        helper.assertTrue(wrong.isEmpty(), wrong.size() + " locked door drop problem(s): " + String.join("; ", wrong));
        helper.succeed();
    }

    /** The tags a block that needs a correct tool has to be in for any tool to be the right one. */
    private static final List<TagKey<Block>> MINEABLE = List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.MINEABLE_WITH_HOE);

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
     * Every locked door: the right key opens it, no key and the wrong key do not. What they look
     * like while doing it is a manual check.
     */
    private static void lockedDoorsOpenOnlyWithTheRightKey(GameTestHelper helper) {
        Block[] doors = StorageBlocks.lockedDoors();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        // spread() lays out a 5x5 grid, and each door is two blocks tall, so 25 is all the 9x9x9
        // test box holds. A 26th would be written outside it and silently fail to place.
        helper.assertTrue(doors.length <= 25, doors.length + " locked doors no longer fit the test box; give this test its own layout");

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
