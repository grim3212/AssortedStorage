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
		super(properties.setRequiresTool().hardnessAndResistance(3.0F, 6.0F));
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
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		if (isDualLocker(worldIn, pos) && isTopLocker(worldIn, pos))
			return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos.down());

		return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if ((worldIn.getBlockState(pos.down()).getBlock() == this && worldIn.getBlockState(pos.down(2)).getBlock() == this) || (worldIn.getBlockState(pos.up()).getBlock() == this && worldIn.getBlockState(pos.up(2)).getBlock() == this)) {
			return;
		}

		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

		TileEntity tileentitytop = worldIn.getTileEntity(pos.up());
		if (isBottomLocker(worldIn, pos) && tileentitytop != null && (tileentitytop instanceof BaseStorageTileEntity)) {
			BaseStorageTileEntity tileentitythis = (BaseStorageTileEntity) worldIn.getTileEntity(pos);
			BaseStorageTileEntity tileentitystoragetop = (BaseStorageTileEntity) tileentitytop;

			if (tileentitystoragetop.isLocked()) {
				tileentitythis.setLockCode(tileentitystoragetop.getLockCode());
				tileentitystoragetop.setLockCode(null);
			}
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (isDualLocker(worldIn, pos)) {
			if (isTopLocker(worldIn, pos)) {
				return onBlockActivated(worldIn.getBlockState(pos.down()), worldIn, pos.down(), player, handIn, hit);
			}

			return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
		}

		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}

	public static boolean isDualLocker(IBlockReader world, BlockPos pos) {
		return isTopLocker(world, pos) || isBottomLocker(world, pos);
	}

	public static boolean isTopLocker(IBlockReader world, BlockPos pos) {
		return world.getBlockState(pos.down()) == world.getBlockState(pos);
	}

	public static boolean isBottomLocker(IBlockReader world, BlockPos pos) {
		return world.getBlockState(pos.up()) == world.getBlockState(pos);
	}
}
