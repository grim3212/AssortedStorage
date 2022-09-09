package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.blockentity.LockedChestBlockEntity;
import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LockedChest extends BaseStorageBlock implements IStorageMaterial {

	private final StorageMaterial material;
	protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

	public LockedChest(StorageMaterial material) {
		super(material.getProps());
		this.material = material;
	}

	@Override
	public StorageMaterial getStorageMaterial() {
		return material;
	}

	@Override
	public StorageType getStorageType() {
		return StorageType.CHEST;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LockedChestBlockEntity(pos, state);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected boolean isDoorBlocked(LevelAccessor world, BlockPos pos) {
		return isInvalidBlock(world, pos.above());
	}

}
