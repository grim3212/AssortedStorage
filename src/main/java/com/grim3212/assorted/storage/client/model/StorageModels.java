package com.grim3212.assorted.storage.client.model;

import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.resources.ResourceLocation;

public class StorageModels {

	public static final Map<StorageMaterial, ResourceLocation> CHEST_LOCATIONS = Maps.newHashMap();
	public static final Map<StorageMaterial, ResourceLocation> SHULKER_LOCATIONS = Maps.newHashMap();
	static {
		CHEST_LOCATIONS.put(null, new ResourceLocation(AssortedStorage.MODID, "model/chests/normal"));
		SHULKER_LOCATIONS.put(null, new ResourceLocation(AssortedStorage.MODID, "model/shulkers/normal"));
		Stream.of(StorageMaterial.values()).forEach((type) -> {
			CHEST_LOCATIONS.put(type, new ResourceLocation(AssortedStorage.MODID, "model/chests/" + type.toString()));
			SHULKER_LOCATIONS.put(type, new ResourceLocation(AssortedStorage.MODID, "model/shulkers/" + type.toString()));
		});
	}

}
