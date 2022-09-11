package com.grim3212.assorted.storage.common.item;

import java.util.List;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.LockedChestBlock;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.BaseStorageBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.LockedChestBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.LockedShulkerBoxBlockEntity;
import com.grim3212.assorted.storage.common.handler.StorageConfig;
import com.grim3212.assorted.storage.common.util.StorageMaterial;
import com.grim3212.assorted.storage.common.util.StorageTags;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class LevelUpgradeItem extends Item {

	private final StorageMaterial storageMaterial;

	public LevelUpgradeItem(Properties properties, StorageMaterial storageMaterial) {
		super(properties.stacksTo(16));
		this.storageMaterial = storageMaterial;
	}

	public StorageMaterial getStorageMaterial() {
		return storageMaterial;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (!Screen.hasShiftDown()) {
			tooltip.add(Component.translatable(AssortedStorage.MODID + ".info.level_upgrade_shift").withStyle(ChatFormatting.GRAY));
		} else {
			tooltip.add(Component.translatable(AssortedStorage.MODID + ".info.level_upgrade"));

			TagKey<Item> supportedBlocksTag = null;

			switch (storageMaterial.getStorageLevel()) {
				case 1:
					supportedBlocksTag = StorageTags.Items.CAN_UPGRADE_LEVEL_1;
					break;
				case 2:
					supportedBlocksTag = StorageTags.Items.CAN_UPGRADE_LEVEL_2;
					break;
				case 3:
					supportedBlocksTag = StorageTags.Items.CAN_UPGRADE_LEVEL_3;
					break;
				case 4:
					supportedBlocksTag = StorageTags.Items.CAN_UPGRADE_LEVEL_4;
					break;
				default:
					supportedBlocksTag = StorageTags.Items.CAN_UPGRADE_LEVEL_0;
					break;
			}

			ForgeRegistries.ITEMS.tags().getTag(supportedBlocksTag).forEach((i) -> {
				tooltip.add(Component.literal(" * ").append(i.getDescription()).withStyle(ChatFormatting.AQUA));
			});
		}
	}

	@Override
	protected boolean allowedIn(CreativeModeTab tab) {
		return StorageConfig.COMMON.upgradesEnabled.get() ? super.allowedIn(tab) : false;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!StorageConfig.COMMON.upgradesEnabled.get()) {
			return super.useOn(context);
		}

		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();

		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();
		ItemStack itemstack = player.getItemInHand(hand);

		String currentLockCode = null;
		Component currentCustomName = null;
		NonNullList<ItemStack> currentItems = null;

		BlockState newState = null;
		BlockEntity newBlockEntity = null;

		if (world.getBlockState(pos).getBlock()instanceof LockedChestBlock chestBlock) {
			int currentStorageLevel = chestBlock.getStorageMaterial() != null ? chestBlock.getStorageMaterial().getStorageLevel() : 0;

			if (currentStorageLevel == this.storageMaterial.getStorageLevel() - 1) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null && blockEntity instanceof LockedChestBlockEntity storageBE) {
					if (storageBE.getNumberOfPlayersUsing(world, storageBE) > 0) {
						return InteractionResult.PASS;
					}

					if (storageBE.isLocked() && !StorageUtil.canAccess(world, pos, player)) {
						return InteractionResult.PASS;
					}

					currentLockCode = storageBE.getLockCode();
					currentCustomName = storageBE.getCustomName();
					currentItems = storageBE.getItems();

					newState = StorageBlocks.CHESTS.get(storageMaterial).get().defaultBlockState().setValue(BaseStorageBlock.FACING, world.getBlockState(pos).getValue(BaseStorageBlock.FACING));
					newBlockEntity = new LockedChestBlockEntity(pos, newState);
				}
			}
		} else if (world.getBlockState(pos).getBlock()instanceof LockedShulkerBoxBlock chestBlock) {
			int currentStorageLevel = chestBlock.getStorageMaterial() != null ? chestBlock.getStorageMaterial().getStorageLevel() : 0;

			if (currentStorageLevel == this.storageMaterial.getStorageLevel() - 1) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null && blockEntity instanceof LockedShulkerBoxBlockEntity storageBE) {
					if (!storageBE.isClosed()) {
						return InteractionResult.PASS;
					}

					if (storageBE.isLocked() && !StorageUtil.canAccess(world, pos, player)) {
						return InteractionResult.PASS;
					}

					currentLockCode = storageBE.getLockCode();
					currentCustomName = storageBE.getCustomName();
					currentItems = storageBE.getItems();

					newState = StorageBlocks.SHULKERS.get(storageMaterial).get().defaultBlockState().setValue(ShulkerBoxBlock.FACING, world.getBlockState(pos).getValue(ShulkerBoxBlock.FACING));

					LockedShulkerBoxBlockEntity newShulkerEntity = new LockedShulkerBoxBlockEntity(pos, newState);
					newShulkerEntity.setColor(storageBE.getColor());
					newBlockEntity = newShulkerEntity;
				}
			}
		} else if (world.getBlockState(pos).getBlock()instanceof ChestBlock chestToUpgrade) {
			if (this.storageMaterial.getStorageLevel() == 0 || this.storageMaterial.getStorageLevel() == 1) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null && blockEntity instanceof ChestBlockEntity chestBE) {
					if (ChestBlockEntity.getOpenCount(world, pos) > 0) {
						return InteractionResult.PASS;
					}

					if (!chestBE.canOpen(player)) {
						return InteractionResult.PASS;
					}

					currentCustomName = chestBE.getCustomName();

					currentItems = NonNullList.withSize(chestBE.getContainerSize(), ItemStack.EMPTY);

					for (int slot = 0; slot < chestBE.getContainerSize(); slot++) {
						currentItems.set(slot, chestBE.getItem(slot));
					}

					newState = StorageBlocks.CHESTS.get(storageMaterial).get().defaultBlockState().setValue(BaseStorageBlock.FACING, world.getBlockState(pos).getValue(ChestBlock.FACING));
					newBlockEntity = new LockedChestBlockEntity(pos, newState);
				}
			}
		} else if (world.getBlockState(pos).getBlock()instanceof ShulkerBoxBlock shulkerToUpgrade) {
			if (this.storageMaterial.getStorageLevel() == 0 || this.storageMaterial.getStorageLevel() == 1) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null && blockEntity instanceof ShulkerBoxBlockEntity shulkerBE) {
					if (!shulkerBE.isClosed()) {
						return InteractionResult.PASS;
					}

					if (!shulkerBE.canOpen(player)) {
						return InteractionResult.PASS;
					}

					currentCustomName = shulkerBE.getCustomName();

					currentItems = NonNullList.withSize(shulkerBE.getContainerSize(), ItemStack.EMPTY);

					for (int slot = 0; slot < shulkerBE.getContainerSize(); slot++) {
						currentItems.set(slot, shulkerBE.getItem(slot));
					}

					newState = StorageBlocks.SHULKERS.get(storageMaterial).get().defaultBlockState().setValue(ShulkerBoxBlock.FACING, world.getBlockState(pos).getValue(ShulkerBoxBlock.FACING));
					LockedShulkerBoxBlockEntity newShulkerEntity = new LockedShulkerBoxBlockEntity(pos, newState);
					newShulkerEntity.setColor(ShulkerBoxBlock.getColorFromBlock(shulkerToUpgrade));
					newBlockEntity = newShulkerEntity;
				}
			}
		}

		if (newState == null || newBlockEntity == null) {
			return InteractionResult.PASS;
		}

		world.removeBlockEntity(pos);
		world.removeBlock(pos, false);

		world.setBlock(pos, newState, 3);
		world.setBlockEntity(newBlockEntity);

		world.sendBlockUpdated(pos, newState, newState, 3);

		BlockEntity freshlySetBE = world.getBlockEntity(pos);

		if (freshlySetBE instanceof LockedChestBlockEntity chestBE) {
			if (currentCustomName != null) {
				chestBE.setCustomName(currentCustomName);
			}

			chestBE.setItems(currentItems);
			chestBE.setLockCode(currentLockCode);

			if (!player.isCreative())
				itemstack.shrink(1);

			world.playSound(player, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
		} else if (freshlySetBE instanceof LockedShulkerBoxBlockEntity shulkerBE) {
			if (currentCustomName != null) {
				shulkerBE.setCustomName(currentCustomName);
			}

			shulkerBE.setItems(currentItems);
			shulkerBE.setLockCode(currentLockCode);

			if (!player.isCreative())
				itemstack.shrink(1);

			world.playSound(player, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
		}
		return InteractionResult.SUCCESS;
	}
}
