package com.grim3212.assorted.storage.client.model;

import com.google.common.collect.Maps;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.stream.Stream;

public class StorageModels {

    public static final Map<StorageMaterial, ResourceLocation> CHEST_LOCATIONS = Maps.newHashMap();
    public static final Map<StorageMaterial, ResourceLocation> SHULKER_LOCATIONS = Maps.newHashMap();

    static {
        CHEST_LOCATIONS.put(null, new ResourceLocation(Constants.MOD_ID, "model/chests/normal"));
        SHULKER_LOCATIONS.put(null, new ResourceLocation(Constants.MOD_ID, "model/shulkers/normal"));
        Stream.of(StorageMaterial.values()).forEach((type) -> {
            CHEST_LOCATIONS.put(type, new ResourceLocation(Constants.MOD_ID, "model/chests/" + type.toString()));
            SHULKER_LOCATIONS.put(type, new ResourceLocation(Constants.MOD_ID, "model/shulkers/" + type.toString()));
        });
    }

}
