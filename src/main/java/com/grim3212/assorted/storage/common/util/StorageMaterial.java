package com.grim3212.assorted.storage.common.util;

import java.util.function.Supplier;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.Tags;

public enum StorageMaterial {
	// Vanilla materials
	// WOOD(() -> ItemTags.PLANKS, new ResourceLocation("block/oak_planks"), 3, 9,
	// () ->
	// Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.5F)),
	STONE(() -> Tags.Items.STONE, new ResourceLocation("block/stone"), 0, 3, 9, 1, 5, () -> Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(3.5F).requiresCorrectToolForDrops()),
	COPPER(() -> Tags.Items.INGOTS_COPPER, new ResourceLocation("block/copper_block"), 1, 4, 9, 1, 6, () -> Block.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE).sound(SoundType.COPPER).requiresCorrectToolForDrops().strength(3.0F, 6.0F)),
	IRON(() -> Tags.Items.INGOTS_IRON, new ResourceLocation("block/iron_block"), 1, 5, 9, 1, 8, 5.0F, 6.0F),
	AMETHYST(() -> Tags.Items.GEMS_AMETHYST, new ResourceLocation("block/amethyst_block"), 2, 6, 9, 1, 7, () -> Block.Properties.of(Material.AMETHYST).sound(SoundType.AMETHYST).requiresCorrectToolForDrops().strength(1.5F)),
	EMERALD(() -> Tags.Items.GEMS_EMERALD, new ResourceLocation("block/emerald_block"), 2, 7, 9, 1, 9, () -> Block.Properties.of(Material.METAL, MaterialColor.EMERALD).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)),
	GOLD(() -> Tags.Items.INGOTS_GOLD, new ResourceLocation("block/gold_block"), 3, 8, 9, 2, 5, () -> Block.Properties.of(Material.METAL, MaterialColor.GOLD).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(3.0F, 6.0F)),
	DIAMOND(() -> Tags.Items.GEMS_DIAMOND, new ResourceLocation("block/diamond_block"), 4, 9, 11, 3, 5, () -> Block.Properties.of(Material.METAL, MaterialColor.DIAMOND).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)),
	NETHERITE(() -> Tags.Items.INGOTS_NETHERITE, new ResourceLocation("block/netherite_block"), 5, 9, 14, 3, 9, () -> Block.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F)),

	// Assorted Core added materials
	ALUMINUM(() -> StorageTags.Items.INGOTS_ALUMINUM, new ResourceLocation(AssortedStorage.MODID, "block/particle/aluminum_block"), 1, 4, 9, 1, 6, 5.0F, 6.0F),
	TIN(() -> StorageTags.Items.INGOTS_TIN, new ResourceLocation(AssortedStorage.MODID, "block/particle/tin_block"), 1, 4, 10, 1, 6, 5.0F, 6.0F),
	TOPAZ(() -> StorageTags.Items.GEMS_TOPAZ, new ResourceLocation(AssortedStorage.MODID, "block/particle/topaz_block"), 1, 5, 9, 1, 7, () -> Block.Properties.of(Material.METAL, MaterialColor.COLOR_YELLOW).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)),
	PERIDOT(() -> StorageTags.Items.GEMS_PERIDOT, new ResourceLocation(AssortedStorage.MODID, "block/particle/peridot_block"), 2, 6, 9, 1, 7, () -> Block.Properties.of(Material.METAL, MaterialColor.COLOR_GREEN).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)),
	LEAD(() -> StorageTags.Items.INGOTS_LEAD, new ResourceLocation(AssortedStorage.MODID, "block/particle/lead_block"), 2, 6, 9, 1, 8, 5.0F, 6.0F),
	NICKEL(() -> StorageTags.Items.INGOTS_NICKEL, new ResourceLocation(AssortedStorage.MODID, "block/particle/nickel_block"), 2, 6, 10, 1, 8, 5.0F, 6.0F),
	SAPPHIRE(() -> StorageTags.Items.GEMS_SAPPHIRE, new ResourceLocation(AssortedStorage.MODID, "block/particle/sapphire_block"), 2, 7, 9, 1, 9, () -> Block.Properties.of(Material.METAL, MaterialColor.COLOR_BLUE).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)),
	BRONZE(() -> StorageTags.Items.INGOTS_BRONZE, new ResourceLocation(AssortedStorage.MODID, "block/particle/bronze_block"), 3, 9, 9, 2, 6, 5.0F, 6.0F),
	RUBY(() -> StorageTags.Items.GEMS_RUBY, new ResourceLocation(AssortedStorage.MODID, "block/particle/ruby_block"), 3, 9, 10, 2, 6, () -> Block.Properties.of(Material.METAL, MaterialColor.COLOR_RED).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)),
	INVAR(() -> StorageTags.Items.INGOTS_INVAR, new ResourceLocation(AssortedStorage.MODID, "block/particle/invar_block"), 4, 9, 10, 2, 8, 5.0F, 6.0F),
	SILVER(() -> StorageTags.Items.INGOTS_SILVER, new ResourceLocation(AssortedStorage.MODID, "block/particle/silver_block"), 3, 9, 11, 2, 7, 5.0F, 6.0F),
	ELECTRUM(() -> StorageTags.Items.INGOTS_ELECTRUM, new ResourceLocation(AssortedStorage.MODID, "block/particle/electrum_block"), 4, 9, 11, 3, 5, 5.0F, 6.0F),
	STEEL(() -> StorageTags.Items.INGOTS_STEEL, new ResourceLocation(AssortedStorage.MODID, "block/particle/steel_block"), 4, 9, 12, 3, 6, 5.0F, 6.0F),
	PLATINUM(() -> StorageTags.Items.INGOTS_PLATINUM, new ResourceLocation(AssortedStorage.MODID, "block/particle/platinum_block"), 5, 9, 13, 3, 8, 5.0F, 6.0F);

	private final Supplier<TagKey<Item>> material;
	private final ResourceLocation particle;
	private final Supplier<Block.Properties> props;
	/**
	 * The level at which this storage item is relative to other storage materials
	 * 
	 * wood/stone being the worst, netherite being the best
	 * 
	 */
	private final int storageLevel;
	private final int xRows;
	private final int yCols;

	private final int hopperXRows;
	private final int hopperYCols;

	private StorageMaterial(Supplier<TagKey<Item>> material, ResourceLocation particle, int storageLevel, int xRows, int yCols, int hopperXRows, int hopperYCols, float destroyTime, float explosionResistance) {
		this(material, particle, storageLevel, xRows, yCols, hopperXRows, hopperYCols, () -> Block.Properties.of(Material.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(destroyTime, explosionResistance));
	}

	private StorageMaterial(Supplier<TagKey<Item>> material, ResourceLocation particle, int storageLevel, int xRows, int yCols, int hopperXRows, int hopperYCols, Supplier<Block.Properties> props) {
		this.material = material;
		this.particle = particle;
		this.storageLevel = storageLevel;
		this.xRows = xRows;
		this.yCols = yCols;
		this.hopperXRows = hopperXRows;
		this.hopperYCols = hopperYCols;
		this.props = props;
	}

	public ResourceLocation getParticle() {
		return particle;
	}

	public int getStorageLevel() {
		return storageLevel;
	}

	public int getXRows() {
		return xRows;
	}

	public int getYCols() {
		return yCols;
	}

	public int totalItems() {
		return this.xRows * this.yCols;
	}

	public int hopperXRows() {
		return this.hopperXRows;
	}

	public int hopperYCols() {
		return this.hopperYCols;
	}

	public int hopperSize() {
		return this.hopperXRows * this.hopperYCols;
	}
	
	public int hopperCooldown() {
		return 8 - storageLevel;
	}

	public TagKey<Item> getMaterial() {
		return material.get();
	}

	public Block.Properties getProps() {
		return props.get();
	}

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
