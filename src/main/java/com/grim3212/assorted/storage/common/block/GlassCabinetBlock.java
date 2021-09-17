package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.tileentity.GlassCabinetTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class GlassCabinetBlock extends BaseStorageBlock {

	public GlassCabinetBlock(Properties properties) {
		super(properties.strength(3.0f, 5.0f));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new GlassCabinetTileEntity();
	}
}
