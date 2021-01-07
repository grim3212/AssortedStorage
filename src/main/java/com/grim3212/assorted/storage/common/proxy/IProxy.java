package com.grim3212.assorted.storage.common.proxy;

import net.minecraft.entity.player.PlayerEntity;

public interface IProxy {
	default void starting() {
	}

	default PlayerEntity getClientPlayer() {
		return null;
	}
}
