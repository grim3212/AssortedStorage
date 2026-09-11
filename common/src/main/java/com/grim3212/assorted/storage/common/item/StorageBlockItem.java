package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.block.IStorageMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;


/**
 * Shared block item for the storage blocks.
 * <p>
 * Its lock code and storage level lines come from the {@link StorageInfo} component its
 * constructor sets; tooltips are a component concern now. The stored contents lines are gone from
 * the mod's own code entirely: the vanilla {@code CONTAINER} data component the block entity hands
 * to the stack renders them itself.
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
