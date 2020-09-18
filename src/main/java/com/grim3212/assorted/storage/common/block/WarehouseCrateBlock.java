package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.tileentity.WarehouseCrateTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.WoodType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class WarehouseCrateBlock extends BaseStorageBlock {

	private final WoodType type;

	public WarehouseCrateBlock(WoodType type) {
		super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD));
		this.type = type;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new WarehouseCrateTileEntity(this);
	}

	protected boolean isDoorBlocked(IWorld world, BlockPos pos) {
		return isInvalidBlock(world, pos.up());
	}

	public WoodType getWoodType() {
		return this.type;
	}

}
