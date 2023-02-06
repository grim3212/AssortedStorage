package com.grim3212.assorted.storage.common.data;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.item.upgrades.LevelUpgradeItem;
import com.grim3212.assorted.storage.common.util.StorageMaterial;
import com.grim3212.assorted.storage.common.util.StorageTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class StorageItemTagProvider extends ItemTagsProvider {

	public StorageItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup, TagsProvider<Block> blockTags, ExistingFileHelper existingFileHelper) {
		super(packOutput, lookup, blockTags, AssortedStorage.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		this.tag(ItemTags.PIGLIN_LOVED).add(StorageBlocks.GOLD_SAFE.get().asItem(), StorageBlocks.CHESTS.get(StorageMaterial.GOLD).get().asItem(), StorageBlocks.BARRELS.get(StorageMaterial.GOLD).get().asItem(), StorageBlocks.HOPPERS.get(StorageMaterial.GOLD).get().asItem(), StorageBlocks.SHULKERS.get(StorageMaterial.GOLD).get().asItem(), StorageItems.LEVEL_UPGRADES.get(StorageMaterial.GOLD).get().asItem());

		this.tag(Tags.Items.CHESTS_ENDER).add(StorageBlocks.LOCKED_ENDER_CHEST.get().asItem());
		this.tag(Tags.Items.CHESTS_WOODEN).add(StorageBlocks.LOCKED_CHEST.get().asItem());
		this.tag(Tags.Items.BARRELS_WOODEN).add(StorageBlocks.LOCKED_BARREL.get().asItem());

		this.copy(Tags.Blocks.CHESTS, Tags.Items.CHESTS);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_0, StorageTags.Items.CHESTS_LEVEL_0);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_1, StorageTags.Items.CHESTS_LEVEL_1);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_2, StorageTags.Items.CHESTS_LEVEL_2);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_3, StorageTags.Items.CHESTS_LEVEL_3);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_4, StorageTags.Items.CHESTS_LEVEL_4);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_5, StorageTags.Items.CHESTS_LEVEL_5);

		this.tag(StorageTags.Items.CAN_UPGRADE_LEVEL_0).addTag(Tags.Items.CHESTS_WOODEN);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_0, StorageTags.Items.CAN_UPGRADE_LEVEL_1);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_1, StorageTags.Items.CAN_UPGRADE_LEVEL_2);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_2, StorageTags.Items.CAN_UPGRADE_LEVEL_3);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_3, StorageTags.Items.CAN_UPGRADE_LEVEL_4);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_4, StorageTags.Items.CAN_UPGRADE_LEVEL_5);

		this.copy(Tags.Blocks.BARRELS, Tags.Items.BARRELS);
		this.copy(StorageTags.Blocks.BARRELS_LEVEL_0, StorageTags.Items.BARRELS_LEVEL_0);
		this.copy(StorageTags.Blocks.BARRELS_LEVEL_1, StorageTags.Items.BARRELS_LEVEL_1);
		this.copy(StorageTags.Blocks.BARRELS_LEVEL_2, StorageTags.Items.BARRELS_LEVEL_2);
		this.copy(StorageTags.Blocks.BARRELS_LEVEL_3, StorageTags.Items.BARRELS_LEVEL_3);
		this.copy(StorageTags.Blocks.BARRELS_LEVEL_4, StorageTags.Items.BARRELS_LEVEL_4);
		this.copy(StorageTags.Blocks.BARRELS_LEVEL_5, StorageTags.Items.BARRELS_LEVEL_5);

		this.tag(StorageTags.Items.CAN_UPGRADE_LEVEL_0).addTag(Tags.Items.BARRELS_WOODEN);
		this.copy(StorageTags.Blocks.BARRELS_LEVEL_0, StorageTags.Items.CAN_UPGRADE_LEVEL_1);
		this.copy(StorageTags.Blocks.BARRELS_LEVEL_1, StorageTags.Items.CAN_UPGRADE_LEVEL_2);
		this.copy(StorageTags.Blocks.BARRELS_LEVEL_2, StorageTags.Items.CAN_UPGRADE_LEVEL_3);
		this.copy(StorageTags.Blocks.BARRELS_LEVEL_3, StorageTags.Items.CAN_UPGRADE_LEVEL_4);
		this.copy(StorageTags.Blocks.BARRELS_LEVEL_4, StorageTags.Items.CAN_UPGRADE_LEVEL_5);

		this.copy(StorageTags.Blocks.HOPPERS, StorageTags.Items.HOPPERS);
		this.copy(StorageTags.Blocks.HOPPERS_LEVEL_0, StorageTags.Items.HOPPERS_LEVEL_0);
		this.copy(StorageTags.Blocks.HOPPERS_LEVEL_1, StorageTags.Items.HOPPERS_LEVEL_1);
		this.copy(StorageTags.Blocks.HOPPERS_LEVEL_2, StorageTags.Items.HOPPERS_LEVEL_2);
		this.copy(StorageTags.Blocks.HOPPERS_LEVEL_3, StorageTags.Items.HOPPERS_LEVEL_3);
		this.copy(StorageTags.Blocks.HOPPERS_LEVEL_4, StorageTags.Items.HOPPERS_LEVEL_4);
		this.copy(StorageTags.Blocks.HOPPERS_LEVEL_5, StorageTags.Items.HOPPERS_LEVEL_5);

		this.tag(StorageTags.Items.CAN_UPGRADE_LEVEL_0).addTag(StorageTags.Items.HOPPERS);
		this.copy(StorageTags.Blocks.HOPPERS_LEVEL_0, StorageTags.Items.CAN_UPGRADE_LEVEL_1);
		this.copy(StorageTags.Blocks.HOPPERS_LEVEL_1, StorageTags.Items.CAN_UPGRADE_LEVEL_2);
		this.copy(StorageTags.Blocks.HOPPERS_LEVEL_2, StorageTags.Items.CAN_UPGRADE_LEVEL_3);
		this.copy(StorageTags.Blocks.HOPPERS_LEVEL_3, StorageTags.Items.CAN_UPGRADE_LEVEL_4);
		this.copy(StorageTags.Blocks.HOPPERS_LEVEL_4, StorageTags.Items.CAN_UPGRADE_LEVEL_5);

		this.copy(StorageTags.Blocks.SHULKERS_NORMAL, StorageTags.Items.SHULKERS_NORMAL);
		this.copy(StorageTags.Blocks.SHULKERS_LEVEL_0, StorageTags.Items.SHULKERS_LEVEL_0);
		this.copy(StorageTags.Blocks.SHULKERS_LEVEL_1, StorageTags.Items.SHULKERS_LEVEL_1);
		this.copy(StorageTags.Blocks.SHULKERS_LEVEL_2, StorageTags.Items.SHULKERS_LEVEL_2);
		this.copy(StorageTags.Blocks.SHULKERS_LEVEL_3, StorageTags.Items.SHULKERS_LEVEL_3);
		this.copy(StorageTags.Blocks.SHULKERS_LEVEL_4, StorageTags.Items.SHULKERS_LEVEL_4);
		this.copy(StorageTags.Blocks.SHULKERS_LEVEL_5, StorageTags.Items.SHULKERS_LEVEL_5);

		this.tag(StorageTags.Items.CAN_UPGRADE_LEVEL_0).addTag(StorageTags.Items.SHULKERS_NORMAL);
		this.copy(StorageTags.Blocks.SHULKERS_LEVEL_0, StorageTags.Items.CAN_UPGRADE_LEVEL_1);
		this.copy(StorageTags.Blocks.SHULKERS_LEVEL_1, StorageTags.Items.CAN_UPGRADE_LEVEL_2);
		this.copy(StorageTags.Blocks.SHULKERS_LEVEL_2, StorageTags.Items.CAN_UPGRADE_LEVEL_3);
		this.copy(StorageTags.Blocks.SHULKERS_LEVEL_3, StorageTags.Items.CAN_UPGRADE_LEVEL_4);
		this.copy(StorageTags.Blocks.SHULKERS_LEVEL_4, StorageTags.Items.CAN_UPGRADE_LEVEL_5);

		this.tag(StorageTags.Items.PAPER).add(Items.PAPER);
		
		// Logic from FunctionalStorage
		//@formatter:off
		this.tag(StorageTags.Items.CRAFTING_OVERRIDE)
			.add(Items.MELON, Items.MELON_SLICE)
			.add(Items.CLAY, Items.CLAY_BALL)
			.add(Items.GLOWSTONE, Items.GLOWSTONE_DUST)
			.add(Items.QUARTZ, Items.QUARTZ_BLOCK)
			.add(Items.ICE, Items.BLUE_ICE, Items.PACKED_ICE)
			.add(Items.AMETHYST_BLOCK, Items.AMETHYST_SHARD)
			.add(Items.SNOWBALL, Items.SNOW_BLOCK)
			.add(Items.BRICKS, Items.BRICK)
			.add(Items.NETHER_BRICK, Items.NETHER_BRICKS)
			.add(Items.NETHER_WART_BLOCK, Items.NETHER_WART)
			.add(Items.SANDSTONE, Items.SAND)
			.add(Items.RED_SANDSTONE, Items.RED_SAND);
		//@formatter:on

		for (Entry<StorageMaterial, RegistryObject<LevelUpgradeItem>> levelUpgrade : StorageItems.LEVEL_UPGRADES.entrySet()) {
			LevelUpgradeItem item = levelUpgrade.getValue().get();

			switch (levelUpgrade.getKey().getStorageLevel()) {
				case 1:
					this.tag(StorageTags.Items.STORAGE_LEVEL_1_UPGRADES).add(item);
					break;
				case 2:
					this.tag(StorageTags.Items.STORAGE_LEVEL_2_UPGRADES).add(item);
					break;
				case 3:
					this.tag(StorageTags.Items.STORAGE_LEVEL_3_UPGRADES).add(item);
					break;
				case 4:
					this.tag(StorageTags.Items.STORAGE_LEVEL_4_UPGRADES).add(item);
					break;
				case 5:
					this.tag(StorageTags.Items.STORAGE_LEVEL_5_UPGRADES).add(item);
					break;
				default:
					this.tag(StorageTags.Items.STORAGE_LEVEL_0_UPGRADES).add(item);
					break;
			}
		}
		this.tag(StorageTags.Items.STORAGE_LEVEL_UPGRADES).addTags(StorageTags.Items.STORAGE_LEVEL_0_UPGRADES, StorageTags.Items.STORAGE_LEVEL_1_UPGRADES, StorageTags.Items.STORAGE_LEVEL_2_UPGRADES, StorageTags.Items.STORAGE_LEVEL_3_UPGRADES, StorageTags.Items.STORAGE_LEVEL_4_UPGRADES, StorageTags.Items.STORAGE_LEVEL_5_UPGRADES);

		for (Entry<StorageMaterial, RegistryObject<BagItem>> bag : StorageItems.BAGS.entrySet()) {
			BagItem item = bag.getValue().get();

			switch (bag.getKey().getStorageLevel()) {
				case 1:
					this.tag(StorageTags.Items.BAGS_LEVEL_1).add(item);
					break;
				case 2:
					this.tag(StorageTags.Items.BAGS_LEVEL_2).add(item);
					break;
				case 3:
					this.tag(StorageTags.Items.BAGS_LEVEL_3).add(item);
					break;
				case 4:
					this.tag(StorageTags.Items.BAGS_LEVEL_4).add(item);
					break;
				case 5:
					this.tag(StorageTags.Items.BAGS_LEVEL_5).add(item);
					break;
				default:
					this.tag(StorageTags.Items.BAGS_LEVEL_0).add(item);
					break;
			}
		}

		this.tag(StorageTags.Items.BAGS_LEVEL_0).add(StorageItems.BAG.get());
		this.tag(StorageTags.Items.BAGS).add(StorageItems.ENDER_BAG.get());
		this.tag(StorageTags.Items.BAGS).addTags(StorageTags.Items.BAGS_LEVEL_0, StorageTags.Items.BAGS_LEVEL_1, StorageTags.Items.BAGS_LEVEL_2, StorageTags.Items.BAGS_LEVEL_3, StorageTags.Items.BAGS_LEVEL_4, StorageTags.Items.BAGS_LEVEL_5);

	}

	@Override
	public String getName() {
		return "Assorted Storage item tags";
	}
}
