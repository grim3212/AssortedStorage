package com.grim3212.assorted.storage.common.item.upgrades;

import com.grim3212.assorted.storage.api.crates.ICrateUpgrade;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BasicCrateUpgradeItem extends Item implements ICrateUpgrade {

	public BasicCrateUpgradeItem(Properties props) {
		super(props);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();

		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof CrateBlockEntity crate) {
			int firstEmptySlot = (crate.getEnhancements().subList(1, crate.getEnhancements().size()).indexOf(ItemStack.EMPTY) + 1);
			// The 0 slot is for Padlocks only
			if (firstEmptySlot > 0) {
				boolean alreadyExists = crate.getEnhancements().stream().anyMatch(slotStack -> slotStack.getItem() == stack.getItem());
				if (!alreadyExists) {
					crate.getEnhancements().set(firstEmptySlot, stack.copyWithCount(1));
					stack.shrink(1);
					return InteractionResult.SUCCESS;
				}
			}
		}

		return super.onItemUseFirst(stack, context);
	}
}
