package com.grim3212.assorted.storage.common.item;

import java.util.function.Consumer;

import com.grim3212.assorted.storage.client.blockentity.item.LockerBEWLR;
import com.grim3212.assorted.storage.common.block.LockerBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.IItemRenderProperties;

public class LockerItem extends BlockItem {

	public LockerItem(Properties builder) {
		super(StorageBlocks.LOCKER.get(), builder);
	}

	@Override
	protected boolean canPlace(BlockPlaceContext context, BlockState state) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();

		if (!super.canPlace(context, state))
			return false;

		if (world.getBlockState(pos.below()).getBlock() == this.getBlock()) {
			return !LockerBlock.isTopLocker(world, pos.below());
		} else if (world.getBlockState(pos.above()).getBlock() == this.getBlock()) {
			return !LockerBlock.isBottomLocker(world, pos.above());
		}

		return true;
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return LockerBEWLR.LOCKER_ITEM_RENDERER;
			}
		});
	}
}
