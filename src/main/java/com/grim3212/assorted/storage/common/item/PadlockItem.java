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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
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
	public ActionResultType useOn(ItemUseContext context) {
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();

		PlayerEntity player = context.getPlayer();
		Hand hand = context.getHand();

		if (world.getBlockState(pos).getBlock() instanceof DoorBlock) {
			if (tryPlaceLock(world, pos, player, hand)) {
				return ActionResultType.SUCCESS;
			}
		}

		return super.useOn(context);
	}

	private boolean tryPlaceLock(World worldIn, BlockPos pos, PlayerEntity entityplayer, Hand hand) {
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
				worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundCategory.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
				BaseLockedTileEntity currentTE = (BaseLockedTileEntity) worldIn.getBlockEntity(pos);
				currentTE.setLockCode(code);

				if (currentDoor.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
					BlockState downState = worldIn.getBlockState(pos.below());
					worldIn.setBlock(pos.below(), newDoor.defaultBlockState().setValue(DoorBlock.FACING, downState.getValue(DoorBlock.FACING)).setValue(DoorBlock.OPEN, downState.getValue(DoorBlock.OPEN)).setValue(DoorBlock.HINGE, downState.getValue(DoorBlock.HINGE)).setValue(DoorBlock.HALF, downState.getValue(DoorBlock.HALF)), 3);
					BaseLockedTileEntity downTE = (BaseLockedTileEntity) worldIn.getBlockEntity(pos.below());
					downTE.setLockCode(code);
				} else {
					BlockState upState = worldIn.getBlockState(pos.above());
					worldIn.setBlock(pos.above(), newDoor.defaultBlockState().setValue(DoorBlock.FACING, upState.getValue(DoorBlock.FACING)).setValue(DoorBlock.OPEN, upState.getValue(DoorBlock.OPEN)).setValue(DoorBlock.HINGE, upState.getValue(DoorBlock.HINGE)).setValue(DoorBlock.HALF, upState.getValue(DoorBlock.HALF)), 3);
					BaseLockedTileEntity upTE = (BaseLockedTileEntity) worldIn.getBlockEntity(pos.above());
					upTE.setLockCode(code);
				}

				return true;
			}

		}

		return false;
	}
}
