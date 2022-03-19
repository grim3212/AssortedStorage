package com.grim3212.assorted.storage.common.data;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

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
		piglinBuilder.add(StorageBlocks.LOCKED_ENDER_CHEST.get());

		TagAppender<Block> doorBuilder = this.tag(BlockTags.DOORS);
		for (Block b : StorageBlocks.lockedDoors()) {
			doorBuilder.add(b);
		}

		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(StorageBlocks.ITEM_TOWER.get(), StorageBlocks.LOCKER.get(), StorageBlocks.GOLD_SAFE.get(), StorageBlocks.OBSIDIAN_SAFE.get(), StorageBlocks.LOCKED_IRON_DOOR.get(), StorageBlocks.LOCKED_QUARTZ_DOOR.get(), StorageBlocks.LOCKED_STEEL_DOOR.get(), StorageBlocks.LOCKED_ENDER_CHEST.get());
		this.tag(Tags.Blocks.CHESTS_ENDER).add(StorageBlocks.LOCKED_ENDER_CHEST.get());
	}

	@Override
	public String getName() {
		return "Assorted Storage block tags";
	}
}
