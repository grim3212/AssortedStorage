package com.grim3212.assorted.storage.common.item;

import java.util.Map;

import com.google.common.collect.Maps;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.BaseLockedBlockEntity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public class PadlockItem extends CombinationItem {

	private final Map<Block, Block> doorMapping;

	public PadlockItem(Properties properties) {
		super(properties);

		// Populate map so that we can properly map unlocked doors to locked doors
		this.doorMapping = Maps.newHashMap();
		doorMapping.put(Blocks.OAK_DOOR, StorageBlocks.LOCKED_OAK_DOOR.get());
		doorMapping.put(Blocks.SPRUCE_DOOR, StorageBlocks.LOCKED_SPRUCE_DOOR.get());
		doorMapping.put(Blocks.BIRCH_DOOR, StorageBlocks.LOCKED_BIRCH_DOOR.get());
		doorMapping.put(Blocks.ACACIA_DOOR, StorageBlocks.LOCKED_ACACIA_DOOR.get());
		doorMapping.put(Blocks.JUNGLE_DOOR, StorageBlocks.LOCKED_JUNGLE_DOOR.get());
		doorMapping.put(Blocks.DARK_OAK_DOOR, StorageBlocks.LOCKED_DARK_OAK_DOOR.get());
		doorMapping.put(Blocks.CRIMSON_DOOR, StorageBlocks.LOCKED_CRIMSON_DOOR.get());
		doorMapping.put(Blocks.WARPED_DOOR, StorageBlocks.LOCKED_WARPED_DOOR.get());
		doorMapping.put(Blocks.IRON_DOOR, StorageBlocks.LOCKED_IRON_DOOR.get());

		Block quartzDoor = Registry.BLOCK.get(new ResourceLocation("assorteddecor:quartz_door"));
		if (quartzDoor != Blocks.AIR) {
			doorMapping.put(quartzDoor, StorageBlocks.LOCKED_QUARTZ_DOOR.get());
		}
	}

	private Block getMatchingDoor(Block doorIn) {
		if (this.doorMapping.containsKey(doorIn)) {
			return this.doorMapping.get(doorIn);
		}
		return Blocks.AIR;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();

		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();

		if (world.getBlockState(pos).getBlock() instanceof DoorBlock) {
			if (tryPlaceLock(world, pos, player, hand)) {
				return InteractionResult.SUCCESS;
			}
		}

		return super.useOn(context);
	}

	private boolean tryPlaceLock(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
		ItemStack itemstack = entityplayer.getItemInHand(hand);

		if (itemstack.hasTag()) {
			String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
			if (!code.isEmpty()) {
				BlockState currentDoor = worldIn.getBlockState(pos);

				Block newDoor = getMatchingDoor(currentDoor.getBlock());
				if (newDoor == Blocks.AIR) {
					return false;
				}

				if (!entityplayer.isCreative())
					itemstack.shrink(1);

				worldIn.setBlock(pos, newDoor.defaultBlockState().setValue(DoorBlock.FACING, currentDoor.getValue(DoorBlock.FACING)).setValue(DoorBlock.OPEN, currentDoor.getValue(DoorBlock.OPEN)).setValue(DoorBlock.HINGE, currentDoor.getValue(DoorBlock.HINGE)).setValue(DoorBlock.HALF, currentDoor.getValue(DoorBlock.HALF)), 3);
				worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
				BaseLockedBlockEntity currentTE = (BaseLockedBlockEntity) worldIn.getBlockEntity(pos);
				currentTE.setLockCode(code);

				if (currentDoor.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
					BlockState downState = worldIn.getBlockState(pos.below());
					worldIn.setBlock(pos.below(), newDoor.defaultBlockState().setValue(DoorBlock.FACING, downState.getValue(DoorBlock.FACING)).setValue(DoorBlock.OPEN, downState.getValue(DoorBlock.OPEN)).setValue(DoorBlock.HINGE, downState.getValue(DoorBlock.HINGE)).setValue(DoorBlock.HALF, downState.getValue(DoorBlock.HALF)), 3);
					BaseLockedBlockEntity downTE = (BaseLockedBlockEntity) worldIn.getBlockEntity(pos.below());
					downTE.setLockCode(code);
				} else {
					BlockState upState = worldIn.getBlockState(pos.above());
					worldIn.setBlock(pos.above(), newDoor.defaultBlockState().setValue(DoorBlock.FACING, upState.getValue(DoorBlock.FACING)).setValue(DoorBlock.OPEN, upState.getValue(DoorBlock.OPEN)).setValue(DoorBlock.HINGE, upState.getValue(DoorBlock.HINGE)).setValue(DoorBlock.HALF, upState.getValue(DoorBlock.HALF)), 3);
					BaseLockedBlockEntity upTE = (BaseLockedBlockEntity) worldIn.getBlockEntity(pos.above());
					upTE.setLockCode(code);
				}

				return true;
			}

		}

		return false;
	}
}
