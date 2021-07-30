package com.grim3212.assorted.storage.common.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.grim3212.assorted.storage.common.block.blockentity.GoldSafeBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.WoodCabinetBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class WoodCabinetBlock extends BaseStorageBlock {

	public WoodCabinetBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new WoodCabinetBlockEntity(pos, state);
	}
}
