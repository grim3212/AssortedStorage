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
		Builder<Block> piglinBuilder = this.getOrCreateBuilder(BlockTags.GUARDED_BY_PIGLINS);
		piglinBuilder.addItemEntry(StorageBlocks.ACACIA_WAREHOUSE_CRATE.get());
		piglinBuilder.addItemEntry(StorageBlocks.BIRCH_WAREHOUSE_CRATE.get());
		piglinBuilder.addItemEntry(StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get());
		piglinBuilder.addItemEntry(StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get());
		piglinBuilder.addItemEntry(StorageBlocks.OAK_WAREHOUSE_CRATE.get());
		piglinBuilder.addItemEntry(StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get());
		piglinBuilder.addItemEntry(StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get());
		piglinBuilder.addItemEntry(StorageBlocks.WARPED_WAREHOUSE_CRATE.get());
		piglinBuilder.addItemEntry(StorageBlocks.GLASS_CABINET.get());
		piglinBuilder.addItemEntry(StorageBlocks.WOOD_CABINET.get());
		piglinBuilder.addItemEntry(StorageBlocks.GOLD_SAFE.get());
		piglinBuilder.addItemEntry(StorageBlocks.OBSIDIAN_SAFE.get());
		piglinBuilder.addItemEntry(StorageBlocks.LOCKER.get());
		piglinBuilder.addItemEntry(StorageBlocks.ITEM_TOWER.get());

		Builder<Block> doorBuilder = this.getOrCreateBuilder(BlockTags.DOORS);
		for (Block b : StorageBlocks.lockedDoors()) {
			doorBuilder.addItemEntry(b);
		}

	}

	@Override
	public String getName() {
		return "Assorted Storage block tags";
	}
}
