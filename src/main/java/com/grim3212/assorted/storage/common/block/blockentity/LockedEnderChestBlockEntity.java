package com.grim3212.assorted.storage.common.block.blockentity;

import javax.annotation.Nonnull;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.inventory.LockedEnderChestInventory;
import com.grim3212.assorted.storage.common.inventory.StorageContainer;
import com.grim3212.assorted.storage.common.save.EnderSavedData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

@OnlyIn(value = Dist.CLIENT, _interface = IStorage.class)
public class LockedEnderChestBlockEntity extends BaseStorageBlockEntity {

	private LockedEnderChestInventory inventory;
	private LazyOptional<IItemHandler> inventoryLazy = LazyOptional.of(this::getInventory);

	public LockedEnderChestBlockEntity(BlockPos pos, BlockState state) {
		super(StorageBlockEntityTypes.LOCKED_ENDER_CHEST.get(), pos, state);
	}

	@Nonnull
	public IItemHandlerModifiable getInventory() {
		if (level != null && inventory == null && isLocked()) {
			inventory = EnderSavedData.get(level).getInventory(this.getStorageLockCode());
			inventory.addWeakListener(this);
		}
		return inventory;
	}

	@Override
	public void setLockCode(String s) {
		super.setLockCode(s);
		invalidateInventory();
	}

	private void invalidateInventory() {
		inventoryLazy.invalidate();
		inventoryLazy = LazyOptional.of(this::getInventory);

		releasePreviousInventory();
	}

	private void releasePreviousInventory() {
		if (inventory != null) {
			inventory.removeWeakListener(this);
		}
		inventory = null;
	}

	@Override
	public int getContainerSize() {
		return this.getInventory().getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < this.getInventory().getSlots(); i++) {
			ItemStack stack = this.getInventory().getStackInSlot(i);
			if (!stack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return inventoryLazy.cast();
		}
		return super.getCapability(cap, side);
	}

	/**
	 * invalidates a tile entity
	 */
	@Override
	public void setRemoved() {
		this.remove = true;
		this.invalidateCaps();
		requestModelDataUpdate();
		invalidateInventory();
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < this.getInventory().getSlots(); i++) {
			this.getInventory().setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	@Override
	public ItemStack getItem(int index) {
		return this.getInventory().getStackInSlot(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack stack = this.getInventory().extractItem(index, count, false);
		if (!stack.isEmpty()) {
			this.setChanged();
		}

		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return this.getInventory().extractItem(index, 1, false);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.getInventory().setStackInSlot(index, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}

		this.setChanged();
	}

	@Override
	protected boolean selfInventory() {
		return false;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
		return StorageContainer.createEnderChestContainer(windowId, playerInv, this);
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent(AssortedStorage.MODID + ".container.locked_ender_chest");
	}

}
