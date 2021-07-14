package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.tileentity.ItemTowerTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ItemTowerBlock extends BaseStorageBlock {

	public ItemTowerBlock(Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(3.0F, 6.0F));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ItemTowerTileEntity();
	}

	protected boolean isDoorBlocked(IWorld world, BlockPos pos) {
		return isInvalidBlock(world, pos.above());
	}

	@Override
	protected boolean canBeLocked(World worldIn, BlockPos pos) {
		return false;
	}
}
