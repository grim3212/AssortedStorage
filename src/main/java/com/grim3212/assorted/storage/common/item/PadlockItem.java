package com.grim3212.assorted.storage.common.item;

import java.util.Map;

import com.google.common.collect.Maps;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.BaseLockedBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.ILockeable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.registries.ForgeRegistries;

public class PadlockItem extends CombinationItem {

	private final Map<Block, Block> lockMappings;

	public PadlockItem(Properties properties) {
		super(properties);

		// Populate map so that we can properly map unlocked doors to locked doors
		this.lockMappings = Maps.newHashMap();
		lockMappings.put(Blocks.OAK_DOOR, StorageBlocks.LOCKED_OAK_DOOR.get());
		lockMappings.put(Blocks.SPRUCE_DOOR, StorageBlocks.LOCKED_SPRUCE_DOOR.get());
		lockMappings.put(Blocks.BIRCH_DOOR, StorageBlocks.LOCKED_BIRCH_DOOR.get());
		lockMappings.put(Blocks.ACACIA_DOOR, StorageBlocks.LOCKED_ACACIA_DOOR.get());
		lockMappings.put(Blocks.JUNGLE_DOOR, StorageBlocks.LOCKED_JUNGLE_DOOR.get());
		lockMappings.put(Blocks.DARK_OAK_DOOR, StorageBlocks.LOCKED_DARK_OAK_DOOR.get());
		lockMappings.put(Blocks.CRIMSON_DOOR, StorageBlocks.LOCKED_CRIMSON_DOOR.get());
		lockMappings.put(Blocks.WARPED_DOOR, StorageBlocks.LOCKED_WARPED_DOOR.get());
		lockMappings.put(Blocks.IRON_DOOR, StorageBlocks.LOCKED_IRON_DOOR.get());
		lockMappings.put(Blocks.ENDER_CHEST, StorageBlocks.LOCKED_ENDER_CHEST.get());

		Block quartzDoor = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("assorteddecor:quartz_door"));
		if (quartzDoor != Blocks.AIR) {
			lockMappings.put(quartzDoor, StorageBlocks.LOCKED_QUARTZ_DOOR.get());
		}
	}

	private Block getMatchingBlock(Block doorIn) {
		if (this.lockMappings.containsKey(doorIn)) {
			return this.lockMappings.get(doorIn);
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
			if (tryPlaceLockOnDoor(world, pos, player, hand)) {
				return InteractionResult.SUCCESS;
			}
		} else if (world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST) {
			if (tryPlaceLockOnBlock(world, pos, player, hand)) {
				return InteractionResult.SUCCESS;
			}
		}

		return super.useOn(context);
	}

	private boolean tryPlaceLockOnBlock(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
		ItemStack itemstack = entityplayer.getItemInHand(hand);

		if (itemstack.hasTag()) {
			String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
			if (!code.isEmpty()) {
				BlockState currentBlockState = worldIn.getBlockState(pos);

				Block newBlock = getMatchingBlock(currentBlockState.getBlock());
				if (newBlock == Blocks.AIR) {
					return false;
				}

				if (!entityplayer.isCreative())
					itemstack.shrink(1);

				worldIn.setBlock(pos, newBlock.defaultBlockState().setValue(EnderChestBlock.FACING, currentBlockState.getValue(EnderChestBlock.FACING)), 3);
				worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
				ILockeable currentTE = (ILockeable) worldIn.getBlockEntity(pos);
				currentTE.setLockCode(code);

				return true;
			}

		}

		return false;
	}

	private boolean tryPlaceLockOnDoor(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
		ItemStack itemstack = entityplayer.getItemInHand(hand);

		if (itemstack.hasTag()) {
			String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
			if (!code.isEmpty()) {
				BlockState currentDoor = worldIn.getBlockState(pos);

				Block newDoor = getMatchingBlock(currentDoor.getBlock());
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
