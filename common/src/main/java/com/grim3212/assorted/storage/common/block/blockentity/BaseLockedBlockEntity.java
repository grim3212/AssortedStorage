package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BaseLockedBlockEntity extends BlockEntity implements ILockable {

    private String lockCode = "";

    public BaseLockedBlockEntity(BlockEntityType<BaseLockedBlockEntity> tileEntityType, BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.BASE_LOCKED.get(), pos, state);
    }

    public BaseLockedBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.BASE_LOCKED.get(), pos, state);
    }

    @Override
    public boolean isLocked() {
        return this.lockCode != null && !this.lockCode.isEmpty();
    }

    @Override
    public String getLockCode() {
        return this.lockCode;
    }

    @Override
    public void setLockCode(String s) {
        if (s == null || s.isEmpty())
            this.lockCode = "";
        else
            this.lockCode = s;

        this.setChanged();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.lockCode = StorageUtil.readLock(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        StorageUtil.writeLock(output, this.lockCode);
    }

    /**
     * A door's two halves share one lock, so only the upper half drops it. This used to live in
     * the block's {@code onRemove}, which no longer sees the block entity.
     */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);

        if (this.level != null && this.isLocked() && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            Containers.dropItemStack(this.level, pos.getX(), pos.getY(), pos.getZ(), StorageUtil.setCodeOnStack(this.lockCode, new ItemStack(StorageItems.LOCKSMITH_LOCK.get())));
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

}
