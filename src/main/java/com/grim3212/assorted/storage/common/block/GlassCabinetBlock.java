package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.blockentity.GlassCabinetBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GlassCabinetBlock extends BaseStorageBlock {

	public GlassCabinetBlock(Properties properties) {
		super(properties.strength(3.0f, 5.0f));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GlassCabinetBlockEntity(pos, state);
	}
}
