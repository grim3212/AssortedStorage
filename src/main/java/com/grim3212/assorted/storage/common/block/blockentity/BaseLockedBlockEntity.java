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

public class BaseLockedBlockEntity extends BlockEntity {

	private StorageLockCode lockCode = StorageLockCode.EMPTY_CODE;

	public BaseLockedBlockEntity(BlockEntityType<BaseLockedBlockEntity> tileEntityType, BlockPos pos, BlockState state) {
		super(StorageBlockEntityTypes.BASE_LOCKED.get(), pos, state);
	}

	public BaseLockedBlockEntity(BlockPos pos, BlockState state) {
		super(StorageBlockEntityTypes.BASE_LOCKED.get(), pos, state);
	}

	public boolean isLocked() {
		return this.lockCode != null && this.lockCode != StorageLockCode.EMPTY_CODE;
	}

	public String getLockCode() {
		return this.lockCode.getLockCode();
	}

	public void setLockCode(String s) {
		if (s == null || s.isEmpty())
			this.lockCode = StorageLockCode.EMPTY_CODE;
		else
			this.lockCode = new StorageLockCode(s);

		if (level != null) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
			if (!level.isClientSide) {
				level.blockUpdated(worldPosition, getBlockState().getBlock());
			}
		}
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);

		this.readPacketNBT(nbt);
	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		super.save(compound);

		this.writePacketNBT(compound);

		return compound;
	}

	public void writePacketNBT(CompoundTag cmp) {
		this.lockCode.write(cmp);
	}

	public void readPacketNBT(CompoundTag cmp) {
		this.lockCode = StorageLockCode.read(cmp);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return save(new CompoundTag());
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag nbtTagCompound = new CompoundTag();
		writePacketNBT(nbtTagCompound);
		return new ClientboundBlockEntityDataPacket(this.worldPosition, 1, nbtTagCompound);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		this.readPacketNBT(pkt.getTag());
		requestModelDataUpdate();
		if (level instanceof ClientLevel) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
		}
	}
}
