package com.grim3212.assorted.storage.common.block.blockentity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IStorage {
	float getRotation(float partialTicks);
}
