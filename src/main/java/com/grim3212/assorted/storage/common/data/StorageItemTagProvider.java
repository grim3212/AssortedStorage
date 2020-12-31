package com.grim3212.assorted.storage.common.data;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class StorageItemTagProvider extends ItemTagsProvider {

	public StorageItemTagProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, blockTagProvider, AssortedStorage.MODID, existingFileHelper);
	}

	@Override
	protected void registerTags() {
		this.getOrCreateBuilder(ItemTags.PIGLIN_LOVED).addItemEntry(StorageBlocks.GOLD_SAFE.get().asItem());
	}

	@Override
	public String getName() {
		return "Assorted Storage item tags";
	}
}
