package com.grim3212.assorted.storage.common.handler;

import com.google.gson.JsonObject;
import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class EnabledCondition implements ICondition {

	private static final ResourceLocation NAME = new ResourceLocation(AssortedStorage.MODID, "part_enabled");
	private final String part;

	public static final String CHESTS_CONDITION = "chests";
	public static final String BARRELS_CONDITION = "barrels";
	public static final String SHULKERS_CONDITION = "shulkers";
	public static final String HOPPERS_CONDITION = "hoppers";
	public static final String UPGRADES_CONDITION = "upgrades";
	public static final String BAGS_CONDITION = "bags";
	public static final String STORAGE_CRATES_CONDITION = "storage_crates";

	public EnabledCondition(String part) {
		this.part = part;
	}

	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test(IContext context) {
		switch (part) {
			case CHESTS_CONDITION:
				return StorageConfig.COMMON.chestsEnabled.get();
			case BARRELS_CONDITION:
				return StorageConfig.COMMON.barrelsEnabled.get();
			case SHULKERS_CONDITION:
				return StorageConfig.COMMON.shulkersEnabled.get();
			case HOPPERS_CONDITION:
				return StorageConfig.COMMON.hoppersEnabled.get();
			case UPGRADES_CONDITION:
				return StorageConfig.COMMON.upgradesEnabled.get();
			case BAGS_CONDITION:
				return StorageConfig.COMMON.bagsEnabled.get();
			case STORAGE_CRATES_CONDITION:
				return StorageConfig.COMMON.storageCratesEnabled.get();
			default:
				return false;
		}
	}

	public static class Serializer implements IConditionSerializer<EnabledCondition> {
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public void write(JsonObject json, EnabledCondition value) {
			json.addProperty("part", value.part);
		}

		@Override
		public EnabledCondition read(JsonObject json) {
			return new EnabledCondition(GsonHelper.getAsString(json, "part"));
		}

		@Override
		public ResourceLocation getID() {
			return EnabledCondition.NAME;
		}
	}
}
