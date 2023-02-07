package com.grim3212.assorted.storage.common.util;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class StorageTags {

	public static class Blocks {
		public static final TagKey<Block> CHESTS_LEVEL_0 = storageTag("chests/level_0");
		public static final TagKey<Block> CHESTS_LEVEL_1 = storageTag("chests/level_1");
		public static final TagKey<Block> CHESTS_LEVEL_2 = storageTag("chests/level_2");
		public static final TagKey<Block> CHESTS_LEVEL_3 = storageTag("chests/level_3");
		public static final TagKey<Block> CHESTS_LEVEL_4 = storageTag("chests/level_4");
		public static final TagKey<Block> CHESTS_LEVEL_5 = storageTag("chests/level_5");

		public static final TagKey<Block> SHULKERS_NORMAL = storageTag("shulkers/normal");
		public static final TagKey<Block> SHULKERS_LEVEL_0 = storageTag("shulkers/level_0");
		public static final TagKey<Block> SHULKERS_LEVEL_1 = storageTag("shulkers/level_1");
		public static final TagKey<Block> SHULKERS_LEVEL_2 = storageTag("shulkers/level_2");
		public static final TagKey<Block> SHULKERS_LEVEL_3 = storageTag("shulkers/level_3");
		public static final TagKey<Block> SHULKERS_LEVEL_4 = storageTag("shulkers/level_4");
		public static final TagKey<Block> SHULKERS_LEVEL_5 = storageTag("shulkers/level_5");

		public static final TagKey<Block> BARRELS_LEVEL_0 = storageTag("barrels/level_0");
		public static final TagKey<Block> BARRELS_LEVEL_1 = storageTag("barrels/level_1");
		public static final TagKey<Block> BARRELS_LEVEL_2 = storageTag("barrels/level_2");
		public static final TagKey<Block> BARRELS_LEVEL_3 = storageTag("barrels/level_3");
		public static final TagKey<Block> BARRELS_LEVEL_4 = storageTag("barrels/level_4");
		public static final TagKey<Block> BARRELS_LEVEL_5 = storageTag("barrels/level_5");

		public static final TagKey<Block> HOPPERS = forgeTag("hoppers");
		public static final TagKey<Block> HOPPERS_LEVEL_0 = storageTag("hoppers/level_0");
		public static final TagKey<Block> HOPPERS_LEVEL_1 = storageTag("hoppers/level_1");
		public static final TagKey<Block> HOPPERS_LEVEL_2 = storageTag("hoppers/level_2");
		public static final TagKey<Block> HOPPERS_LEVEL_3 = storageTag("hoppers/level_3");
		public static final TagKey<Block> HOPPERS_LEVEL_4 = storageTag("hoppers/level_4");
		public static final TagKey<Block> HOPPERS_LEVEL_5 = storageTag("hoppers/level_5");
		
		public static final TagKey<Block> CRATES = storageTag("crates");
		public static final TagKey<Block> CRATES_SINGLE = storageTag("crates/single");
		public static final TagKey<Block> CRATES_DOUBLE = storageTag("crates/double");
		public static final TagKey<Block> CRATES_TRIPLE = storageTag("crates/triple");
		public static final TagKey<Block> CRATES_QUADRUPLE = storageTag("crates/quadruple");

		public static final TagKey<Block> DEEPSLATE = forgeTag("deepslate");
		public static final TagKey<Block> PISTONS = forgeTag("pistons");
		
		public static TagKey<Block> forgeTag(String name) {
			return BlockTags.create(new ResourceLocation("forge", name));
		}

		public static TagKey<Block> storageTag(String name) {
			return BlockTags.create(new ResourceLocation(AssortedStorage.MODID, name));
		}
	}

	public static class Items {
		public static final TagKey<Item> INGOTS_TIN = forgeTag("ingots/tin");
		public static final TagKey<Item> INGOTS_SILVER = forgeTag("ingots/silver");
		public static final TagKey<Item> INGOTS_ALUMINUM = forgeTag("ingots/aluminum");
		public static final TagKey<Item> INGOTS_NICKEL = forgeTag("ingots/nickel");
		public static final TagKey<Item> INGOTS_PLATINUM = forgeTag("ingots/platinum");
		public static final TagKey<Item> INGOTS_LEAD = forgeTag("ingots/lead");
		public static final TagKey<Item> INGOTS_BRONZE = forgeTag("ingots/bronze");
		public static final TagKey<Item> INGOTS_ELECTRUM = forgeTag("ingots/electrum");
		public static final TagKey<Item> INGOTS_INVAR = forgeTag("ingots/invar");
		public static final TagKey<Item> INGOTS_STEEL = forgeTag("ingots/steel");
		public static final TagKey<Item> GEMS_RUBY = forgeTag("gems/ruby");
		public static final TagKey<Item> GEMS_SAPPHIRE = forgeTag("gems/sapphire");
		public static final TagKey<Item> GEMS_TOPAZ = forgeTag("gems/topaz");
		public static final TagKey<Item> GEMS_PERIDOT = forgeTag("gems/peridot");

		public static final TagKey<Item> PAPER = forgeTag("paper");

		public static final TagKey<Item> CAN_UPGRADE_LEVEL_0 = storageTag("can_upgrade/level_0");
		public static final TagKey<Item> CAN_UPGRADE_LEVEL_1 = storageTag("can_upgrade/level_1");
		public static final TagKey<Item> CAN_UPGRADE_LEVEL_2 = storageTag("can_upgrade/level_2");
		public static final TagKey<Item> CAN_UPGRADE_LEVEL_3 = storageTag("can_upgrade/level_3");
		public static final TagKey<Item> CAN_UPGRADE_LEVEL_4 = storageTag("can_upgrade/level_4");
		public static final TagKey<Item> CAN_UPGRADE_LEVEL_5 = storageTag("can_upgrade/level_5");

		public static final TagKey<Item> CHESTS_LEVEL_0 = storageTag("chests/level_0");
		public static final TagKey<Item> CHESTS_LEVEL_1 = storageTag("chests/level_1");
		public static final TagKey<Item> CHESTS_LEVEL_2 = storageTag("chests/level_2");
		public static final TagKey<Item> CHESTS_LEVEL_3 = storageTag("chests/level_3");
		public static final TagKey<Item> CHESTS_LEVEL_4 = storageTag("chests/level_4");
		public static final TagKey<Item> CHESTS_LEVEL_5 = storageTag("chests/level_5");

		public static final TagKey<Item> SHULKERS_NORMAL = storageTag("shulkers/normal");
		public static final TagKey<Item> SHULKERS_LEVEL_0 = storageTag("shulkers/level_0");
		public static final TagKey<Item> SHULKERS_LEVEL_1 = storageTag("shulkers/level_1");
		public static final TagKey<Item> SHULKERS_LEVEL_2 = storageTag("shulkers/level_2");
		public static final TagKey<Item> SHULKERS_LEVEL_3 = storageTag("shulkers/level_3");
		public static final TagKey<Item> SHULKERS_LEVEL_4 = storageTag("shulkers/level_4");
		public static final TagKey<Item> SHULKERS_LEVEL_5 = storageTag("shulkers/level_5");

		public static final TagKey<Item> BARRELS_LEVEL_0 = storageTag("barrels/level_0");
		public static final TagKey<Item> BARRELS_LEVEL_1 = storageTag("barrels/level_1");
		public static final TagKey<Item> BARRELS_LEVEL_2 = storageTag("barrels/level_2");
		public static final TagKey<Item> BARRELS_LEVEL_3 = storageTag("barrels/level_3");
		public static final TagKey<Item> BARRELS_LEVEL_4 = storageTag("barrels/level_4");
		public static final TagKey<Item> BARRELS_LEVEL_5 = storageTag("barrels/level_5");

		public static final TagKey<Item> HOPPERS = forgeTag("hoppers");
		public static final TagKey<Item> HOPPERS_LEVEL_0 = storageTag("hoppers/level_0");
		public static final TagKey<Item> HOPPERS_LEVEL_1 = storageTag("hoppers/level_1");
		public static final TagKey<Item> HOPPERS_LEVEL_2 = storageTag("hoppers/level_2");
		public static final TagKey<Item> HOPPERS_LEVEL_3 = storageTag("hoppers/level_3");
		public static final TagKey<Item> HOPPERS_LEVEL_4 = storageTag("hoppers/level_4");
		public static final TagKey<Item> HOPPERS_LEVEL_5 = storageTag("hoppers/level_5");

		public static final TagKey<Item> STORAGE_LEVEL_UPGRADES = forgeTag("storage/level_upgrades");
		public static final TagKey<Item> STORAGE_LEVEL_0_UPGRADES = forgeTag("storage/level_0_upgrades");
		public static final TagKey<Item> STORAGE_LEVEL_1_UPGRADES = forgeTag("storage/level_1_upgrades");
		public static final TagKey<Item> STORAGE_LEVEL_2_UPGRADES = forgeTag("storage/level_2_upgrades");
		public static final TagKey<Item> STORAGE_LEVEL_3_UPGRADES = forgeTag("storage/level_3_upgrades");
		public static final TagKey<Item> STORAGE_LEVEL_4_UPGRADES = forgeTag("storage/level_4_upgrades");
		public static final TagKey<Item> STORAGE_LEVEL_5_UPGRADES = forgeTag("storage/level_5_upgrades");
		
		public static final TagKey<Item> BAGS = storageTag("bags");
		public static final TagKey<Item> BAGS_LEVEL_0 = storageTag("bags/level_0");
		public static final TagKey<Item> BAGS_LEVEL_1 = storageTag("bags/level_1");
		public static final TagKey<Item> BAGS_LEVEL_2 = storageTag("bags/level_2");
		public static final TagKey<Item> BAGS_LEVEL_3 = storageTag("bags/level_3");
		public static final TagKey<Item> BAGS_LEVEL_4 = storageTag("bags/level_4");
		public static final TagKey<Item> BAGS_LEVEL_5 = storageTag("bags/level_5");
		
		public static final TagKey<Item> CRATES = storageTag("crates");
		public static final TagKey<Item> CRATES_SINGLE = storageTag("crates/single");
		public static final TagKey<Item> CRATES_DOUBLE = storageTag("crates/double");
		public static final TagKey<Item> CRATES_TRIPLE = storageTag("crates/triple");
		public static final TagKey<Item> CRATES_QUADRUPLE = storageTag("crates/quadruple");
		public static final TagKey<Item> UPGRADES = storageTag("upgrades");
		public static final TagKey<Item> CRAFTING_OVERRIDE = storageTag("crafting_override");
		
		public static final TagKey<Item> DEEPSLATE = forgeTag("deepslate");
		public static final TagKey<Item> PISTONS = forgeTag("pistons");

		public static TagKey<Item> forgeTag(String name) {
			return ItemTags.create(new ResourceLocation("forge", name));
		}

		public static TagKey<Item> storageTag(String name) {
			return ItemTags.create(new ResourceLocation(AssortedStorage.MODID, name));
		}
	}

}
