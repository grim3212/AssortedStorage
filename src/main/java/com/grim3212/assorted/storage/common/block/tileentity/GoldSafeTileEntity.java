package com.grim3212.assorted.storage.common.block.tileentity;

import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.GoldSafeBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.inventory.StorageContainer;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GoldSafeTileEntity extends BaseStorageTileEntity {

	public GoldSafeTileEntity() {
		super(StorageTileEntityTypes.GOLD_SAFE.get(), 36);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory player, PlayerEntity playerEntity) {
		return StorageContainer.createGoldSafeContainer(windowId, player, this);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent(AssortedStorage.MODID + ".container.gold_safe");
	}

	protected static final int[] GOLD_SAFE_SLOTS = IntStream.range(0, 36).toArray();

	@Override
	public int[] getSlotsForFace(Direction side) {
		return GOLD_SAFE_SLOTS;
	}

	@Override
	public Block getBlockToUse() {
		return StorageBlocks.GOLD_SAFE.get();
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
		return !(Block.byItem(itemStackIn.getItem()) instanceof GoldSafeBlock) && super.canPlaceItemThroughFace(index, itemStackIn, direction);
	}
}
