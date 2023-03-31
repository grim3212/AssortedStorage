package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.DualLockerInventory;
import com.grim3212.assorted.storage.common.inventory.LockerContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.stream.IntStream;

public class LockerBlockEntity extends BaseStorageBlockEntity {

    public LockerBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.LOCKER.get(), pos, state, 45);
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
        BlockEntity lockerUp = level.getBlockEntity(worldPosition.above());

        if (lockerUp != null && lockerUp instanceof LockerBlockEntity) {
            return LockerContainer.createDualLockerContainer(windowId, player, new DualLockerInventory(this, (LockerBlockEntity) lockerUp));
        }

        return LockerContainer.createLockerContainer(windowId, player, this);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(Constants.MOD_ID + ".container.locker");
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
            return lockerItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private LazyOptional<?> lockerItemHandler = LazyOptional.of(() -> createSidedHandler());

    protected IItemHandler createSidedHandler() {
        BlockEntity lockerUp = level.getBlockEntity(worldPosition.above());
        if (lockerUp != null && lockerUp instanceof LockerBlockEntity) {
            return new SidedInvWrapper(new DualLockerInventory(this, (LockerBlockEntity) lockerUp), null);
        }

        BlockEntity lockerDown = level.getBlockEntity(worldPosition.below());
        if (lockerDown != null && lockerDown instanceof LockerBlockEntity) {
            return new SidedInvWrapper(new DualLockerInventory((LockerBlockEntity) lockerDown, this), null);
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
        BlockEntity lockerDown = level.getBlockEntity(worldPosition.below());
        if (lockerDown != null && lockerDown instanceof LockerBlockEntity) {
            return ((LockerBlockEntity) lockerDown).canPlaceItemThroughFace(index, itemStackIn, direction);
        }

        return super.canPlaceItemThroughFace(index, itemStackIn, direction);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        BlockEntity lockerDown = level.getBlockEntity(worldPosition.below());
        if (lockerDown != null && lockerDown instanceof LockerBlockEntity) {
            return ((LockerBlockEntity) lockerDown).canTakeItemThroughFace(index, stack, direction);
        }

        return super.canTakeItemThroughFace(index, stack, direction);
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction, String lockCode, boolean force) {
        BlockEntity lockerDown = level.getBlockEntity(worldPosition.below());
        if (lockerDown != null && lockerDown instanceof LockerBlockEntity) {
            return ((LockerBlockEntity) lockerDown).canPlaceItemThroughFace(index, itemStackIn, direction, lockCode, force);
        }

        return super.canPlaceItemThroughFace(index, itemStackIn, direction, lockCode, force);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction, String lockCode, boolean force) {
        BlockEntity lockerDown = level.getBlockEntity(worldPosition.below());
        if (lockerDown != null && lockerDown instanceof LockerBlockEntity) {
            return ((LockerBlockEntity) lockerDown).canTakeItemThroughFace(index, stack, direction, lockCode, force);
        }

        return super.canTakeItemThroughFace(index, stack, direction, lockCode, force);
    }

    @Override
    public AABB getRenderBoundingBox() {
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

    public LockerBlockEntity getUpperLocker() {
        if (this.level == null || !hasUpperLocker())
            return null;
        return (LockerBlockEntity) this.level.getBlockEntity(worldPosition.above());
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

    public LockerBlockEntity getBottomLocker() {
        if (this.level == null || !hasUpperLocker())
            return null;
        return (LockerBlockEntity) this.level.getBlockEntity(worldPosition.below());
    }
}
