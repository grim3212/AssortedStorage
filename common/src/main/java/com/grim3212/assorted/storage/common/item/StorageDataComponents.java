package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.storage.Constants;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

public class StorageDataComponents {

    public static final RegistryProvider<DataComponentType<?>> DATA_COMPONENTS = RegistryProvider.create(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID);

    public static final IRegistryObject<DataComponentType<StorageInfo>> STORAGE_INFO = DATA_COMPONENTS.register("storage_info",
            () -> new DataComponentType.Builder<StorageInfo>().persistent(StorageInfo.CODEC).networkSynchronized(StorageInfo.STREAM_CODEC).build());
    public static final IRegistryObject<DataComponentType<UpgradeModeInfo>> UPGRADE_MODE_INFO = DATA_COMPONENTS.register("upgrade_mode_info",
            () -> new DataComponentType.Builder<UpgradeModeInfo>().persistent(UpgradeModeInfo.CODEC).networkSynchronized(UpgradeModeInfo.STREAM_CODEC).build());

    // Runs before StorageBlocks and StorageItems, whose items carry these as default components.
    public static void init() {
        Services.PLATFORM.showComponentTooltip(STORAGE_INFO);
        Services.PLATFORM.showComponentTooltip(UPGRADE_MODE_INFO);
    }
}
