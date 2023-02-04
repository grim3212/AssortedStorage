package com.grim3212.assorted.storage.common.creative;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.handler.StorageConfig;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.NBTHelper;
import com.grim3212.assorted.storage.common.util.StorageMaterial;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class StorageCreativeTab {

	public static void registerTabs(final CreativeModeTabEvent.Register event) {
		event.registerCreativeModeTab(new ResourceLocation(AssortedStorage.MODID, "tab"), builder -> builder.title(Component.translatable("itemGroup.assortedstorage")).icon(() -> new ItemStack(StorageBlocks.WOOD_CABINET.get())).displayItems((enabledFlags, populator, hasPermissions) -> {
			populator.accept(StorageItems.LOCKSMITH_KEY.get());
			populator.accept(StorageItems.LOCKSMITH_LOCK.get());
			populator.accept(StorageItems.KEY_RING.get());
			populator.accept(StorageItems.ROTATOR_MAJIG.get());

			if (StorageConfig.COMMON.storageCratesEnabled.get()) {
				populator.accept(StorageBlocks.STORAGE_CRATE.get());
				populator.accept(StorageBlocks.STORAGE_CRATE_DOUBLE.get());
				populator.accept(StorageBlocks.STORAGE_CRATE_TRIPLE.get());
				populator.accept(StorageBlocks.STORAGE_CRATE_QUADRUPLE.get());

				populator.accept(StorageBlocks.STORAGE_CRATE_COMPACTING.get());
			}

			if (StorageConfig.COMMON.upgradesEnabled.get()) {
				populator.accept(StorageItems.BLANK_UPGRADE.get());
				populator.accept(StorageItems.VOID_UPGRADE.get());
				populator.accept(StorageItems.REDSTONE_UPGRADE.get());
				populator.accept(StorageItems.AMOUNT_UPGRADE.get());
				populator.accept(StorageItems.GLOW_UPGRADE.get());
			}

			if (StorageConfig.COMMON.bagsEnabled.get()) {
				populator.accept(StorageItems.ENDER_BAG.get());
				populator.accept(StorageItems.BAG.get());

				for (DyeColor color : DyeColor.values()) {
					populator.accept(NBTHelper.putIntItemStack(new ItemStack(StorageItems.BAG.get()), BagItem.TAG_PRIMARY_COLOR, color.getId()));
				}
			}

			if (StorageConfig.COMMON.bagsEnabled.get()) {
				StorageItems.BAGS.forEach((mat, bag) -> {
					if (canNotCraft(mat)) {
						return;
					}

					populator.accept(bag.get());
				});
			}

			if (StorageConfig.COMMON.upgradesEnabled.get()) {
				StorageItems.LEVEL_UPGRADES.forEach((mat, upgrade) -> {
					if (canNotCraft(mat)) {
						return;
					}

					populator.accept(upgrade.get());
				});
			}

			// Blocks
			populator.accept(StorageBlocks.LOCKSMITH_WORKBENCH.get());
			populator.accept(StorageBlocks.WOOD_CABINET.get());
			populator.accept(StorageBlocks.GLASS_CABINET.get());
			populator.accept(StorageBlocks.GOLD_SAFE.get());
			populator.accept(StorageBlocks.OBSIDIAN_SAFE.get());
			populator.accept(StorageBlocks.LOCKER.get());
			populator.accept(StorageBlocks.ITEM_TOWER.get());
			populator.accept(StorageBlocks.OAK_WAREHOUSE_CRATE.get());
			populator.accept(StorageBlocks.BIRCH_WAREHOUSE_CRATE.get());
			populator.accept(StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get());
			populator.accept(StorageBlocks.ACACIA_WAREHOUSE_CRATE.get());
			populator.accept(StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get());
			populator.accept(StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get());
			populator.accept(StorageBlocks.WARPED_WAREHOUSE_CRATE.get());
			populator.accept(StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get());
			populator.accept(StorageBlocks.MANGROVE_WAREHOUSE_CRATE.get());

			populator.accept(defaultLock(StorageBlocks.LOCKED_ENDER_CHEST.get()));

			if (StorageConfig.COMMON.chestsEnabled.get()) {
				populator.accept(defaultLock(StorageBlocks.LOCKED_CHEST.get()));
			}

			if (StorageConfig.COMMON.shulkersEnabled.get()) {
				populator.accept(defaultLock(StorageBlocks.LOCKED_SHULKER_BOX.get()));
			}

			if (StorageConfig.COMMON.barrelsEnabled.get()) {
				populator.accept(defaultLock(StorageBlocks.LOCKED_BARREL.get()));
			}

			if (StorageConfig.COMMON.hoppersEnabled.get()) {
				populator.accept(defaultLock(StorageBlocks.LOCKED_HOPPER.get()));
			}

			if (StorageConfig.COMMON.chestsEnabled.get()) {
				StorageBlocks.CHESTS.forEach((mat, chest) -> {

					if (canNotCraft(mat)) {
						return;
					}

					populator.accept(chest.get());
				});
			}

			if (StorageConfig.COMMON.shulkersEnabled.get()) {
				StorageBlocks.SHULKERS.forEach((mat, shulker) -> {

					if (canNotCraft(mat)) {
						return;
					}

					populator.accept(shulker.get());
				});
			}

			if (StorageConfig.COMMON.barrelsEnabled.get()) {
				StorageBlocks.BARRELS.forEach((mat, barrel) -> {

					if (canNotCraft(mat)) {
						return;
					}

					populator.accept(barrel.get());
				});
			}

			if (StorageConfig.COMMON.hoppersEnabled.get()) {
				StorageBlocks.HOPPERS.forEach((mat, hopper) -> {

					if (canNotCraft(mat)) {
						return;
					}

					populator.accept(hopper.get());
				});
			}
		}));
	}

	private static ItemStack defaultLock(ItemLike item) {
		return StorageUtil.setCodeOnStack("default", new ItemStack(item));
	}

	private static boolean canNotCraft(StorageMaterial mat) {
		return StorageConfig.COMMON.hideUncraftableItems.get() && ForgeRegistries.ITEMS.tags().getTag(mat.getMaterial()).size() <= 0;
	}
}
