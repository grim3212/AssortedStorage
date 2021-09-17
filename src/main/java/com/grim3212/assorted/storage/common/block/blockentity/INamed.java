package com.grim3212.assorted.storage.common.block.blockentity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;

public interface INamed extends Nameable {

	public void setCustomName(Component name);
}
