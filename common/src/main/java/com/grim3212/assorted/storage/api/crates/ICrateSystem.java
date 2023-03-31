package com.grim3212.assorted.storage.api.crates;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ICrateSystem {

	default int numSlots(Level level, BlockPos pos) {
		return 0;
	}
}
