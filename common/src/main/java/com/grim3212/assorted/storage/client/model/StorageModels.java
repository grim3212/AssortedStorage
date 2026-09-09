package com.grim3212.assorted.storage.client.model;

import com.google.common.collect.Maps;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.stream.Stream;

public class StorageModels {

    public static final Map<StorageMaterial, Identifier> CHEST_LOCATIONS = Maps.newHashMap();
    public static final Map<StorageMaterial, Identifier> SHULKER_LOCATIONS = Maps.newHashMap();

    static {
        CHEST_LOCATIONS.put(null, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "model/chests/normal"));
        SHULKER_LOCATIONS.put(null, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "model/shulkers/normal"));
        Stream.of(StorageMaterial.values()).forEach((type) -> {
            CHEST_LOCATIONS.put(type, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "model/chests/" + type.toString()));
            SHULKER_LOCATIONS.put(type, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "model/shulkers/" + type.toString()));
        });
    }

}
