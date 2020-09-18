package com.grim3212.assorted.storage.common.block.tileentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.inventory.StorageContainer;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GlassCabinetTileEntity extends BaseStorageTileEntity {

	public GlassCabinetTileEntity() {
		super(StorageTileEntityTypes.GLASS_CABINET.get());
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory player, PlayerEntity playerEntity) {
		return StorageContainer.createGlassCabinetContainer(windowId, player, this);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent(AssortedStorage.MODID + ".container.glass_cabinet");
	}

	@Override
	public Block getBlockToUse() {
		return StorageBlocks.GLASS_CABINET.get();
	}
}
