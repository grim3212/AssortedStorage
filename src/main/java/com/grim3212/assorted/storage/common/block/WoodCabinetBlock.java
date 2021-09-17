package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.tileentity.WoodCabinetTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class WoodCabinetBlock extends BaseStorageBlock {

	public WoodCabinetBlock(Properties properties) {
		super(properties.strength(3.0f, 5.0f));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new WoodCabinetTileEntity();
	}
}
