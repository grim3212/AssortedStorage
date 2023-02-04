package com.grim3212.assorted.storage.common.network;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class PacketHandler {
	private static final String PROTOCOL = "7";
	public static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(AssortedStorage.MODID, "channel"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

	public static void init() {
		int id = 0;
		HANDLER.registerMessage(id++, SetLockPacket.class, SetLockPacket::encode, SetLockPacket::decode, SetLockPacket::handle);
		HANDLER.registerMessage(id++, SyncStorageCrate.class, SyncStorageCrate::encode, SyncStorageCrate::decode, SyncStorageCrate::handle);
		HANDLER.registerMessage(id++, SetSlotLockPacket.class, SetSlotLockPacket::encode, SetSlotLockPacket::decode, SetSlotLockPacket::handle);
		HANDLER.registerMessage(id++, SetAllSlotLockPacket.class, SetAllSlotLockPacket::encode, SetAllSlotLockPacket::decode, SetAllSlotLockPacket::handle);
	}

	/**
	 * Send message to all within 64 blocks that have this chunk loaded
	 */
	public static void sendToNearby(Level world, BlockPos pos, Object toSend) {
		world.getEntitiesOfClass(ServerPlayer.class, new AABB(pos).inflate(64)).forEach(playerEntity -> {
			HANDLER.sendTo(toSend, playerEntity.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		});
	}

	public static void sendToNearby(Level world, Entity e, Object toSend) {
		sendToNearby(world, e.blockPosition(), toSend);
	}

	public static void sendTo(ServerPlayer playerMP, Object toSend) {
		HANDLER.sendTo(toSend, playerMP.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
	}

	public static void sendNonLocal(ServerPlayer playerMP, Object toSend) {
		if (playerMP.server.isDedicatedServer() || !playerMP.getGameProfile().getId().equals(playerMP.server.getSingleplayerProfile().getId())) {
			sendTo(playerMP, toSend);
		}
	}

	public static void sendToServer(Object msg) {
		HANDLER.sendToServer(msg);
	}

	private PacketHandler() {
	}

}
