package com.grim3212.assorted.storage.common.block.tileentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.inventory.StorageContainer;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class WarehouseCrateTileEntity extends BaseStorageTileEntity {

	private final Block blockToUse;

	public WarehouseCrateTileEntity() {
		this(Blocks.AIR);
	}

	public WarehouseCrateTileEntity(Block block) {
		super(StorageTileEntityTypes.WAREHOUSE_CRATE.get());
		this.blockToUse = block;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory player, PlayerEntity playerEntity) {
		return StorageContainer.createWarehouseCrateContainer(windowId, player, this);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent(AssortedStorage.MODID + ".container.warehouse_crate");
	}

	@Override
	public Block getBlockToUse() {
		return this.blockToUse;
	}

}
