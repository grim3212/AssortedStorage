package com.grim3212.assorted.storage.config;

import com.grim3212.assorted.lib.config.ConfigurationType;
import com.grim3212.assorted.lib.config.IConfigurationBuilder;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.Constants;

import java.util.function.Supplier;

public class StorageCommonConfig {

    public final Supplier<Boolean> chestsEnabled;
    public final Supplier<Boolean> barrelsEnabled;
    public final Supplier<Boolean> shulkersEnabled;
    public final Supplier<Boolean> hoppersEnabled;
    public final Supplier<Boolean> upgradesEnabled;
    public final Supplier<Boolean> bagsEnabled;
    public final Supplier<Boolean> cratesEnabled;

    public final Supplier<Boolean> hideUncraftableItems;

    public final Supplier<Integer> maxControllerSearchRange;

    public StorageCommonConfig() {
        final IConfigurationBuilder builder = Services.CONFIG.createBuilder(ConfigurationType.NOT_SYNCED, Constants.MOD_ID + "-common");

        chestsEnabled = builder.defineBoolean("parts.chestsEnabled", true, "Set this to true if you would like extra chests to be craftable and found in the creative tab.");
        barrelsEnabled = builder.defineBoolean("parts.barrelsEnabled", true, "Set this to true if you would like extra barrels to be craftable and found in the creative tab.");
        shulkersEnabled = builder.defineBoolean("parts.shulkersEnabled", true, "Set this to true if you would like extra shulker boxes to be craftable and found in the creative tab.");
        hoppersEnabled = builder.defineBoolean("parts.hoppersEnabled", true, "Set this to true if you would like extra hoppers to be craftable and found in the creative tab.");
        upgradesEnabled = builder.defineBoolean("parts.upgradesEnabled", true, "Set this to true if you would like to be able to use and craft storage upgrades.");
        bagsEnabled = builder.defineBoolean("parts.bagsEnabled", true, "Set this to true if you would like to be able to use and craft bags.");
        cratesEnabled = builder.defineBoolean("parts.cratesEnabled", true, "Set this to true if you would like to be able to use and craft storage crates.");

        hideUncraftableItems = builder.defineBoolean("general.hideUncraftableItems", false, "For any item that is unobtainable (like missing materials from other mods) hide it from the creative menu / JEI.");

        maxControllerSearchRange = builder.defineInteger("crates.maxControllerSearchRange", 64, 1, 500, "What is the maximum distance that Storage Crate Controllers will search for Storage Crates.");

        builder.setup();
    }
}
