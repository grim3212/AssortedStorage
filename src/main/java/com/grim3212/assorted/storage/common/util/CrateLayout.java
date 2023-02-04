package com.grim3212.assorted.storage.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public enum CrateLayout {
	SINGLE("single", 1, new int[] { 0 }, new int[] { 32 }),
	DOUBLE("double", 2, new int[] { 0, 1 }, new int[] { 16, 16 }),
	TRIPLE("triple", 3, new int[] { 0, 1, 2 }, new int[] { 16, 8, 8 }),
	QUADRUPLE("quadruple", 4, new int[] { 0, 1, 2, 3 }, new int[] { 8, 8, 8, 8 });

	private final String name;
	private final int numStacks;
	private final int[] slots;
	private final int[] slotsBaseStacks;

	private CrateLayout(String name, int numStacks, int[] slots, int[] slotsBaseStacks) {
		this.name = name;
		this.numStacks = numStacks;
		this.slots = slots;
		this.slotsBaseStacks = slotsBaseStacks;
	}
	
	public String getName() {
		return name;
	}

	public int getNumStacks() {
		return numStacks;
	}
	
	public int[] getSlots() {
		return slots;
	}
	
	public int[] getSlotsBaseStacks() {
		return slotsBaseStacks;
	}
	
	public int getHitSlot(BlockHitResult hit) {
		Vec3 loc = hit.getLocation();
		BlockPos pos = hit.getBlockPos();
		switch (this) {
			case DOUBLE:
				if (hit.getDirection().getAxis().isHorizontal()) {
					boolean vertFlag = loc.y - (double) pos.getY() > 0.5D;
					// Top half
					if (vertFlag)
						return 0;
					// Bottom half
					else
						return 1;
				} else {
					boolean horFlag = loc.z - (double) pos.getZ() < 0.5D;

					// Top half
					if (horFlag)
						return 0;
					// Bottom half
					else
						return 1;

				}
			case TRIPLE:
				if (hit.getDirection().getAxis().isHorizontal()) {
					double compare = hit.getDirection().getAxis() == Axis.Z ? loc.x - (double) pos.getX() : loc.z - (double) pos.getZ();
					boolean vertFlag = loc.y - (double) pos.getY() > 0.5D;
					boolean flag = hit.getDirection() == Direction.EAST || hit.getDirection() == Direction.NORTH ? compare > 0.5D : compare < 0.5D;

					// Top half
					if (vertFlag) {
						return 0;
						// Bottom half
					} else {
						// Left half
						if (flag) {
							return 1;
							// Right half
						} else {
							return 2;
						}
					}
				} else {
					boolean flag = hit.getDirection() == Direction.DOWN ? loc.z - (double) pos.getZ() > 0.5D : loc.z - (double) pos.getZ() < 0.5D;
					boolean horFlag = loc.x - (double) pos.getX() < 0.5D;
					// Top half
					if (flag) {
						return 0;
						// Bottom half
					} else {
						// Left half
						if (horFlag)
							return 1;
						// Right half
						else
							return 2;

					}
				}
			case QUADRUPLE:
				if (hit.getDirection().getAxis().isHorizontal()) {
					double compare = hit.getDirection().getAxis() == Axis.Z ? loc.x - (double) pos.getX() : loc.z - (double) pos.getZ();
					boolean vertFlag = loc.y - (double) pos.getY() > 0.5D;
					boolean flag = hit.getDirection() == Direction.EAST || hit.getDirection() == Direction.NORTH ? compare > 0.5D : compare < 0.5D;

					// Top half
					if (vertFlag) {
						// Left half
						if (flag)
							return 0;
						// Right half
						else
							return 1;
						// Bottom half
					} else {
						// Left half
						if (flag)
							return 2;
						// Right half
						else
							return 3;
					}
				} else {
					boolean flag = hit.getDirection() == Direction.DOWN ? loc.z - (double) pos.getZ() > 0.5D : loc.z - (double) pos.getZ() < 0.5D;
					boolean horFlag = loc.x - (double) pos.getX() < 0.5D;
					// Top half
					if (flag) {
						// Left half
						if (horFlag)
							return 0;
						// Right half
						else
							return 1;
						// Bottom half
					} else {
						// Left half
						if (horFlag)
							return 2;
						// Right half
						else
							return 3;
					}
				}
			case SINGLE:
			default:
				return 0;
		}
	}
}
