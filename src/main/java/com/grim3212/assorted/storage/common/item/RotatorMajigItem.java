package com.grim3212.assorted.storage.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class RotatorMajigItem extends Item {

	public RotatorMajigItem() {
		super(new Item.Properties().stacksTo(1));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		InteractionHand hand = context.getHand();

		if (hand == InteractionHand.OFF_HAND) {
			return InteractionResult.PASS;
		}

		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();

		if (!player.mayUseItemAt(pos, context.getClickedFace(), player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}

		Level level = context.getLevel();
		BlockState state = level.getBlockState(pos);

		level.setBlock(pos, state.rotate(level, pos, player.isShiftKeyDown() ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90), Block.UPDATE_ALL);
		player.swing(hand);
		return InteractionResult.SUCCESS;
	}
}
