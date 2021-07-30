package com.grim3212.assorted.storage.common.proxy;

import net.minecraft.world.entity.player.Player;

public interface IProxy {
	default void starting() {
	}

	default Player getClientPlayer() {
		return null;
	}
}
