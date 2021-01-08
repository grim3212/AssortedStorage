package com.grim3212.assorted.storage.common.item;

import java.util.Map;

import com.google.common.collect.Maps;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.tileentity.BaseLockedTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
		doorMapping.put(StorageBlocks.QUARTZ_DOOR.get(), StorageBlocks.LOCKED_QUARTZ_DOOR.get());
	}

	private Block getMatchingDoor(Block doorIn) {
		if (this.doorMapping.containsKey(doorIn)) {
			return this.doorMapping.get(doorIn);
		}
		return Blocks.AIR;
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();

		PlayerEntity player = context.getPlayer();
		Hand hand = context.getHand();

		if (world.getBlockState(pos).getBlock() instanceof DoorBlock) {
			if (tryPlaceLock(world, pos, player, hand)) {
				return ActionResultType.SUCCESS;
			}
		}

		return super.onItemUse(context);
	}

	private boolean tryPlaceLock(World worldIn, BlockPos pos, PlayerEntity entityplayer, Hand hand) {
		ItemStack itemstack = entityplayer.getHeldItem(hand);

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

				worldIn.setBlockState(pos, newDoor.getDefaultState().with(DoorBlock.FACING, currentDoor.get(DoorBlock.FACING)).with(DoorBlock.OPEN, currentDoor.get(DoorBlock.OPEN)).with(DoorBlock.HINGE, currentDoor.get(DoorBlock.HINGE)).with(DoorBlock.HALF, currentDoor.get(DoorBlock.HALF)), 3);
				worldIn.playSound(entityplayer, pos, SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
				BaseLockedTileEntity currentTE = (BaseLockedTileEntity) worldIn.getTileEntity(pos);
				currentTE.setLockCode(code);

				if (currentDoor.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
					BlockState downState = worldIn.getBlockState(pos.down());
					worldIn.setBlockState(pos.down(), newDoor.getDefaultState().with(DoorBlock.FACING, downState.get(DoorBlock.FACING)).with(DoorBlock.OPEN, downState.get(DoorBlock.OPEN)).with(DoorBlock.HINGE, downState.get(DoorBlock.HINGE)).with(DoorBlock.HALF, downState.get(DoorBlock.HALF)), 3);
					BaseLockedTileEntity downTE = (BaseLockedTileEntity) worldIn.getTileEntity(pos.down());
					downTE.setLockCode(code);
				} else {
					BlockState upState = worldIn.getBlockState(pos.up());
					worldIn.setBlockState(pos.up(), newDoor.getDefaultState().with(DoorBlock.FACING, upState.get(DoorBlock.FACING)).with(DoorBlock.OPEN, upState.get(DoorBlock.OPEN)).with(DoorBlock.HINGE, upState.get(DoorBlock.HINGE)).with(DoorBlock.HALF, upState.get(DoorBlock.HALF)), 3);
					BaseLockedTileEntity upTE = (BaseLockedTileEntity) worldIn.getTileEntity(pos.up());
					upTE.setLockCode(code);
				}

				return true;
			}

		}

		return false;
	}
}
