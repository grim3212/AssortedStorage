package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.tileentity.ObsidianSafeTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class ObsidianSafeBlock extends BaseStorageBlock {

	private static final VoxelShape START_SHAPE = Block.makeCuboidShape(0.0D, 3.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape LEG_1 = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 3.0D);
	private static final VoxelShape LEG_2 = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D);
	private static final VoxelShape LEG_3 = Block.makeCuboidShape(13.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D);
	private static final VoxelShape LEG_4 = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 3.0D, 3.0D, 16.0D);
	public static final VoxelShape SAFE_SHAPE = VoxelShapes.or(START_SHAPE, LEG_1, LEG_2, LEG_3, LEG_4);

	public ObsidianSafeBlock(Properties properties) {
		super(properties.setRequiresTool().hardnessAndResistance(50.0F, 1200.0F));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ObsidianSafeTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SAFE_SHAPE;
	}
}
