package com.grim3212.assorted.storage.common.item;

import net.minecraft.world.item.Item;


public class CombinationItem extends Item {

    public CombinationItem(Properties properties) {
        super(properties.stacksTo(16).component(StorageDataComponents.STORAGE_INFO.get(), new StorageInfo(StorageInfo.LockLine.CODE, -1)));
    }
}
