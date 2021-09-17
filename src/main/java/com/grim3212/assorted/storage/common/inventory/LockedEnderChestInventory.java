package com.grim3212.assorted.storage.common.inventory;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.grim3212.assorted.storage.common.block.tileentity.LockedEnderChestTileEntity;
import com.grim3212.assorted.storage.common.save.IEnderData;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

public class LockedEnderChestInventory extends ItemStackHandler {
	private final IEnderData enderData;

	private final List<Reference<? extends LockedEnderChestTileEntity>> lockedEnderChests = Lists.newArrayList();

	public LockedEnderChestInventory(IEnderData enderData, int numSlots) {
		super(numSlots);
		this.enderData = enderData;
	}

	public void addWeakListener(LockedEnderChestTileEntity e) {
		lockedEnderChests.add(new WeakReference<>(e));
	}

	public void removeWeakListener(LockedEnderChestTileEntity e) {
		for (Iterator<Reference<? extends LockedEnderChestTileEntity>> itr = lockedEnderChests.iterator(); itr.hasNext();) {
			LockedEnderChestTileEntity tileentity = itr.next().get();
			if (tileentity == null || tileentity.isRemoved() || tileentity == e) {
				itr.remove();
			}
		}
	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);

		// HA!
		List<LockedEnderChestTileEntity> dirtyChests = Lists.newArrayList();
		for (Iterator<Reference<? extends LockedEnderChestTileEntity>> itr = lockedEnderChests.iterator(); itr.hasNext();) {
			LockedEnderChestTileEntity tileentity = itr.next().get();
			if (tileentity == null || tileentity.isRemoved()) {
				itr.remove();
			} else {
				dirtyChests.add(tileentity);
			}
		}

		dirtyChests.forEach(TileEntity::setChanged);

		enderData.markDirty();
	}

}
