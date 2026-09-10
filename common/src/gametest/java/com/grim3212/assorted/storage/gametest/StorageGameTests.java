package com.grim3212.assorted.storage.gametest;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.api.StorageAccessUtil;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.Wood;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateCompactingBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateControllerBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.LockedEnderChestBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.WoodCabinetBlockEntity;
import com.grim3212.assorted.storage.common.crafting.BagColoringRecipe;
import com.grim3212.assorted.storage.common.inventory.crates.CompactingCrateInventory;
import com.grim3212.assorted.storage.common.inventory.crates.CrateSidedInv;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for AssortedStorage.
 * <p>
 * The bodies live in common because the behaviour they check is common; each loader module only
 * registers them into {@code Registries.TEST_FUNCTION} through its own hook, and
 * {@code data/assortedstorage/test_instance/*.json} pairs each one with the shared
 * {@code test_box} structure.
 * <p>
 * Manual checks that need a human are in {@code TESTING-CHECKLIST.md}.
 */
public final class StorageGameTests {

    private StorageGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("bag_dyes_and_reads_back_colour", StorageGameTests::bagDyesAndReadsBackColour);
        out.accept("lock_and_key_share_a_code", StorageGameTests::lockAndKeyShareACode);
        out.accept("storage_block_drops_contents_and_lock", StorageGameTests::storageBlockDropsContentsAndLock);
        out.accept("storage_block_survives_save_load", StorageGameTests::storageBlockSurvivesSaveLoad);
        out.accept("hoppers_feed_and_empty_cabinet", StorageGameTests::hoppersFeedAndEmptyCabinet);
        out.accept("crates_join_through_a_bridge", StorageGameTests::cratesJoinThroughABridge);
        out.accept("crate_upgrades_apply_and_read_back", StorageGameTests::crateUpgradesApplyAndReadBack);
        out.accept("compacting_crate_compacts_up_a_tier", StorageGameTests::compactingCrateCompactsUpATier);
        out.accept("locked_ender_chests_share_by_code", StorageGameTests::lockedEnderChestsShareByCode);
    }

    private static final BlockPos BLOCK = new BlockPos(4, 1, 4);
    private static final String CODE = "3212";

    /**
     * A dye on each side of the bag paints both halves. Which half a dye lands on is decided by its
     * column relative to the bag's, and the colours are read back off the stack's CUSTOM_DATA -
     * bags lost their free form stack NBT in the port.
     */
    private static void bagDyesAndReadsBackColour(GameTestHelper helper) {
        ItemStack bag = new ItemStack(StorageItems.BAG.get());
        ItemStack red = new ItemStack(Items.DYE.pick(DyeColor.RED));
        ItemStack blue = new ItemStack(Items.DYE.pick(DyeColor.BLUE));

        CraftingInput input = CraftingInput.of(3, 1, List.of(red, bag, blue));
        helper.assertTrue(BagColoringRecipe.INSTANCE.matches(input, helper.getLevel()), "dye + bag + dye was not a bag colouring recipe");

        ItemStack dyed = BagColoringRecipe.INSTANCE.assemble(input);
        helper.assertValueEqual(NBTHelper.getInt(dyed, BagItem.TAG_SECONDARY_COLOR, -1), DyeColor.RED.getId(), "secondary colour from the dye left of the bag");
        helper.assertValueEqual(NBTHelper.getInt(dyed, BagItem.TAG_PRIMARY_COLOR, -1), DyeColor.BLUE.getId(), "primary colour from the dye right of the bag");

        // The name is derived from the primary colour, and it is per stack now that
        // getDescriptionId lost its stack overload.
        Component name = StorageItems.BAG.get().getName(dyed);
        helper.assertTrue(name.getContents() instanceof TranslatableContents contents && contents.getKey().endsWith("_blue"),
                "a dyed bag did not take its name from the primary colour");

        helper.assertValueEqual(NBTHelper.getInt(bag, BagItem.TAG_PRIMARY_COLOR, -1), -1, "an undyed bag carried a colour");
        helper.succeed();
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
     * Breaking a storage block spills its contents and its lock. This is the regression the
     * onRemove / preRemoveSideEffects split caused elsewhere in the workspace.
     */
    private static void storageBlockDropsContentsAndLock(GameTestHelper helper) {
        helper.setBlock(BLOCK, StorageBlocks.WOOD_CABINET.get());
        WoodCabinetBlockEntity cabinet = helper.getBlockEntity(BLOCK, WoodCabinetBlockEntity.class);
        cabinet.getItemStackStorageHandler().setStackInSlot(0, new ItemStack(Items.DIAMOND, 5));
        cabinet.setLockCode(CODE);

        helper.destroyBlock(BLOCK);
        helper.succeedWhen(() -> {
            helper.assertItemEntityPresent(Items.DIAMOND, BLOCK, 3.0D);
            helper.assertItemEntityPresent(StorageItems.LOCKSMITH_LOCK.get(), BLOCK, 3.0D);

            // The dropped lock has to keep the code, otherwise the block can never be reopened.
            boolean codedLock = helper.getEntities(EntityTypes.ITEM).stream()
                    .map(entity -> entity.getItem())
                    .anyMatch(stack -> stack.is(StorageItems.LOCKSMITH_LOCK.get()) && CODE.equals(StorageUtil.getCode(stack)));
            helper.assertTrue(codedLock, "the dropped padlock lost its code");
        });
    }

    /** Contents and lock survive the chunk save/load round trip block entity state moved onto. */
    private static void storageBlockSurvivesSaveLoad(GameTestHelper helper) {
        helper.setBlock(BLOCK, StorageBlocks.WOOD_CABINET.get());
        WoodCabinetBlockEntity cabinet = helper.getBlockEntity(BLOCK, WoodCabinetBlockEntity.class);
        cabinet.getItemStackStorageHandler().setStackInSlot(0, new ItemStack(Items.DIAMOND, 5));
        cabinet.getItemStackStorageHandler().setStackInSlot(26, new ItemStack(Items.EMERALD, 2));
        cabinet.setLockCode(CODE);

        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        CompoundTag saved = cabinet.saveWithFullMetadata(registries);
        BlockEntity reloaded = BlockEntity.loadStatic(helper.absolutePos(BLOCK), cabinet.getBlockState(), saved, registries);

        helper.assertTrue(reloaded instanceof WoodCabinetBlockEntity, "a saved wood cabinet did not load back as one");
        WoodCabinetBlockEntity loaded = (WoodCabinetBlockEntity) reloaded;
        helper.assertValueEqual(loaded.getLockCode(), CODE, "the lock code across a save/load");

        ItemStack first = loaded.getItemStackStorageHandler().getStackInSlot(0);
        helper.assertTrue(first.is(Items.DIAMOND), "slot 0 lost its item across a save/load");
        helper.assertValueEqual(first.getCount(), 5, "slot 0 stack size across a save/load");
        helper.assertTrue(loaded.getItemStackStorageHandler().getStackInSlot(26).is(Items.EMERALD), "the last slot lost its item across a save/load");
        helper.succeed();
    }

    /**
     * A hopper above pushes in, a hopper below pulls out. This is the sided inventory bridge -
     * {@code ResourceHandler} on NeoForge, the transfer API on Fabric - and it is the part of the
     * port most likely to differ between the two.
     */
    private static void hoppersFeedAndEmptyCabinet(GameTestHelper helper) {
        BlockPos cabinetPos = new BlockPos(4, 2, 4);
        BlockPos above = cabinetPos.above();
        BlockPos below = cabinetPos.below();

        helper.setBlock(cabinetPos, StorageBlocks.WOOD_CABINET.get());
        helper.setBlock(above, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
        helper.setBlock(below, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));

        helper.getBlockEntity(above, HopperBlockEntity.class).setItem(0, new ItemStack(Items.DIAMOND, 1));
        helper.getBlockEntity(cabinetPos, WoodCabinetBlockEntity.class)
                .getItemStackStorageHandler().setStackInSlot(26, new ItemStack(Items.EMERALD, 1));

        helper.succeedWhen(() -> {
            helper.assertContainerEmpty(above);
            helper.assertContainerContains(below, Items.EMERALD);
        });
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

    private static LockedEnderChestBlockEntity enderChest(GameTestHelper helper, BlockPos pos, String code) {
        helper.setBlock(pos, StorageBlocks.LOCKED_ENDER_CHEST.get());
        LockedEnderChestBlockEntity chest = helper.getBlockEntity(pos, LockedEnderChestBlockEntity.class);
        chest.setLockCode(code);
        // The saved-data inventory is only bound when the platform handler is first asked for.
        chest.getStorageHandler();
        return chest;
    }

    private static Block oakCrate() {
        return StorageBlocks.CRATES.stream().filter(group -> group.getType() == Wood.OAK).findFirst().orElseThrow().SINGLE.get();
    }
}
