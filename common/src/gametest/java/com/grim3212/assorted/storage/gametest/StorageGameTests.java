package com.grim3212.assorted.storage.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.MinecraftServer;
import com.grim3212.assorted.lib.platform.Services;
import java.io.BufferedReader;
import java.io.IOException;
import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.api.LockerHalf;
import com.grim3212.assorted.storage.api.StorageAccessUtil;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.Wood;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.LockerBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.BaseLockedBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.BaseStorageBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateCompactingBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateControllerBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.LockedEnderChestBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.LockerBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.block.blockentity.WoodCabinetBlockEntity;
import com.grim3212.assorted.storage.common.crafting.BagColoringRecipe;
import com.grim3212.assorted.storage.common.handlers.StorageCreativeItems;
import com.grim3212.assorted.storage.common.inventory.LocksmithWorkbenchContainer;
import com.grim3212.assorted.storage.common.inventory.bag.BagItemHandler;
import com.grim3212.assorted.storage.common.inventory.crates.CompactingCrateInventory;
import com.grim3212.assorted.storage.common.inventory.crates.CrateSidedInv;
import com.grim3212.assorted.storage.common.inventory.enderbag.EnderBagContainer;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingItemHandler;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.network.SetLockPacket;
import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        out.accept("every_storage_block_holds_items", StorageGameTests::everyStorageBlockHoldsItems);
        out.accept("double_locker_shares_one_inventory", StorageGameTests::doubleLockerSharesOneInventory);
        out.accept("every_storage_block_drops_its_contents", StorageGameTests::everyStorageBlockDropsItsContents);
        out.accept("every_storage_block_survives_save_load", StorageGameTests::everyStorageBlockSurvivesSaveLoad);
        out.accept("bag_holds_items_across_save_load", StorageGameTests::bagHoldsItemsAcrossSaveLoad);
        out.accept("ender_bag_opens_the_players_ender_chest", StorageGameTests::enderBagOpensThePlayersEnderChest);
        out.accept("key_ring_holds_keys_and_opens_a_lock", StorageGameTests::keyRingHoldsKeysAndOpensALock);
        out.accept("locked_doors_open_only_with_the_right_key", StorageGameTests::lockedDoorsOpenOnlyWithTheRightKey);
        out.accept("locksmith_workbench_codes_a_key_and_a_lock", StorageGameTests::locksmithWorkbenchCodesAKeyAndALock);
        out.accept("redstone_upgrade_emits_a_signal", StorageGameTests::redstoneUpgradeEmitsASignal);
        out.accept("rotator_majig_rotates_a_block", StorageGameTests::rotatorMajigRotatesABlock);
        out.accept("every_block_and_item_has_a_model_and_a_name", StorageGameTests::everyBlockAndItemHasAModelAndAName);
        out.accept("every_recipe_loads_or_is_conditioned_off", StorageGameTests::everyRecipeLoadsOrIsConditionedOff);
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

    /**
     * Every storage block takes items in and hands them back, through the platform handler the
     * loaders bridge onto - the same object a hopper or a pipe sees, not the raw list behind it.
     * Driven off the block list so a new storage block is covered the day it is registered.
     */
    private static void everyStorageBlockHoldsItems(GameTestHelper helper) {
        List<Block> blocks = storageBlocks();

        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            BlockPos pos = spread(i);
            helper.setBlock(pos, block);

            BaseStorageBlockEntity storage = helper.getBlockEntity(pos, BaseStorageBlockEntity.class);
            IItemStorageHandler handler = storage.getStorageHandler().getItemStorageHandler(null);
            int last = handler.getSlots() - 1;

            helper.assertTrue(last > 0, name(block) + " has no slots to hold anything");
            helper.assertTrue(handler.insertItem(0, new ItemStack(Items.DIAMOND, 5), false).isEmpty(), name(block) + " refused an item in its first slot");
            helper.assertTrue(handler.insertItem(last, new ItemStack(Items.EMERALD, 2), false).isEmpty(), name(block) + " refused an item in its last slot");

            helper.assertTrue(handler.getStackInSlot(0).is(Items.DIAMOND), name(block) + " did not hold what went into its first slot");
            helper.assertValueEqual(handler.getStackInSlot(0).getCount(), 5, name(block) + " first slot stack size");
            helper.assertTrue(handler.getStackInSlot(last).is(Items.EMERALD), name(block) + " did not hold what went into its last slot");

            // The platform handler has to be a view of the block entity's own inventory, not a copy.
            helper.assertTrue(storage.getItemStackStorageHandler().getStackInSlot(0).is(Items.DIAMOND), name(block) + " kept the item somewhere other than its own inventory");
        }

        helper.succeed();
    }

    /**
     * Two lockers stacked are one 90 slot inventory: the top half is slots 0-44 and the bottom
     * 45-89. The halves are set explicitly because {@code setBlock} skips the placement path that
     * would pair them.
     */
    private static void doubleLockerSharesOneInventory(GameTestHelper helper) {
        BlockPos bottom = new BlockPos(4, 1, 4);
        BlockPos top = bottom.above();

        helper.setBlock(bottom, StorageBlocks.LOCKER.get().defaultBlockState().setValue(LockerBlock.HALF, LockerHalf.BOTTOM));
        helper.setBlock(top, StorageBlocks.LOCKER.get().defaultBlockState().setValue(LockerBlock.HALF, LockerHalf.TOP));
        helper.assertBlockProperty(bottom, LockerBlock.HALF, LockerHalf.BOTTOM);
        helper.assertBlockProperty(top, LockerBlock.HALF, LockerHalf.TOP);

        LockerBlockEntity lower = helper.getBlockEntity(bottom, LockerBlockEntity.class);
        LockerBlockEntity upper = helper.getBlockEntity(top, LockerBlockEntity.class);

        IItemStorageHandler joined = lower.getStorageHandler().getItemStorageHandler(null);
        helper.assertValueEqual(joined.getSlots(), 90, "a double locker's joined slot count");

        helper.assertTrue(joined.insertItem(0, new ItemStack(Items.DIAMOND, 1), false).isEmpty(), "the double locker refused an item in its first slot");
        helper.assertTrue(joined.insertItem(89, new ItemStack(Items.EMERALD, 1), false).isEmpty(), "the double locker refused an item in its last slot");

        helper.assertTrue(upper.getItemStackStorageHandler().getStackInSlot(0).is(Items.DIAMOND), "the first joined slot did not land in the top half");
        helper.assertTrue(lower.getItemStackStorageHandler().getStackInSlot(44).is(Items.EMERALD), "the last joined slot did not land in the bottom half");
        helper.succeed();
    }

    /**
     * Breaking a storage block gives the contents back. Most of them spill loose from the block
     * entity's {@code preRemoveSideEffects}; the gold safe and the locked shulker box deliberately
     * do not, and carry their contents inside the dropped item instead, so those two are broken
     * with drops on and the dropped item is inspected.
     * <p>
     * Each block is given a different marker item so one dropped stack can never be mistaken for
     * a neighbour's.
     */
    private static void everyStorageBlockDropsItsContents(GameTestHelper helper) {
        List<Block> keepsContentsInTheItem = List.of(StorageBlocks.GOLD_SAFE.get(), StorageBlocks.LOCKED_SHULKER_BOX.get());
        List<Block> blocks = storageBlocks();
        List<Item> spilled = new ArrayList<>();

        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            BlockPos pos = spread(i);
            Item marker = MARKERS.get(i);

            helper.setBlock(pos, block);
            helper.getBlockEntity(pos, BaseStorageBlockEntity.class).getItemStackStorageHandler().setStackInSlot(0, new ItemStack(marker, 3));

            if (keepsContentsInTheItem.contains(block)) {
                // Needs the loot table to run, which helper.destroyBlock never reaches.
                helper.getLevel().destroyBlock(helper.absolutePos(pos), true);
            } else {
                helper.destroyBlock(pos);
                spilled.add(marker);
            }
        }

        helper.succeedWhen(() -> {
            for (Item marker : spilled) {
                helper.assertItemEntityPresent(marker);
            }

            for (Block block : keepsContentsInTheItem) {
                Item marker = MARKERS.get(blocks.indexOf(block));
                helper.assertTrue(droppedItemCarries(helper, block.asItem(), marker), name(block) + " dropped without the contents it is supposed to carry");
            }
        });
    }

    /**
     * Contents and lock code survive the chunk save/load round trip block entity state moved onto,
     * for every storage block rather than the one the first pass covered.
     */
    private static void everyStorageBlockSurvivesSaveLoad(GameTestHelper helper) {
        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        List<Block> blocks = storageBlocks();

        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            BlockPos pos = spread(i);
            helper.setBlock(pos, block);

            BaseStorageBlockEntity storage = helper.getBlockEntity(pos, BaseStorageBlockEntity.class);
            int last = storage.getItemStackStorageHandler().getSlots() - 1;
            storage.getItemStackStorageHandler().setStackInSlot(0, new ItemStack(Items.DIAMOND, 5));
            storage.getItemStackStorageHandler().setStackInSlot(last, new ItemStack(Items.EMERALD, 2));
            storage.setLockCode(CODE);

            CompoundTag saved = storage.saveWithFullMetadata(registries);
            BlockEntity reloaded = BlockEntity.loadStatic(helper.absolutePos(pos), storage.getBlockState(), saved, registries);

            helper.assertTrue(reloaded instanceof BaseStorageBlockEntity, name(block) + " did not load back as a storage block entity");
            BaseStorageBlockEntity loaded = (BaseStorageBlockEntity) reloaded;

            helper.assertValueEqual(loaded.getLockCode(), CODE, name(block) + " lost its lock code across a save/load");
            helper.assertTrue(loaded.getItemStackStorageHandler().getStackInSlot(0).is(Items.DIAMOND), name(block) + " lost its first slot across a save/load");
            helper.assertValueEqual(loaded.getItemStackStorageHandler().getStackInSlot(0).getCount(), 5, name(block) + " first slot stack size across a save/load");
            helper.assertTrue(loaded.getItemStackStorageHandler().getStackInSlot(last).is(Items.EMERALD), name(block) + " lost its last slot across a save/load");
        }

        helper.succeed();
    }

    /**
     * A bag holds items and keeps them across a save/load. Its contents are a
     * {@code DataComponents.CONTAINER} component now rather than free form stack NBT, so the round
     * trip goes through {@code ItemStack.CODEC} - the same path the stack takes to disk.
     */
    private static void bagHoldsItemsAcrossSaveLoad(GameTestHelper helper) {
        ItemStack bag = new ItemStack(StorageItems.BAG.get());

        // Exactly what BagItem#getStorageHandler builds for the stack.
        BagItemHandler handler = new BagItemHandler(bag, null);
        handler.load();
        helper.assertValueEqual(handler.getSlots(), 28, "a plain bag's slot count, lock slot included");

        int last = handler.getSlots() - 1;
        handler.setStackInSlot(1, new ItemStack(Items.DIAMOND, 5));
        handler.setStackInSlot(last, new ItemStack(Items.EMERALD, 2));

        ItemStack reloaded = saveAndLoad(helper, bag);
        helper.assertTrue(reloaded.is(StorageItems.BAG.get()), "a saved bag did not load back as a bag");

        BagItemHandler after = new BagItemHandler(reloaded, null);
        after.load();
        helper.assertTrue(after.getStackInSlot(1).is(Items.DIAMOND), "the bag lost its first item across a save/load");
        helper.assertValueEqual(after.getStackInSlot(1).getCount(), 5, "the bag's first stack size across a save/load");
        helper.assertTrue(after.getStackInSlot(last).is(Items.EMERALD), "the bag lost its last item across a save/load");
        helper.assertTrue(after.getStackInSlot(0).isEmpty(), "an unlocked bag came back with something in its lock slot");
        helper.succeed();
    }

    /**
     * An ender bag with no code on it opens the player's own vanilla ender chest, rather than one
     * of the mod's code keyed shared inventories. Checked on the container the menu resolves to,
     * which is the decision; the screen on top of it is a manual check.
     */
    private static void enderBagOpensThePlayersEnderChest(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper, new ItemStack(StorageItems.ENDER_BAG.get()));
        helper.assertFalse(StorageUtil.hasCode(player.getMainHandItem()), "a fresh ender bag already carried a code");

        PlayerEnderChestContainer ownChest = player.getEnderChestInventory();
        ownChest.setItem(0, new ItemStack(Items.DIAMOND, 3));

        EnderBagContainer menu = new EnderBagContainer(1, helper.getLevel(), player.blockPosition(), player.getInventory(), player);
        helper.runBeforeTestEnd(() -> menu.removed(player));

        // One padlock slot, the 27 chest slots, then the player's own 36.
        helper.assertValueEqual(menu.slots.size(), 1 + 27 + 36, "the ender bag menu's slot count");
        helper.assertTrue(menu.getSlot(1).container == ownChest, "an uncoded ender bag did not resolve to the player's own ender chest");
        helper.assertTrue(menu.getSlot(1).getItem().is(Items.DIAMOND), "the ender bag did not show what was in the player's ender chest");
        helper.assertValueEqual(menu.getSlot(1).getItem().getCount(), 3, "the stack the ender bag showed from the player's ender chest");
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
     * The whole locksmith loop, server side: iron crafts into blank keys and locks, a code typed
     * into the workbench is put on whichever of the two is in the slot, and the lock and key that
     * come out share it - the lock closes a cabinet the key then opens.
     * <p>
     * The typed code arrives as {@link SetLockPacket}, so the packet's own handler is what is
     * called here rather than the screen that would normally send it.
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

    /** The rotator majig turns whatever it is used on a quarter turn clockwise. */
    private static void rotatorMajigRotatesABlock(GameTestHelper helper) {
        helper.setBlock(BLOCK, Blocks.FURNACE.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));

        ServerPlayer player = survivalPlayer(helper, new ItemStack(StorageItems.ROTATOR_MAJIG.get()));
        BlockPos pos = helper.absolutePos(BLOCK);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos).add(0.0D, 0.5D, 0.0D), Direction.UP, pos, false);
        InteractionResult result = player.getItemInHand(InteractionHand.MAIN_HAND).useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));

        helper.assertTrue(result.consumesAction(), "the rotator majig did not act on the block");
        helper.assertBlockProperty(BLOCK, HorizontalDirectionalBlock.FACING, Direction.EAST);
        helper.succeed();
    }

    /**
     * Every block and item this mod registers has a model and a name.
     * <p>
     * Both are generated - blockstates and item models by the NeoForge client datagen, the lang
     * file by hand since this mod's language provider did not survive the port - and neither shows
     * up as a compile error, so a missing one is only ever found by looking. Every gap is reported
     * at once, because finding them one run at a time is unbearable.
     */
    private static void everyBlockAndItemHasAModelAndAName(GameTestHelper helper) {
        JsonObject lang = lang(helper);
        List<String> missing = new ArrayList<>();

        for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }

            if (!resourceExists("/assets/" + Constants.MOD_ID + "/blockstates/" + id.getPath() + ".json")) {
                missing.add("blockstate " + id.getPath());
            }
            if (!lang.has(entry.getValue().getDescriptionId())) {
                missing.add("lang key " + entry.getValue().getDescriptionId());
            }
        }

        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }

            if (!resourceExists("/assets/" + Constants.MOD_ID + "/items/" + id.getPath() + ".json")) {
                missing.add("item model " + id.getPath());
            }
            if (!lang.has(entry.getValue().getDescriptionId())) {
                missing.add("lang key " + entry.getValue().getDescriptionId());
            }
        }

        helper.assertTrue(BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(StorageCreativeItems.CREATIVE_TAB_KEY), "the Assorted Storage creative tab is not registered");
        helper.assertTrue(lang.has("itemGroup." + Constants.MOD_ID), "the Assorted Storage creative tab has no name");

        helper.assertTrue(missing.isEmpty(), missing.size() + " missing assets: " + String.join(", ", missing));
        helper.succeed();
    }

    /** Every storage block whose block entity keeps an inventory of its own. */
    private static List<Block> storageBlocks() {
        List<Block> blocks = new ArrayList<>();
        blocks.add(StorageBlocks.LOCKER.get());
        blocks.add(StorageBlocks.WOOD_CABINET.get());
        blocks.add(StorageBlocks.GLASS_CABINET.get());
        blocks.add(StorageBlocks.GOLD_SAFE.get());
        blocks.add(StorageBlocks.OBSIDIAN_SAFE.get());
        blocks.add(StorageBlocks.ITEM_TOWER.get());
        blocks.add(StorageBlocks.LOCKED_CHEST.get());
        blocks.add(StorageBlocks.LOCKED_SHULKER_BOX.get());
        Collections.addAll(blocks, StorageBlockEntityTypes.getWarehouseCrates());
        return blocks;
    }

    /**
     * One marker item per block under test, so a dropped stack can only have come from one of
     * them. Long enough for every position {@link #spread(int)} hands out.
     */
    private static final List<Item> MARKERS = List.of(
            Items.DIAMOND, Items.EMERALD, Items.GOLD_INGOT, Items.IRON_INGOT, Items.COPPER_INGOT,
            Items.COAL, Items.REDSTONE, Items.LAPIS_LAZULI, Items.QUARTZ, Items.AMETHYST_SHARD,
            Items.ECHO_SHARD, Items.FLINT, Items.BONE, Items.STRING, Items.PAPER,
            Items.BRICK, Items.CLAY_BALL, Items.SUGAR, Items.APPLE, Items.FEATHER,
            Items.WHEAT, Items.LEATHER, Items.SLIME_BALL, Items.GLOWSTONE_DUST, Items.BLAZE_ROD);

    /** Positions two apart on the test box floor, so nothing a block drops lands on a neighbour. */
    private static BlockPos spread(int index) {
        return new BlockPos((index % 5) * 2, 1, (index / 5) * 2);
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    /** Right clicks the north face of a door with whatever the player is carrying. */
    private static InteractionResult knock(GameTestHelper helper, Player player, BlockPos door) {
        BlockPos pos = helper.absolutePos(door);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, false);
        BlockState state = helper.getLevel().getBlockState(pos);
        return state.useItemOn(player.getItemInHand(InteractionHand.MAIN_HAND), helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
    }

    /** The result of the one crafting recipe that matches {@code input}. */
    private static ItemStack crafted(GameTestHelper helper, CraftingInput input) {
        var found = helper.getLevel().recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel());
        helper.assertTrue(found.isPresent(), "no crafting recipe matched the ingredients laid out");
        return found.get().value().assemble(input);
    }

    /** Puts {@code blank} in the workbench, types {@link #CODE} at it, and takes the result. */
    private static ItemStack typeCode(GameTestHelper helper, ServerPlayer player, ItemStack blank) {
        LocksmithWorkbenchContainer menu = (LocksmithWorkbenchContainer) player.containerMenu;
        menu.getSlot(0).set(blank);

        SetLockPacket.handle(new SetLockPacket(CODE), player);
        ItemStack result = menu.getSlot(1).getItem();
        helper.assertFalse(result.isEmpty(), "the workbench made nothing from a typed code");

        menu.getSlot(0).set(ItemStack.EMPTY);
        return result;
    }

    /** The stack, through the codec it goes to disk with. */
    private static ItemStack saveAndLoad(GameTestHelper helper, ItemStack stack) {
        RegistryOps<Tag> ops = helper.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE);
        Tag saved = ItemStack.CODEC.encodeStart(ops, stack).getOrThrow();
        return ItemStack.CODEC.parse(ops, saved).getOrThrow();
    }

    /** Whether a dropped {@code blockItem} is carrying {@code content} in its CONTAINER component. */
    private static boolean droppedItemCarries(GameTestHelper helper, Item blockItem, Item content) {
        return helper.getEntities(EntityTypes.ITEM).stream()
                .map(entity -> entity.getItem())
                .filter(stack -> stack.is(blockItem))
                .anyMatch(stack -> {
                    NonNullList<ItemStack> contents = NonNullList.withSize(27, ItemStack.EMPTY);
                    stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(contents);
                    return contents.stream().anyMatch(held -> held.is(content));
                });
    }

    /** The mod's own en_us.json, off the classpath - it is a resource even on a headless server. */
    private static JsonObject lang(GameTestHelper helper) {
        try (InputStream in = StorageGameTests.class.getResourceAsStream("/assets/" + Constants.MOD_ID + "/lang/en_us.json")) {
            helper.assertTrue(in != null, "this mod ships no en_us.json");
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            throw helper.assertionException("could not read en_us.json: " + e);
        }
    }

    private static boolean resourceExists(String path) {
        try (InputStream in = StorageGameTests.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * A player really in the level, in survival. {@code makeMockServerPlayerInLevel} is deprecated
     * for removal and hard codes creative; {@code makeMockServerPlayer} is never placed, so it has
     * no connection and anything sent to it throws.
     */
    private static ServerPlayer survivalPlayer(GameTestHelper helper, ItemStack held) {
        ServerLevel level = helper.getLevel();
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "assortedstorage-test"), false);
        ServerPlayer player = new ServerPlayer(level.getServer(), level, cookie.gameProfile(), cookie.clientInformation());

        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        level.getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
        helper.runBeforeTestEnd(() -> level.getServer().getPlayerList().remove(player));

        player.setGameMode(GameType.SURVIVAL);
        helper.assertFalse(player.isCreative(), "the test player is in creative, which changes every path under test");
        player.setItemInHand(InteractionHand.MAIN_HAND, held);
        return player;
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
    /**
     * Every recipe file this mod ships either loaded, or carries this loader's load conditions and was
     * skipped by them. A file with neither failed to parse. On Fabric that was every conditional
     * recipe for a while: Fabric's datagen wrote them without conditions, and the NeoForge copy that
     * shadowed it carries a key Fabric ignores - so only this loader's own key counts.
     */
    private static void everyRecipeLoadsOrIsConditionedOff(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        FileToIdConverter recipes = FileToIdConverter.json("recipe");
        String conditionsKey = Services.PLATFORM.getPlatformName().equals("Fabric") ? "fabric:load_conditions" : "neoforge:conditions";
        List<String> failed = new ArrayList<>();

        recipes.listMatchingResources(server.getResourceManager()).forEach((file, resource) -> {
            Identifier id = recipes.fileToId(file);
            if (!id.getNamespace().equals(Constants.MOD_ID) || server.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, id)).isPresent()) {
                return;
            }

            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                if (!json.has(conditionsKey)) {
                    failed.add(id.toString());
                }
            } catch (IOException e) {
                failed.add(id + " (" + e.getMessage() + ")");
            }
        });

        helper.assertTrue(failed.isEmpty(), failed.size() + " recipes failed to load without being conditioned off: " + String.join(", ", failed.subList(0, Math.min(10, failed.size()))));
        helper.succeed();
    }
}
