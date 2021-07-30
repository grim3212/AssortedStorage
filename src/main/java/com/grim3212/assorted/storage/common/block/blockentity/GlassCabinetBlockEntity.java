package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.inventory.StorageContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class GlassCabinetBlockEntity extends BaseStorageBlockEntity {

	public GlassCabinetBlockEntity(BlockPos pos, BlockState state) {
		super(StorageBlockEntityTypes.GLASS_CABINET.get(), pos, state);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
		return StorageContainer.createGlassCabinetContainer(windowId, player, this);
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent(AssortedStorage.MODID + ".container.glass_cabinet");
	}
}
