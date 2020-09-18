package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.tileentity.GoldSafeTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class GoldSafeBlock extends BaseStorageBlock {

	public GoldSafeBlock(Properties properties) {
		super(properties.setRequiresTool().hardnessAndResistance(3.0F, 6.0F));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new GoldSafeTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return ObsidianSafeBlock.SAFE_SHAPE;
	}

}
