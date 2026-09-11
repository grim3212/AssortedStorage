package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.block.IStorageMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;


/**
 * Shared block item for the storage blocks. Its lock and level lines come from the {@link
 * StorageInfo} component it sets; the vanilla {@code CONTAINER} component shows the contents.
 */
public class StorageBlockItem extends BlockItem {

    public StorageBlockItem(Block b, Properties props) {
        super(b, props.component(StorageDataComponents.STORAGE_INFO.get(), new StorageInfo(StorageInfo.LockLine.CODE, levelOf(b))));
    }

    private static int levelOf(Block block) {
        if (block instanceof IStorageMaterial storageBlock) {
            StorageMaterial material = storageBlock.getStorageMaterial();
            return material == null ? 0 : material.getStorageLevel();
        }
        return -1;
    }
}
