package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.storage.common.block.LockerBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LockerItem extends BlockItem {

	public LockerItem(Properties builder) {
		super(StorageBlocks.LOCKER.get(), builder);
	}

	@Override
	protected boolean canPlace(BlockItemUseContext context, BlockState state) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();

		if (!super.canPlace(context, state))
			return false;

		if (world.getBlockState(pos.down()).getBlock() == this.getBlock()) {
			return !LockerBlock.isTopLocker(world, pos.down());
		} else if (world.getBlockState(pos.up()).getBlock() == this.getBlock()) {
			return !LockerBlock.isBottomLocker(world, pos.up());
		}

		return true;
	}
}
