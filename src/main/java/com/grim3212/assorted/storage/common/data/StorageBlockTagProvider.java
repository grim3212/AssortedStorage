package com.grim3212.assorted.storage.common.data;

import java.util.Map.Entry;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.LockedBarrelBlock;
import com.grim3212.assorted.storage.common.block.LockedChestBlock;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.util.StorageMaterial;
import com.grim3212.assorted.storage.common.util.StorageTags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

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
		piglinBuilder.add(StorageBlocks.MANGROVE_WAREHOUSE_CRATE.get());
		piglinBuilder.add(StorageBlocks.GLASS_CABINET.get());
		piglinBuilder.add(StorageBlocks.WOOD_CABINET.get());
		piglinBuilder.add(StorageBlocks.GOLD_SAFE.get());
		piglinBuilder.add(StorageBlocks.OBSIDIAN_SAFE.get());
		piglinBuilder.add(StorageBlocks.LOCKER.get());
		piglinBuilder.add(StorageBlocks.ITEM_TOWER.get());
		piglinBuilder.add(StorageBlocks.LOCKED_ENDER_CHEST.get());
		piglinBuilder.add(StorageBlocks.LOCKED_CHEST.get());
		piglinBuilder.add(StorageBlocks.LOCKED_SHULKER_BOX.get());

		for (Entry<StorageMaterial, RegistryObject<LockedChestBlock>> chest : StorageBlocks.CHESTS.entrySet()) {
			Block block = chest.getValue().get();
			piglinBuilder.add(block);
			this.tag(Tags.Blocks.CHESTS).add(block);
			this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);

			switch (chest.getKey().getStorageLevel()) {
				case 1:
					this.tag(StorageTags.Blocks.CHESTS_LEVEL_1).add(block);
					break;
				case 2:
					this.tag(StorageTags.Blocks.CHESTS_LEVEL_2).add(block);
					break;
				case 3:
					this.tag(StorageTags.Blocks.CHESTS_LEVEL_3).add(block);
					break;
				case 4:
					this.tag(StorageTags.Blocks.CHESTS_LEVEL_4).add(block);
					break;
				default:
					this.tag(StorageTags.Blocks.CHESTS_LEVEL_0).add(block);
					break;
			}
		}

		for (Entry<StorageMaterial, RegistryObject<LockedBarrelBlock>> barrel : StorageBlocks.BARRELS.entrySet()) {
			Block block = barrel.getValue().get();
			piglinBuilder.add(block);
			this.tag(Tags.Blocks.BARRELS).add(block);
			this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);

			switch (barrel.getKey().getStorageLevel()) {
				case 1:
					this.tag(StorageTags.Blocks.BARRELS_LEVEL_1).add(block);
					break;
				case 2:
					this.tag(StorageTags.Blocks.BARRELS_LEVEL_2).add(block);
					break;
				case 3:
					this.tag(StorageTags.Blocks.BARRELS_LEVEL_3).add(block);
					break;
				case 4:
					this.tag(StorageTags.Blocks.BARRELS_LEVEL_4).add(block);
					break;
				default:
					this.tag(StorageTags.Blocks.BARRELS_LEVEL_0).add(block);
					break;
			}
		}

		for (Entry<StorageMaterial, RegistryObject<LockedShulkerBoxBlock>> chest : StorageBlocks.SHULKERS.entrySet()) {
			Block block = chest.getValue().get();
			piglinBuilder.add(block);
			this.tag(BlockTags.SHULKER_BOXES).add(block);
			this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);

			switch (chest.getKey().getStorageLevel()) {
				case 1:
					this.tag(StorageTags.Blocks.SHULKERS_LEVEL_1).add(block);
					break;
				case 2:
					this.tag(StorageTags.Blocks.SHULKERS_LEVEL_2).add(block);
					break;
				case 3:
					this.tag(StorageTags.Blocks.SHULKERS_LEVEL_3).add(block);
					break;
				case 4:
					this.tag(StorageTags.Blocks.SHULKERS_LEVEL_4).add(block);
					break;
				default:
					this.tag(StorageTags.Blocks.SHULKERS_LEVEL_0).add(block);
					break;
			}
		}

		this.tag(Tags.Blocks.CHESTS_WOODEN).add(StorageBlocks.LOCKED_CHEST.get());
		this.tag(StorageTags.Blocks.CHESTS_LEVEL_0).addTag(Tags.Blocks.CHESTS_WOODEN);

		this.tag(Tags.Blocks.BARRELS_WOODEN).add(StorageBlocks.LOCKED_BARREL.get());
		this.tag(StorageTags.Blocks.BARRELS_LEVEL_0).addTag(Tags.Blocks.BARRELS_WOODEN);

		this.tag(StorageTags.Blocks.HOPPERS).add(Blocks.HOPPER, StorageBlocks.LOCKED_HOPPER.get());
		this.tag(StorageTags.Blocks.HOPPERS_LEVEL_0).add(Blocks.HOPPER, StorageBlocks.LOCKED_HOPPER.get());

		this.tag(BlockTags.SHULKER_BOXES).add(StorageBlocks.LOCKED_SHULKER_BOX.get());
		this.tag(StorageTags.Blocks.SHULKERS_LEVEL_0).add(Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX,
				Blocks.YELLOW_SHULKER_BOX);
		this.tag(StorageTags.Blocks.SHULKERS_NORMAL).add(Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX,
				Blocks.YELLOW_SHULKER_BOX);

		TagAppender<Block> doorBuilder = this.tag(BlockTags.DOORS);
		for (Block b : StorageBlocks.lockedDoors()) {
			doorBuilder.add(b);
		}

		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(StorageBlocks.ITEM_TOWER.get(), StorageBlocks.LOCKER.get(), StorageBlocks.GOLD_SAFE.get(), StorageBlocks.OBSIDIAN_SAFE.get(), StorageBlocks.LOCKED_IRON_DOOR.get(), StorageBlocks.LOCKED_QUARTZ_DOOR.get(), StorageBlocks.LOCKED_STEEL_DOOR.get(), StorageBlocks.LOCKED_ENDER_CHEST.get(), StorageBlocks.LOCKED_HOPPER.get());
		this.tag(Tags.Blocks.CHESTS_ENDER).add(StorageBlocks.LOCKED_ENDER_CHEST.get());
		this.tag(BlockTags.MINEABLE_WITH_AXE).add(StorageBlocks.LOCKED_CHEST.get(), StorageBlocks.LOCKED_BARREL.get());
	}

	@Override
	public String getName() {
		return "Assorted Storage block tags";
	}
}
