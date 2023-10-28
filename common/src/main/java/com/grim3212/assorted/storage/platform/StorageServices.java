package com.grim3212.assorted.storage.platform;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.platform.services.IEquipHelper;

import java.util.ServiceLoader;

public class StorageServices {

    public static final IEquipHelper EQUIP = load(IEquipHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
