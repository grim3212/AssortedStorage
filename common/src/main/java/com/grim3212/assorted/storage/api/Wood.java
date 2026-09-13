package com.grim3212.assorted.storage.api;

import java.util.function.Supplier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * Every vanilla {@link WoodType}, in {@code WoodType.values()} order. Each wood's four crates,
 * warehouse crate and locked door are generated from this, so a wood added here gets its blocks,
 * models, recipes, loot, tags and names for free; only the textures have to be drawn
 * ({@code block/crates/<wood>_facing}, {@code model/warehouse_crate/<wood>} and the two halves of
 * {@code block/locked_<wood>_door}). The {@code every_vanilla_wood_has_a_family} and
 * {@code every_locked_door_has_its_textures} gametests guard both halves.
 */
public enum Wood {
	OAK(WoodType.OAK, Blocks.OAK_PLANKS, Blocks.OAK_LOG, Blocks.OAK_DOOR, () -> ItemTags.OAK_LOGS),
	SPRUCE(WoodType.SPRUCE, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LOG, Blocks.SPRUCE_DOOR, () -> ItemTags.SPRUCE_LOGS),
	BIRCH(WoodType.BIRCH, Blocks.BIRCH_PLANKS, Blocks.BIRCH_LOG, Blocks.BIRCH_DOOR, () -> ItemTags.BIRCH_LOGS),
	ACACIA(WoodType.ACACIA, Blocks.ACACIA_PLANKS, Blocks.ACACIA_LOG, Blocks.ACACIA_DOOR, () -> ItemTags.ACACIA_LOGS),
	CHERRY(WoodType.CHERRY, Blocks.CHERRY_PLANKS, Blocks.CHERRY_LOG, Blocks.CHERRY_DOOR, () -> ItemTags.CHERRY_LOGS),
	JUNGLE(WoodType.JUNGLE, Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LOG, Blocks.JUNGLE_DOOR, () -> ItemTags.JUNGLE_LOGS),
	DARK_OAK(WoodType.DARK_OAK, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_DOOR, () -> ItemTags.DARK_OAK_LOGS),
	PALE_OAK(WoodType.PALE_OAK, Blocks.PALE_OAK_PLANKS, Blocks.PALE_OAK_LOG, Blocks.PALE_OAK_DOOR, () -> ItemTags.PALE_OAK_LOGS),
	CRIMSON(WoodType.CRIMSON, Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_STEM, Blocks.CRIMSON_DOOR, () -> ItemTags.CRIMSON_STEMS),
	WARPED(WoodType.WARPED, Blocks.WARPED_PLANKS, Blocks.WARPED_STEM, Blocks.WARPED_DOOR, () -> ItemTags.WARPED_STEMS),
	MANGROVE(WoodType.MANGROVE, Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_LOG, Blocks.MANGROVE_DOOR, () -> ItemTags.MANGROVE_LOGS),
	// Bamboo's "log" is the bamboo block, and its tag is #minecraft:bamboo_blocks rather than a
	// *_logs one. Everything else reads through getLog()/getLogTag(), so nothing else special-cases it.
	BAMBOO(WoodType.BAMBOO, Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_BLOCK, Blocks.BAMBOO_DOOR, () -> ItemTags.BAMBOO_BLOCKS);

	private final WoodType type;
	private final Block planks;
	private final Block log;
	private final Block door;
	private final Supplier<TagKey<Item>> logTag;

	Wood(WoodType type, Block planks, Block log, Block door, Supplier<TagKey<Item>> logTag) {
		this.type = type;
		this.planks = planks;
		this.log = log;
		this.door = door;
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

	/** The vanilla door of this wood, which the matching locked door stands in for. */
	public Block getDoor() {
		return door;
	}

	public TagKey<Item> getLogTag() {
		return logTag.get();
	}

	@Override
	public String toString() {
		return this.type.name();
	}

	/**
	 * The vanilla side texture of {@link #getLog()}, which is its registry path: {@code oak_log},
	 * {@code crimson_stem}, {@code bamboo_block}. Taken from the registry rather than spelled out,
	 * because the three families disagree on the suffix.
	 */
	public String getLogTextureName() {
		return BuiltInRegistries.BLOCK.getKey(this.log).getPath();
	}

	/** The end-grain texture beside {@link #getLogTextureName()}, used as the crate's particle. */
	public String getLogTopTextureName() {
		return getLogTextureName() + "_top";
	}
}
