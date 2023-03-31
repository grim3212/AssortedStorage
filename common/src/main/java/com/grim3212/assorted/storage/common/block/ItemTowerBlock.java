package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.blockentity.ItemTowerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ItemTowerBlock extends BaseStorageBlock {

	public ItemTowerBlock(Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(3.0F, 6.0F));
	}

	protected boolean isDoorBlocked(LevelAccessor world, BlockPos pos) {
		return isInvalidBlock(world, pos.above());
	}

	@Override
	protected boolean canBeLocked(Level worldIn, BlockPos pos) {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ItemTowerBlockEntity(pos, state);
	}
}
