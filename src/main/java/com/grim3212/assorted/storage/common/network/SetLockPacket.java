package com.grim3212.assorted.storage.common.network;

import java.util.function.Supplier;

import com.grim3212.assorted.storage.common.inventory.LocksmithWorkbenchContainer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public class SetLockPacket {

	private final String lock;

	public SetLockPacket(String lock) {
		this.lock = lock;
	}

	public static SetLockPacket decode(FriendlyByteBuf buf) {
		return new SetLockPacket(buf.readUtf(10));
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(this.lock, 10);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
			ctx.get().enqueueWork(() -> ((LocksmithWorkbenchContainer) ctx.get().getSender().containerMenu).updateLock(this.lock));
			ctx.get().setPacketHandled(true);
		}
	}
}