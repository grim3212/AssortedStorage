package com.grim3212.assorted.storage.common.block;

import java.util.List;
import java.util.Random;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.tileentity.ILockeable;
import com.grim3212.assorted.storage.common.block.tileentity.LockedEnderChestTileEntity;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LockedEnderChestBlock extends BaseStorageBlock {

	protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

	public LockedEnderChestBlock(Properties props) {
		super(props);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	protected boolean isDoorBlocked(IWorld world, BlockPos pos) {
		return isInvalidBlock(world, pos.above());
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new LockedEnderChestTileEntity();
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
		ItemStack output = StorageUtil.setCodeOnStack("default", new ItemStack(StorageBlocks.LOCKED_ENDER_CHEST.get()));
		items.add(output);
	}

	@Override
	public void appendHoverText(ItemStack stack, IBlockReader level, List<ITextComponent> tooltip, ITooltipFlag flag) {
		String code = StorageUtil.getCode(stack);

		if (!code.isEmpty()) {
			tooltip.add(new TranslationTextComponent(AssortedStorage.MODID + ".info.combo", new StringTextComponent(code).withStyle(TextFormatting.AQUA)));
		}
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader worldIn, BlockPos pos, BlockState state) {
		String lockCode = StorageUtil.getCode(worldIn.getBlockEntity(pos));
		ItemStack output = new ItemStack(StorageBlocks.LOCKED_ENDER_CHEST.get());
		return StorageUtil.setCodeOnStack(lockCode, output);
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);

		ILockeable lockeable = (ILockeable) worldIn.getBlockEntity(pos);
		if (lockeable != null) {
			lockeable.setLockCode(StorageUtil.getCode(stack));
		}
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.hasTileEntity() && (!state.is(newState.getBlock()) || !newState.hasTileEntity())) {
			worldIn.removeBlockEntity(pos);
		}
	}

	@Override
	protected boolean removeLock(World worldIn, BlockPos pos, PlayerEntity entityplayer) {
		worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundCategory.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);

		BlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() instanceof LockedEnderChestBlock) {
			worldIn.setBlock(pos, Blocks.ENDER_CHEST.defaultBlockState().setValue(EnderChestBlock.FACING, state.getValue(LockedEnderChestBlock.FACING)), 3);
		}
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World level, BlockPos pos, Random random) {
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
