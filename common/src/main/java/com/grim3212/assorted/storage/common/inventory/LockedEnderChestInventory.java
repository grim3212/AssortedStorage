package com.grim3212.assorted.storage.common.inventory;

import com.google.common.collect.Lists;
import com.grim3212.assorted.lib.core.inventory.impl.LockedItemStackStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.lib.util.ITagSerializable;
import com.grim3212.assorted.storage.common.block.blockentity.LockedEnderChestBlockEntity;
import com.grim3212.assorted.storage.common.save.IEnderData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

public class LockedEnderChestInventory extends LockedItemStackStorageHandler implements ITagSerializable<CompoundTag> {

    private final IEnderData enderData;
    private final List<Reference<? extends LockedEnderChestBlockEntity>> lockedEnderChests = Lists.newArrayList();

    public LockedEnderChestInventory(IEnderData enderData, String lockCode, int numSlots) {
        super(ILockable.CONSTANT(lockCode), numSlots);
        this.enderData = enderData;
    }

    public void addWeakListener(LockedEnderChestBlockEntity e) {
        lockedEnderChests.add(new WeakReference<>(e));
    }

    public void removeWeakListener(LockedEnderChestBlockEntity e) {
        for (Iterator<Reference<? extends LockedEnderChestBlockEntity>> itr = lockedEnderChests.iterator(); itr.hasNext(); ) {
            LockedEnderChestBlockEntity tileentity = itr.next().get();
            if (tileentity == null || tileentity.isRemoved() || tileentity == e) {
                itr.remove();
            }
        }
    }

    @Override
    public void onContentsChanged(int slot) {
        // HA!
        List<LockedEnderChestBlockEntity> dirtyChests = Lists.newArrayList();
        for (Iterator<Reference<? extends LockedEnderChestBlockEntity>> itr = lockedEnderChests.iterator(); itr.hasNext(); ) {
            LockedEnderChestBlockEntity tileentity = itr.next().get();
            if (tileentity == null || tileentity.isRemoved()) {
                itr.remove();
            } else {
                dirtyChests.add(tileentity);
            }
        }

        dirtyChests.forEach(BlockEntity::setChanged);

        enderData.markDirty();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (lockedEnderChests.size() <= 0) {
            return false;
        }
        return true;
    }

}
