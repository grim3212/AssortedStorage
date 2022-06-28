package com.grim3212.assorted.storage.common.block.blockentity;

import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.GoldSafeBlock;
import com.grim3212.assorted.storage.common.inventory.StorageContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class GoldSafeBlockEntity extends BaseStorageBlockEntity {

	public GoldSafeBlockEntity(BlockPos pos, BlockState state) {
		super(StorageBlockEntityTypes.GOLD_SAFE.get(), pos, state, 36);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
		return StorageContainer.createGoldSafeContainer(windowId, player, this);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable(AssortedStorage.MODID + ".container.gold_safe");
	}

	protected static final int[] GOLD_SAFE_SLOTS = IntStream.range(0, 36).toArray();

	@Override
	public int[] getSlotsForFace(Direction side) {
		return GOLD_SAFE_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
		return !(Block.byItem(itemStackIn.getItem()) instanceof GoldSafeBlock) && super.canPlaceItemThroughFace(index, itemStackIn, direction);
	}
}
