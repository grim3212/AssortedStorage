package com.grim3212.assorted.storage.common.inventory;

import com.grim3212.assorted.storage.common.block.blockentity.BaseStorageBlockEntity;

/**
 * A menu that holds a storage block open, driving its door animation.
 * {@link BaseStorageBlockEntity} recounts open menus every 200 ticks; one it cannot recognise reads
 * as nobody, which drives the count negative on close and freezes the animation.
 */
public interface IStorageMenu {

    /** Whether this menu is holding {@code blockEntity} open. */
    boolean holdsOpen(BaseStorageBlockEntity blockEntity);
}
