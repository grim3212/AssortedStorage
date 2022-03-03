package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.storage.common.util.StorageLockCode;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BaseLockedBlockEntity extends BlockEntity implements ILockeable {

	private StorageLockCode lockCode = StorageLockCode.EMPTY_CODE;

	public BaseLockedBlockEntity(BlockEntityType<BaseLockedBlockEntity> tileEntityType, BlockPos pos, BlockState state) {
		super(StorageBlockEntityTypes.BASE_LOCKED.get(), pos, state);
	}

	public BaseLockedBlockEntity(BlockPos pos, BlockState state) {
		super(StorageBlockEntityTypes.BASE_LOCKED.get(), pos, state);
	}

	@Override
	public boolean isLocked() {
		return this.lockCode != null && this.lockCode != StorageLockCode.EMPTY_CODE;
	}

	@Override
	public String getLockCode() {
		return this.lockCode.getLockCode();
	}

	@Override
	public void setLockCode(String s) {
		if (s == null || s.isEmpty())
			this.lockCode = StorageLockCode.EMPTY_CODE;
		else
			this.lockCode = new StorageLockCode(s);

		this.setChanged();
	}
	
	@Override
	public StorageLockCode getStorageLockCode() {
		return this.lockCode;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.lockCode = StorageLockCode.read(nbt);
	}

	@Override
	protected void saveAdditional(CompoundTag cmp) {
		super.saveAdditional(cmp);
		this.lockCode.write(cmp);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		requestModelDataUpdate();
		if (level instanceof ClientLevel) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
		}
	}
}
