package com.grim3212.assorted.storage.common.data;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;

import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class StorageBlockTagProvider extends BlockTagsProvider {

	public StorageBlockTagProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
		super(generatorIn, AssortedStorage.MODID, existingFileHelper);
	}

	@Override
	protected void registerTags() {
		Builder<Block> builder = this.getOrCreateBuilder(BlockTags.GUARDED_BY_PIGLINS);

		builder.addItemEntry(StorageBlocks.ACACIA_WAREHOUSE_CRATE.get());
		builder.addItemEntry(StorageBlocks.BIRCH_WAREHOUSE_CRATE.get());
		builder.addItemEntry(StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get());
		builder.addItemEntry(StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get());
		builder.addItemEntry(StorageBlocks.OAK_WAREHOUSE_CRATE.get());
		builder.addItemEntry(StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get());
		builder.addItemEntry(StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get());
		builder.addItemEntry(StorageBlocks.WARPED_WAREHOUSE_CRATE.get());
		builder.addItemEntry(StorageBlocks.GLASS_CABINET.get());
		builder.addItemEntry(StorageBlocks.WOOD_CABINET.get());
		builder.addItemEntry(StorageBlocks.GOLD_SAFE.get());
		builder.addItemEntry(StorageBlocks.OBSIDIAN_SAFE.get());
		builder.addItemEntry(StorageBlocks.LOCKER.get());
		builder.addItemEntry(StorageBlocks.ITEM_TOWER.get());

		this.getOrCreateBuilder(BlockTags.DOORS).addItemEntry(StorageBlocks.QUARTZ_LOCKED_DOOR.get());
	}

	@Override
	public String getName() {
		return "Assorted Storage block tags";
	}
}
