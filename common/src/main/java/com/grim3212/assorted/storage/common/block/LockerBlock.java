package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.blockentity.BaseStorageBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.LockerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LockerBlock extends BaseStorageBlock {

	public LockerBlock(Properties properties) {
		super(properties.requiresCorrectToolForDrops().strength(3.0F, 6.0F));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LockerBlockEntity(pos, state);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (isDualLocker(worldIn, pos) && isTopLocker(worldIn, pos))
			return super.getShape(state, worldIn, pos, context);

		return ObsidianSafeBlock.SAFE_SHAPE;
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
		if (isDualLocker(worldIn, pos) && isTopLocker(worldIn, pos))
			return super.getDestroyProgress(state, player, worldIn, pos.below());

		return super.getDestroyProgress(state, player, worldIn, pos);
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if ((worldIn.getBlockState(pos.below()).getBlock() == this && worldIn.getBlockState(pos.below(2)).getBlock() == this) || (worldIn.getBlockState(pos.above()).getBlock() == this && worldIn.getBlockState(pos.above(2)).getBlock() == this)) {
			return;
		}

		super.setPlacedBy(worldIn, pos, state, placer, stack);

		BlockEntity tileentitytop = worldIn.getBlockEntity(pos.above());
		if (isBottomLocker(worldIn, pos) && tileentitytop != null && (tileentitytop instanceof BaseStorageBlockEntity)) {
			BaseStorageBlockEntity tileentitythis = (BaseStorageBlockEntity) worldIn.getBlockEntity(pos);
			BaseStorageBlockEntity tileentitystoragetop = (BaseStorageBlockEntity) tileentitytop;

			if (tileentitystoragetop.isLocked()) {
				tileentitythis.setLockCode(tileentitystoragetop.getLockCode());
				tileentitystoragetop.setLockCode(null);
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (isDualLocker(worldIn, pos)) {
			if (isTopLocker(worldIn, pos)) {
				return use(worldIn.getBlockState(pos.below()), worldIn, pos.below(), player, handIn, hit);
			}

			return super.use(state, worldIn, pos, player, handIn, hit);
		}

		return super.use(state, worldIn, pos, player, handIn, hit);
	}

	public static boolean isDualLocker(BlockGetter world, BlockPos pos) {
		return isTopLocker(world, pos) || isBottomLocker(world, pos);
	}

	public static boolean isTopLocker(BlockGetter world, BlockPos pos) {
		return world.getBlockState(pos.below()) == world.getBlockState(pos);
	}

	public static boolean isBottomLocker(BlockGetter world, BlockPos pos) {
		return world.getBlockState(pos.above()) == world.getBlockState(pos);
	}
}
