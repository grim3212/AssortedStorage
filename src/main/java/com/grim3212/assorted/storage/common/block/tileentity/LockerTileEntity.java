package com.grim3212.assorted.storage.common.block.tileentity;

import java.util.stream.IntStream;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.inventory.DualLockerInventory;
import com.grim3212.assorted.storage.common.inventory.LockerContainer;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class LockerTileEntity extends BaseStorageTileEntity {

	public LockerTileEntity() {
		super(StorageTileEntityTypes.LOCKER.get(), 45);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory player, PlayerEntity playerEntity) {
		TileEntity lockerUp = world.getTileEntity(pos.up());

		if (lockerUp != null && lockerUp instanceof LockerTileEntity) {
			return LockerContainer.createDualLockerContainer(windowId, player, new DualLockerInventory(this, (LockerTileEntity) lockerUp));
		}

		return LockerContainer.createLockerContainer(windowId, player, this);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent(AssortedStorage.MODID + ".container.locker");
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return lockerItemHandler.cast();
		}
		return super.getCapability(cap, side);
	}

	private LazyOptional<?> lockerItemHandler = LazyOptional.of(() -> createSidedHandler());

	protected IItemHandler createSidedHandler() {
		TileEntity lockerUp = world.getTileEntity(pos.up());
		if (lockerUp != null && lockerUp instanceof LockerTileEntity) {
			return new SidedInvWrapper(new DualLockerInventory(this, (LockerTileEntity) lockerUp), null);
		}

		TileEntity lockerDown = world.getTileEntity(pos.down());
		if (lockerDown != null && lockerDown instanceof LockerTileEntity) {
			return new SidedInvWrapper(new DualLockerInventory((LockerTileEntity) lockerDown, this), null);
		}

		return super.createSidedHandler();
	}

	@Override
	public void remove() {
		super.remove();
		this.lockerItemHandler.invalidate();
	}

	protected static final int[] LOCKER_SLOTS = IntStream.range(0, 45).toArray();

	@Override
	public int[] getSlotsForFace(Direction side) {
		return LOCKER_SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		TileEntity lockerDown = world.getTileEntity(pos.down());
		if (lockerDown != null && lockerDown instanceof LockerTileEntity) {
			return ((LockerTileEntity) lockerDown).canInsertItem(index, itemStackIn, direction);
		}

		return super.canInsertItem(index, itemStackIn, direction);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		TileEntity lockerDown = world.getTileEntity(pos.down());
		if (lockerDown != null && lockerDown instanceof LockerTileEntity) {
			return ((LockerTileEntity) lockerDown).canExtractItem(index, stack, direction);
		}

		return super.canExtractItem(index, stack, direction);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return hasUpperLocker() ? super.getRenderBoundingBox().expand(0, 1, 0) : super.getRenderBoundingBox();
	}

	public boolean isUpperLocker() {
		if (this.world == null)
			return false;
		return this.world.getBlockState(pos.down()) == this.world.getBlockState(pos);
	}

	public boolean hasUpperLocker() {
		if (this.world == null)
			return false;
		return this.world.getBlockState(this.pos.up()) == this.world.getBlockState(pos);
	}

	public LockerTileEntity getUpperLocker() {
		if (this.world == null || !hasUpperLocker())
			return null;
		return (LockerTileEntity) this.world.getTileEntity(pos.up());
	}

	public boolean isBottomLocker() {
		if (this.world == null)
			return false;
		return this.world.getBlockState(pos.up()) == this.world.getBlockState(pos);
	}

	public boolean hasBottomLocker() {
		if (this.world == null)
			return false;
		return this.world.getBlockState(this.pos.down()) == this.world.getBlockState(pos);
	}

	public LockerTileEntity getBottomLocker() {
		if (this.world == null || !hasUpperLocker())
			return null;
		return (LockerTileEntity) this.world.getTileEntity(pos.down());
	}

	@Override
	public Block getBlockToUse() {
		return StorageBlocks.LOCKER.get();
	}
}
