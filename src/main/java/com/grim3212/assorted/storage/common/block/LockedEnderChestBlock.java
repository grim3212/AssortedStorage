package com.grim3212.assorted.storage.common.block;

import java.util.List;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.blockentity.ILockeable;
import com.grim3212.assorted.storage.common.block.blockentity.LockedEnderChestBlockEntity;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LockedEnderChestBlock extends BaseStorageBlock {

	protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

	public LockedEnderChestBlock(Properties props) {
		super(props);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	protected boolean isDoorBlocked(LevelAccessor world, BlockPos pos) {
		return isInvalidBlock(world, pos.above());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LockedEnderChestBlockEntity(pos, state);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		ItemStack output = StorageUtil.setCodeOnStack("default", new ItemStack(StorageBlocks.LOCKED_ENDER_CHEST.get()));
		items.add(output);
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		String code = StorageUtil.getCode(stack);

		if (!code.isEmpty()) {
			tooltip.add(Component.translatable(AssortedStorage.MODID + ".info.combo", Component.literal(code).withStyle(ChatFormatting.AQUA)));
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {
		String lockCode = StorageUtil.getCode(worldIn.getBlockEntity(pos));
		ItemStack output = new ItemStack(StorageBlocks.LOCKED_ENDER_CHEST.get());
		return StorageUtil.setCodeOnStack(lockCode, output);
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);

		ILockeable lockeable = (ILockeable) worldIn.getBlockEntity(pos);
		if (lockeable != null) {
			lockeable.setLockCode(StorageUtil.getCode(stack));
		}
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
			worldIn.removeBlockEntity(pos);
		}
	}

	@Override
	protected boolean removeLock(Level worldIn, BlockPos pos, Player entityplayer) {
		worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);

		BlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() instanceof LockedEnderChestBlock) {
			worldIn.setBlock(pos, Blocks.ENDER_CHEST.defaultBlockState().setValue(EnderChestBlock.FACING, state.getValue(LockedEnderChestBlock.FACING)), 3);
		}
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		for (int i = 0; i < 3; ++i) {
			int j = random.nextInt(2) * 2 - 1;
			int k = random.nextInt(2) * 2 - 1;
			double d0 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
			double d1 = (double) ((float) pos.getY() + random.nextFloat());
			double d2 = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
			double d3 = (double) (random.nextFloat() * (float) j);
			double d4 = ((double) random.nextFloat() - 0.5D) * 0.125D;
			double d5 = (double) (random.nextFloat() * (float) k);
			level.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
		}
	}
}
