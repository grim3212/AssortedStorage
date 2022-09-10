package com.grim3212.assorted.storage.common.block;

import java.util.List;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.blockentity.ILockable;
import com.grim3212.assorted.storage.common.block.blockentity.LockedChestBlockEntity;
import com.grim3212.assorted.storage.common.util.StorageMaterial;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LockedChestBlock extends BaseStorageBlock implements IStorageMaterial {

	private final StorageMaterial material;
	protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

	public LockedChestBlock(StorageMaterial material) {
		this(material, material.getProps());
	}

	public LockedChestBlock(StorageMaterial material, Block.Properties props) {
		super(props);
		this.material = material;
	}

	@Override
	public StorageMaterial getStorageMaterial() {
		return material;
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

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (this.getStorageMaterial() == null) {
			ItemStack output = StorageUtil.setCodeOnStack("default", new ItemStack(StorageBlocks.LOCKED_CHEST.get()));
			items.add(output);
		} else {
			super.fillItemCategory(group, items);
		}
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
		if (this.getStorageMaterial() == null) {
			String lockCode = StorageUtil.getCode(worldIn.getBlockEntity(pos));
			ItemStack output = new ItemStack(StorageBlocks.LOCKED_ENDER_CHEST.get());
			return StorageUtil.setCodeOnStack(lockCode, output);
		}
		return super.getCloneItemStack(worldIn, pos, state);
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);

		ILockable lockeable = (ILockable) worldIn.getBlockEntity(pos);
		if (lockeable != null) {
			lockeable.setLockCode(StorageUtil.getCode(stack));
		}
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	protected boolean removeLock(Level worldIn, BlockPos pos, Player entityplayer) {
		if (this.getStorageMaterial() != null) {
			return super.removeLock(worldIn, pos, entityplayer);
		}

		worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);

		BlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() instanceof LockedChestBlock && worldIn.getBlockEntity(pos)instanceof LockedChestBlockEntity chestBE) {
			NonNullList<ItemStack> chestItems = NonNullList.withSize(chestBE.getContainerSize(), ItemStack.EMPTY);
			for (int i = 0; i < chestBE.getContainerSize(); i++) {
				chestItems.set(i, chestBE.getItem(i).copy());
			}
			chestBE.clearContent();
			chestBE.setLockCode(null);

			worldIn.setBlock(pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, state.getValue(LockedChestBlock.FACING)), 3);
			if (worldIn.getBlockEntity(pos)instanceof ChestBlockEntity newChestBE) {
				for (int i = 0; i < newChestBE.getContainerSize(); i++) {
					newChestBE.setItem(i, chestItems.get(i));
				}
			}
		}
		return true;
	}
}
