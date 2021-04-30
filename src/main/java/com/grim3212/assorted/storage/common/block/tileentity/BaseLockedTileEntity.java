package com.grim3212.assorted.storage.common.block.tileentity;

import com.grim3212.assorted.storage.common.util.StorageLockCode;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

public class BaseLockedTileEntity extends TileEntity {

	private StorageLockCode lockCode = StorageLockCode.EMPTY_CODE;

	public BaseLockedTileEntity() {
		super(StorageTileEntityTypes.BASE_LOCKED.get());
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

		if (world != null) {
			world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 3);
			if (!world.isRemote) {
				world.updateBlock(pos, getBlockState().getBlock());
			}
		}
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);

		this.readPacketNBT(nbt);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);

		this.writePacketNBT(compound);

		return compound;
	}

	public void writePacketNBT(CompoundNBT cmp) {
		this.lockCode.write(cmp);
	}

	public void readPacketNBT(CompoundNBT cmp) {
		this.lockCode = StorageLockCode.read(cmp);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbtTagCompound = new CompoundNBT();
		writePacketNBT(nbtTagCompound);
		return new SUpdateTileEntityPacket(this.pos, 1, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.readPacketNBT(pkt.getNbtCompound());
		requestModelDataUpdate();
		if (world instanceof ClientWorld) {
			world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 0);
		}
	}
}
