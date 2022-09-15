package com.grim3212.assorted.storage.common.block;

import java.util.List;

import javax.annotation.Nullable;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.blockentity.ILockable;
import com.grim3212.assorted.storage.common.block.blockentity.INamed;
import com.grim3212.assorted.storage.common.block.blockentity.LockedBarrelBlockEntity;
import com.grim3212.assorted.storage.common.handler.StorageConfig;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageLockCode;
import com.grim3212.assorted.storage.common.util.StorageMaterial;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

public class LockedBarrelBlock extends Block implements EntityBlock, IStorageMaterial {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

	private final StorageMaterial material;

	public LockedBarrelBlock(StorageMaterial material) {
		this(material, material.getProps());
	}

	public LockedBarrelBlock(StorageMaterial material, Block.Properties props) {
		super(props);
		this.material = material;
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
	}

	@Override
	public StorageMaterial getStorageMaterial() {
		return material;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (!StorageConfig.COMMON.barrelsEnabled.get()) {
			return;
		}

		if (this.getStorageMaterial() == null) {
			ItemStack output = StorageUtil.setCodeOnStack("default", new ItemStack(StorageBlocks.LOCKED_BARREL.get()));
			items.add(output);
			return;
		}

		if (StorageConfig.COMMON.hideUncraftableItems.get() && ForgeRegistries.ITEMS.tags().getTag(this.getStorageMaterial().getMaterial()).size() <= 0) {
			return;
		}

		super.fillItemCategory(group, items);
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {
		if (this.getStorageMaterial() == null) {
			String lockCode = StorageUtil.getCode(worldIn.getBlockEntity(pos));
			ItemStack output = new ItemStack(StorageBlocks.LOCKED_BARREL.get());
			return StorageUtil.setCodeOnStack(lockCode, output);
		}
		return super.getCloneItemStack(worldIn, pos, state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
		BlockEntity te = worldIn.getBlockEntity(pos);

		if (te instanceof ILockable) {
			ILockable tileentity = (ILockable) te;

			if (tileentity.isLocked() && !StorageUtil.canAccess(worldIn, pos, player))
				return -1.0F;
		}

		return super.getDestroyProgress(state, player, worldIn, pos);
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);

		if (tileentity instanceof INamed) {
			if (stack.hasCustomHoverName()) {
				((INamed) tileentity).setCustomName(stack.getHoverName());
			}
		}

		ILockable lockeable = (ILockable) worldIn.getBlockEntity(pos);
		if (lockeable != null) {
			lockeable.setLockCode(StorageUtil.getCode(stack));
		}
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);

			if (tileentity instanceof ILockable) {
				ILockable teStorage = (ILockable) tileentity;

				if (teStorage.isLocked()) {
					ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
					CompoundTag tag = new CompoundTag();
					new StorageLockCode(teStorage.getLockCode()).write(tag);
					lockStack.setTag(tag);
					Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), lockStack);
				}

				Containers.dropContents(worldIn, pos, (WorldlyContainer) tileentity);
				worldIn.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (this.canBeLocked(worldIn, pos) && player.getItemInHand(handIn).getItem() == StorageItems.LOCKSMITH_LOCK.get()) {
			if (BaseStorageBlock.tryPlaceLock(worldIn, pos, player, handIn))
				return InteractionResult.SUCCESS;
		}

		if (player.isShiftKeyDown() && StorageUtil.canAccess(worldIn, pos, player)) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if (tileentity instanceof ILockable) {
				ILockable teStorage = (ILockable) tileentity;

				if (teStorage.isLocked()) {
					ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
					CompoundTag tag = new CompoundTag();
					new StorageLockCode(teStorage.getLockCode()).write(tag);
					lockStack.setTag(tag);

					if (removeLock(worldIn, pos, player)) {
						ItemEntity blockDropped = new ItemEntity(worldIn, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), lockStack);
						if (!worldIn.isClientSide) {
							worldIn.addFreshEntity(blockDropped);
							if (!(player instanceof FakePlayer)) {
								blockDropped.playerTouch(player);
							}
						}
						return InteractionResult.SUCCESS;
					}
				}
			}
		}

		if (StorageUtil.canAccess(worldIn, pos, player)) {
			if (!worldIn.isClientSide) {
				MenuProvider inamedcontainerprovider = this.getMenuProvider(state, worldIn, pos);
				if (inamedcontainerprovider != null) {
					NetworkHooks.openScreen((ServerPlayer) player, inamedcontainerprovider, pos);
					player.awardStat(Stats.OPEN_BARREL);
					PiglinAi.angerNearbyPiglins(player, true);
				}
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	@Nullable
	public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
		BlockEntity tileentity = world.getBlockEntity(pos);
		return tileentity instanceof MenuProvider ? (MenuProvider) tileentity : null;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return (level1, blockPos, blockState, t) -> {
			if (t instanceof LockedBarrelBlockEntity storage) {
				storage.tick();
			}
		};
	}

	@Override
	public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
		super.triggerEvent(state, worldIn, pos, id, param);
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromContainer((Container) worldIn.getBlockEntity(pos));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	protected boolean canBeLocked(Level worldIn, BlockPos pos) {
		return !((ILockable) worldIn.getBlockEntity(pos)).isLocked();
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		String code = StorageUtil.getCode(stack);

		if (!code.isEmpty()) {
			tooltip.add(Component.translatable(AssortedStorage.MODID + ".info.combo", Component.literal(code).withStyle(ChatFormatting.AQUA)));
		}

		tooltip.add(Component.translatable(AssortedStorage.MODID + ".info.level_upgrade_level", Component.literal("" + (material == null ? 0 : material.getStorageLevel())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
	}

	protected boolean removeLock(Level worldIn, BlockPos pos, Player entityplayer) {
		if (this.getStorageMaterial() != null) {
			return BaseStorageBlock.tryRemoveLock(worldIn, pos, entityplayer);
		}

		worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);

		BlockState state = worldIn.getBlockState(pos);
		if (state.getBlock() instanceof LockedBarrelBlock && worldIn.getBlockEntity(pos)instanceof LockedBarrelBlockEntity barrelBE) {
			NonNullList<ItemStack> chestItems = NonNullList.withSize(barrelBE.getContainerSize(), ItemStack.EMPTY);
			for (int i = 0; i < barrelBE.getContainerSize(); i++) {
				chestItems.set(i, barrelBE.getItem(i).copy());
			}
			barrelBE.clearContent();
			barrelBE.setLockCode(null);

			worldIn.setBlock(pos, Blocks.BARREL.defaultBlockState().setValue(BarrelBlock.FACING, state.getValue(LockedBarrelBlock.FACING)), 3);
			if (worldIn.getBlockEntity(pos)instanceof BarrelBlockEntity newBarrelBE) {
				for (int i = 0; i < newBarrelBE.getContainerSize(); i++) {
					newBarrelBE.setItem(i, chestItems.get(i));
				}
			}
		}
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LockedBarrelBlockEntity(pos, state);
	}
}