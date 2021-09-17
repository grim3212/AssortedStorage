package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.blockentity.WoodCabinetBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WoodCabinetBlock extends BaseStorageBlock {

	public WoodCabinetBlock(Properties properties) {
		super(properties.strength(3.0f, 5.0f));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new WoodCabinetBlockEntity(pos, state);
	}
}
