package com.grim3212.assorted.storage.common.data;

import java.util.Map.Entry;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.LevelUpgradeItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageMaterial;
import com.grim3212.assorted.storage.common.util.StorageTags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class StorageItemTagProvider extends ItemTagsProvider {

	public StorageItemTagProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagProvider, AssortedStorage.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(ItemTags.PIGLIN_LOVED).add(StorageBlocks.GOLD_SAFE.get().asItem(), StorageBlocks.CHESTS.get(StorageMaterial.GOLD).get().asItem(), StorageItems.LEVEL_UPGRADES.get(StorageMaterial.GOLD).get().asItem());

		this.tag(Tags.Items.CHESTS_ENDER).add(StorageBlocks.LOCKED_ENDER_CHEST.get().asItem());

		this.copy(Tags.Blocks.CHESTS, Tags.Items.CHESTS);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_0, StorageTags.Items.CHESTS_LEVEL_0);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_1, StorageTags.Items.CHESTS_LEVEL_1);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_2, StorageTags.Items.CHESTS_LEVEL_2);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_3, StorageTags.Items.CHESTS_LEVEL_3);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_4, StorageTags.Items.CHESTS_LEVEL_4);

		this.tag(StorageTags.Items.CAN_UPGRADE_LEVEL_0).addTag(Tags.Items.CHESTS_WOODEN);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_0, StorageTags.Items.CAN_UPGRADE_LEVEL_1);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_1, StorageTags.Items.CAN_UPGRADE_LEVEL_2);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_2, StorageTags.Items.CAN_UPGRADE_LEVEL_3);
		this.copy(StorageTags.Blocks.CHESTS_LEVEL_3, StorageTags.Items.CAN_UPGRADE_LEVEL_4);

		this.tag(StorageTags.Items.PAPER).add(Items.PAPER);

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
				default:
					this.tag(StorageTags.Items.STORAGE_LEVEL_0_UPGRADES).add(item);
					break;
			}
		}
		this.tag(StorageTags.Items.STORAGE_LEVEL_UPGRADES).addTags(StorageTags.Items.STORAGE_LEVEL_0_UPGRADES, StorageTags.Items.STORAGE_LEVEL_1_UPGRADES, StorageTags.Items.STORAGE_LEVEL_2_UPGRADES, StorageTags.Items.STORAGE_LEVEL_3_UPGRADES, StorageTags.Items.STORAGE_LEVEL_4_UPGRADES);
	}

	@Override
	public String getName() {
		return "Assorted Storage item tags";
	}
}
