package com.grim3212.assorted.storage.api.crates;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ICrateSystem {

	default boolean hasItems() {
		return false;
	}

	default List<ItemStack> getItems(Level level, BlockPos pos) {
		return new ArrayList<>();
	}
}
