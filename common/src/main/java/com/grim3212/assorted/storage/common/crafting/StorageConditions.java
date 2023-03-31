package com.grim3212.assorted.storage.common.crafting;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.StorageCommonMod;

public class StorageConditions {

    public static class Parts {
        public static final String CHESTS = "chests";
        public static final String BARRELS = "barrels";
        public static final String SHULKERS = "shulkers";
        public static final String HOPPERS = "hoppers";
        public static final String UPGRADES = "upgrades";
        public static final String BAGS = "bags";
        public static final String CRATES = "crates";
    }


    public static void init() {
        Services.CONDITIONS.registerPartCondition(Parts.CHESTS, () -> StorageCommonMod.COMMON_CONFIG.chestsEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.BARRELS, () -> StorageCommonMod.COMMON_CONFIG.barrelsEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.SHULKERS, () -> StorageCommonMod.COMMON_CONFIG.shulkersEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.HOPPERS, () -> StorageCommonMod.COMMON_CONFIG.hoppersEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.UPGRADES, () -> StorageCommonMod.COMMON_CONFIG.upgradesEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.BAGS, () -> StorageCommonMod.COMMON_CONFIG.bagsEnabled.get());
        Services.CONDITIONS.registerPartCondition(Parts.CRATES, () -> StorageCommonMod.COMMON_CONFIG.cratesEnabled.get());
    }

}
