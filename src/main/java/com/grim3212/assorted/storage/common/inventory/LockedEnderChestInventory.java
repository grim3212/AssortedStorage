package com.grim3212.assorted.storage.common.inventory;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.grim3212.assorted.storage.common.block.blockentity.LockedEnderChestBlockEntity;
import com.grim3212.assorted.storage.common.save.IEnderData;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;

public class LockedEnderChestInventory extends ItemStackHandler {
	private final IEnderData enderData;

	private final List<Reference<? extends LockedEnderChestBlockEntity>> lockedEnderChests = Lists.newArrayList();

	public LockedEnderChestInventory(IEnderData enderData, int numSlots) {
		super(numSlots);
		this.enderData = enderData;
	}

	public void addWeakListener(LockedEnderChestBlockEntity e) {
		lockedEnderChests.add(new WeakReference<>(e));
	}

	public void removeWeakListener(LockedEnderChestBlockEntity e) {
		for (Iterator<Reference<? extends LockedEnderChestBlockEntity>> itr = lockedEnderChests.iterator(); itr.hasNext();) {
			LockedEnderChestBlockEntity tileentity = itr.next().get();
			if (tileentity == null || tileentity.isRemoved() || tileentity == e) {
				itr.remove();
			}
		}
	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);

		// HA!
		List<LockedEnderChestBlockEntity> dirtyChests = Lists.newArrayList();
		for (Iterator<Reference<? extends LockedEnderChestBlockEntity>> itr = lockedEnderChests.iterator(); itr.hasNext();) {
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

}
