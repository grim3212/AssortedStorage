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
		TileEntity lockerUp = level.getBlockEntity(worldPosition.above());

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
		if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return lockerItemHandler.cast();
		}
		return super.getCapability(cap, side);
	}

	private LazyOptional<?> lockerItemHandler = LazyOptional.of(() -> createSidedHandler());

	protected IItemHandler createSidedHandler() {
		TileEntity lockerUp = level.getBlockEntity(worldPosition.above());
		if (lockerUp != null && lockerUp instanceof LockerTileEntity) {
			return new SidedInvWrapper(new DualLockerInventory(this, (LockerTileEntity) lockerUp), null);
		}

		TileEntity lockerDown = level.getBlockEntity(worldPosition.below());
		if (lockerDown != null && lockerDown instanceof LockerTileEntity) {
			return new SidedInvWrapper(new DualLockerInventory((LockerTileEntity) lockerDown, this), null);
		}

		return super.createSidedHandler();
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		this.lockerItemHandler.invalidate();
	}

	protected static final int[] LOCKER_SLOTS = IntStream.range(0, 45).toArray();

	@Override
	public int[] getSlotsForFace(Direction side) {
		return LOCKER_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		TileEntity lockerDown = level.getBlockEntity(worldPosition.below());
		if (lockerDown != null && lockerDown instanceof LockerTileEntity) {
			return ((LockerTileEntity) lockerDown).canPlaceItemThroughFace(index, itemStackIn, direction);
		}

		return super.canPlaceItemThroughFace(index, itemStackIn, direction);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		TileEntity lockerDown = level.getBlockEntity(worldPosition.below());
		if (lockerDown != null && lockerDown instanceof LockerTileEntity) {
			return ((LockerTileEntity) lockerDown).canTakeItemThroughFace(index, stack, direction);
		}

		return super.canTakeItemThroughFace(index, stack, direction);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return hasUpperLocker() ? super.getRenderBoundingBox().expandTowards(0, 1, 0) : super.getRenderBoundingBox();
	}

	public boolean isUpperLocker() {
		if (this.level == null)
			return false;
		return this.level.getBlockState(worldPosition.below()) == this.level.getBlockState(worldPosition);
	}

	public boolean hasUpperLocker() {
		if (this.level == null)
			return false;
		return this.level.getBlockState(this.worldPosition.above()) == this.level.getBlockState(worldPosition);
	}

	public LockerTileEntity getUpperLocker() {
		if (this.level == null || !hasUpperLocker())
			return null;
		return (LockerTileEntity) this.level.getBlockEntity(worldPosition.above());
	}

	public boolean isBottomLocker() {
		if (this.level == null)
			return false;
		return this.level.getBlockState(worldPosition.above()) == this.level.getBlockState(worldPosition);
	}

	public boolean hasBottomLocker() {
		if (this.level == null)
			return false;
		return this.level.getBlockState(this.worldPosition.below()) == this.level.getBlockState(worldPosition);
	}

	public LockerTileEntity getBottomLocker() {
		if (this.level == null || !hasUpperLocker())
			return null;
		return (LockerTileEntity) this.level.getBlockEntity(worldPosition.below());
	}

	@Override
	public Block getBlockToUse() {
		return StorageBlocks.LOCKER.get();
	}
}
