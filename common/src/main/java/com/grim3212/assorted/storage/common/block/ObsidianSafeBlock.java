package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.blockentity.ObsidianSafeBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ObsidianSafeBlock extends BaseStorageBlock {

	private static final VoxelShape START_SHAPE = Block.box(0.0D, 3.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape LEG_1 = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 3.0D);
	private static final VoxelShape LEG_2 = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D);
	private static final VoxelShape LEG_3 = Block.box(13.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D);
	private static final VoxelShape LEG_4 = Block.box(0.0D, 0.0D, 13.0D, 3.0D, 3.0D, 16.0D);
	public static final VoxelShape SAFE_SHAPE = Shapes.or(START_SHAPE, LEG_1, LEG_2, LEG_3, LEG_4);

	public ObsidianSafeBlock(Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(50.0F, 1200.0F));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ObsidianSafeBlockEntity(pos, state);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SAFE_SHAPE;
	}

}
