package com.grim3212.assorted.storage.common.handler;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public final class StorageConfig {

	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static class Common {

		public final ForgeConfigSpec.BooleanValue chestsEnabled;
		public final ForgeConfigSpec.BooleanValue barrelsEnabled;
		public final ForgeConfigSpec.BooleanValue shulkersEnabled;
		public final ForgeConfigSpec.BooleanValue hoppersEnabled;
		public final ForgeConfigSpec.BooleanValue upgradesEnabled;
		public final ForgeConfigSpec.BooleanValue bagsEnabled;
		public final ForgeConfigSpec.BooleanValue cratesEnabled;

		public final ForgeConfigSpec.BooleanValue hideUncraftableItems;

		public final ForgeConfigSpec.IntValue maxControllerSearchRange;

		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("Parts");
			chestsEnabled = builder.comment("Set this to true if you would like extra chests to be craftable and found in the creative tab.").define("chestsEnabled", true);
			barrelsEnabled = builder.comment("Set this to true if you would like extra barrels to be craftable and found in the creative tab.").define("barrelsEnabled", true);
			shulkersEnabled = builder.comment("Set this to true if you would like extra shulker boxes to be craftable and found in the creative tab.").define("shulkersEnabled", true);
			hoppersEnabled = builder.comment("Set this to true if you would like extra hoppers to be craftable and found in the creative tab.").define("hoppersEnabled", true);
			upgradesEnabled = builder.comment("Set this to true if you would like to be able to use and craft storage upgrades.").define("upgradesEnabled", true);
			bagsEnabled = builder.comment("Set this to true if you would like to be able to use and craft bags.").define("bagsEnabled", true);
			cratesEnabled = builder.comment("Set this to true if you would like to be able to use and craft storage crates.").define("cratesEnabled", true);
			builder.pop();

			builder.push("General");
			hideUncraftableItems = builder.comment("For any item that is unobtainable (like missing materials from other mods) hide it from the creative menu / JEI.").define("hideUncraftableItems", false);
			builder.pop();

			builder.push("Crates");
			maxControllerSearchRange = builder.comment("What is the maximum distance that Storage Crate Controllers will search for Storage Crates.").defineInRange("maxControllerSearchRange", 64, 1, 500);
			builder.pop();
		}
	}

	public static final Client CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;
	static {
		final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	public static class Client {

		public final ForgeConfigSpec.IntValue crateMaxRenderDistance;

		public Client(ForgeConfigSpec.Builder builder) {
			builder.push("Crates");
			crateMaxRenderDistance = builder.comment("Set this to the maximum distance you would like to still render Storage Crate items.").defineInRange("crateMaxRenderDistance", 16, 1, 256);
			builder.pop();
		}

	}
}
