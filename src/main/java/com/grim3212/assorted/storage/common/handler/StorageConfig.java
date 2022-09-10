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
		public final ForgeConfigSpec.BooleanValue shulkersEnabled;
		public final ForgeConfigSpec.BooleanValue upgradesEnabled;

		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("Parts");
			chestsEnabled = builder.comment("Set this to true if you would like extra chests to be craftable and found in the creative tab.").define("chestsEnabled", true);
			shulkersEnabled = builder.comment("Set this to true if you would like extra shulker boxes to be craftable and found in the creative tab.").define("shulkersEnabled", true);
			upgradesEnabled = builder.comment("Set this to true if you would like to be able to use and craft storage upgrades.").define("upgradesEnabled", true);
			builder.pop();
		}
	}
}
