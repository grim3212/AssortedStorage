package com.grim3212.assorted.storage.api;

import java.util.function.Supplier;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;

public enum Wood {
	OAK(WoodType.OAK, Blocks.OAK_PLANKS, Blocks.OAK_LOG, () -> ItemTags.OAK_LOGS),
	SPRUCE(WoodType.SPRUCE, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LOG, () -> ItemTags.SPRUCE_LOGS),
	BIRCH(WoodType.BIRCH, Blocks.BIRCH_PLANKS, Blocks.BIRCH_LOG, () -> ItemTags.BIRCH_LOGS),
	ACACIA(WoodType.ACACIA, Blocks.ACACIA_PLANKS, Blocks.ACACIA_LOG, () -> ItemTags.ACACIA_LOGS),
	JUNGLE(WoodType.JUNGLE, Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LOG, () -> ItemTags.JUNGLE_LOGS),
	DARK_OAK(WoodType.DARK_OAK, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LOG, () -> ItemTags.DARK_OAK_LOGS),
	CRIMSON(WoodType.CRIMSON, Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_STEM, () -> ItemTags.CRIMSON_STEMS),
	WARPED(WoodType.WARPED, Blocks.WARPED_PLANKS, Blocks.WARPED_STEM, () -> ItemTags.WARPED_STEMS),
	MANGROVE(WoodType.MANGROVE, Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_LOG, () -> ItemTags.MANGROVE_LOGS);

	private final WoodType type;
	private final Block planks;
	private final Block log;
	private final Supplier<TagKey<Item>> logTag;

	Wood(WoodType type, Block planks, Block log, Supplier<TagKey<Item>> logTag) {
		this.type = type;
		this.planks = planks;
		this.log = log;
		this.logTag = logTag;
	}

	public WoodType getType() {
		return type;
	}

	public Block getPlanks() {
		return planks;
	}

	public Block getLog() {
		return log;
	}

	public TagKey<Item> getLogTag() {
		return logTag.get();
	}
	
	@Override
	public String toString() {
		return this.type.name();
	}

	public String getLogTextureName() {
		if (type == WoodType.CRIMSON) {
			return "crimson_stem";
		} else if (type == WoodType.WARPED) {
			return "warped_stem";
		} else {
			return type.name() + "_log";
		}
	}
}
