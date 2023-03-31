package com.grim3212.assorted.storage.api.crates;

import net.minecraft.core.BlockPos;

public class CrateConnection {

	private final BlockPos pos;
	private final int depth;
	private final int numSlots;

	public CrateConnection(BlockPos pos, int depth, int numSlots) {
		this.pos = pos;
		this.depth = depth;
		this.numSlots = numSlots;
	}

	public BlockPos getPos() {
		return pos;
	}

	public int getDepth() {
		return depth;
	}

	public int getNumSlots() {
		return numSlots;
	}
}
