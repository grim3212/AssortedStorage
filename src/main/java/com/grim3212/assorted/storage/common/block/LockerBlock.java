package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.tileentity.BaseStorageTileEntity;
import com.grim3212.assorted.storage.common.block.tileentity.LockerTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class LockerBlock extends BaseStorageBlock {

	public LockerBlock(Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(3.0F, 6.0F));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new LockerTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (isDualLocker(worldIn, pos) && isTopLocker(worldIn, pos))
			return super.getShape(state, worldIn, pos, context);

		return ObsidianSafeBlock.SAFE_SHAPE;
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		if (isDualLocker(worldIn, pos) && isTopLocker(worldIn, pos))
			return super.getDestroyProgress(state, player, worldIn, pos.below());

		return super.getDestroyProgress(state, player, worldIn, pos);
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if ((worldIn.getBlockState(pos.below()).getBlock() == this && worldIn.getBlockState(pos.below(2)).getBlock() == this) || (worldIn.getBlockState(pos.above()).getBlock() == this && worldIn.getBlockState(pos.above(2)).getBlock() == this)) {
			return;
		}

		super.setPlacedBy(worldIn, pos, state, placer, stack);

		TileEntity tileentitytop = worldIn.getBlockEntity(pos.above());
		if (isBottomLocker(worldIn, pos) && tileentitytop != null && (tileentitytop instanceof BaseStorageTileEntity)) {
			BaseStorageTileEntity tileentitythis = (BaseStorageTileEntity) worldIn.getBlockEntity(pos);
			BaseStorageTileEntity tileentitystoragetop = (BaseStorageTileEntity) tileentitytop;

			if (tileentitystoragetop.isLocked()) {
				tileentitythis.setLockCode(tileentitystoragetop.getLockCode());
				tileentitystoragetop.setLockCode(null);
			}
		}
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (isDualLocker(worldIn, pos)) {
			if (isTopLocker(worldIn, pos)) {
				return use(worldIn.getBlockState(pos.below()), worldIn, pos.below(), player, handIn, hit);
			}

			return super.use(state, worldIn, pos, player, handIn, hit);
		}

		return super.use(state, worldIn, pos, player, handIn, hit);
	}

	public static boolean isDualLocker(IBlockReader world, BlockPos pos) {
		return isTopLocker(world, pos) || isBottomLocker(world, pos);
	}

	public static boolean isTopLocker(IBlockReader world, BlockPos pos) {
		return world.getBlockState(pos.below()) == world.getBlockState(pos);
	}

	public static boolean isBottomLocker(IBlockReader world, BlockPos pos) {
		return world.getBlockState(pos.above()) == world.getBlockState(pos);
	}
}
