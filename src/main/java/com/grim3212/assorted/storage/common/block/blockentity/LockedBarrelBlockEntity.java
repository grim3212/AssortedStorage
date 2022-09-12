package com.grim3212.assorted.storage.common.block.blockentity;

import java.util.stream.IntStream;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.LockedBarrelBlock;
import com.grim3212.assorted.storage.common.inventory.LockedMaterialContainer;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.ModelData;

public class LockedBarrelBlockEntity extends BaseStorageBlockEntity {

	private final StorageMaterial storageMaterial;
	protected final int[] slots;

	public LockedBarrelBlockEntity(BlockPos pos, BlockState state) {
		super(StorageBlockEntityTypes.LOCKED_BARREL.get(), pos, state);

		if (state.getBlock()instanceof LockedBarrelBlock lockedBarrel) {
			this.storageMaterial = lockedBarrel.getStorageMaterial();
			setStartingContents(this.storageMaterial != null ? this.storageMaterial.totalItems() : 27);
		} else {
			// Default to regular chest
			this.storageMaterial = null;
			this.setStartingContents(27);
		}

		this.slots = IntStream.range(0, this.getContainerSize()).toArray();
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return this.slots;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
		return new LockedMaterialContainer(StorageContainerTypes.BARRELS.get(storageMaterial).get(), windowId, player, this, storageMaterial, false);
	}

	@Override
	protected Component getDefaultName() {
		if (this.storageMaterial == null) {
			return Component.translatable(AssortedStorage.MODID + ".container.locked_barrel");
		}

		return Component.translatable(AssortedStorage.MODID + ".container.barrel_" + this.storageMaterial.toString());
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		requestModelDataUpdate();
		if (level instanceof ClientLevel) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
		}
	}

	@Override
	public void setLockCode(String s) {
		super.setLockCode(s);
		requestModelDataUpdate();
		if (level instanceof ClientLevel) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
		}
	}

	@Override
	public ModelData getModelData() {
		return ModelData.builder().with(ModelProperties.IS_LOCKED, this.isLocked()).build();
	}

	@Override
	protected SoundEvent openSound() {
		return SoundEvents.BARREL_OPEN;
	}

	@Override
	protected SoundEvent closeSound() {
		return SoundEvents.BARREL_CLOSE;
	}

	@Override
	public int getNumberOfPlayersUsing(Level world, BaseStorageBlockEntity lockableTileEntity, int x, int y, int z) {
		int i = 0;

		for (Player playerentity : world.getEntitiesOfClass(Player.class, new AABB((double) ((float) x - 5.0F), (double) ((float) y - 5.0F), (double) ((float) z - 5.0F), (double) ((float) (x + 1) + 5.0F), (double) ((float) (y + 1) + 5.0F), (double) ((float) (z + 1) + 5.0F)))) {
			if (playerentity.containerMenu instanceof LockedMaterialContainer) {
				++i;
			}
		}

		return i;
	}
}
