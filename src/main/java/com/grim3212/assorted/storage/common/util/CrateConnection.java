package com.grim3212.assorted.storage.common.util;

import java.util.List;
import java.util.stream.IntStream;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public class CrateConnection {

	private final BlockPos pos;
	private final int depth;
	private final List<ItemStack> supportedItems;

	public CrateConnection(BlockPos pos, int depth, List<ItemStack> supportedItems) {
		this.pos = pos;
		this.depth = depth;
		this.supportedItems = supportedItems;
	}

	public BlockPos getPos() {
		return pos;
	}

	public int getDepth() {
		return depth;
	}

	public List<ItemStack> getSupportedItems() {
		return supportedItems;
	}

	public int[] slots() {
		return IntStream.range(0, this.getSupportedItems().size()).toArray();
	}

	public boolean isEmpty() {
		return !this.getSupportedItems().stream().anyMatch(e -> !e.isEmpty());
	}
}
