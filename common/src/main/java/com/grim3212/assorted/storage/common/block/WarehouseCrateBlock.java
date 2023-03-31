package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.blockentity.WarehouseCrateBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;

public class WarehouseCrateBlock extends BaseStorageBlock {

	private final WoodType type;

	public WarehouseCrateBlock(WoodType type) {
		super(Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(3.0f, 5.0f));
		this.type = type;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new WarehouseCrateBlockEntity(pos, state);
	}

	protected boolean isDoorBlocked(LevelAccessor world, BlockPos pos) {
		return isInvalidBlock(world, pos.above());
	}

	public WoodType getWoodType() {
		return this.type;
	}

}
