package com.grim3212.assorted.storage.common.data;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import net.minecraft.data.tags.TagsProvider.TagAppender;

public class StorageBlockTagProvider extends BlockTagsProvider {

	public StorageBlockTagProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
		super(generatorIn, AssortedStorage.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		TagAppender<Block> piglinBuilder = this.tag(BlockTags.GUARDED_BY_PIGLINS);
		piglinBuilder.add(StorageBlocks.ACACIA_WAREHOUSE_CRATE.get());
		piglinBuilder.add(StorageBlocks.BIRCH_WAREHOUSE_CRATE.get());
		piglinBuilder.add(StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get());
		piglinBuilder.add(StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get());
		piglinBuilder.add(StorageBlocks.OAK_WAREHOUSE_CRATE.get());
		piglinBuilder.add(StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get());
		piglinBuilder.add(StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get());
		piglinBuilder.add(StorageBlocks.WARPED_WAREHOUSE_CRATE.get());
		piglinBuilder.add(StorageBlocks.GLASS_CABINET.get());
		piglinBuilder.add(StorageBlocks.WOOD_CABINET.get());
		piglinBuilder.add(StorageBlocks.GOLD_SAFE.get());
		piglinBuilder.add(StorageBlocks.OBSIDIAN_SAFE.get());
		piglinBuilder.add(StorageBlocks.LOCKER.get());
		piglinBuilder.add(StorageBlocks.ITEM_TOWER.get());

		TagAppender<Block> doorBuilder = this.tag(BlockTags.DOORS);
		for (Block b : StorageBlocks.lockedDoors()) {
			doorBuilder.add(b);
		}

	}

	@Override
	public String getName() {
		return "Assorted Storage block tags";
	}
}
