package com.grim3212.assorted.storage.common.network;

import java.util.function.Supplier;

import com.grim3212.assorted.storage.common.inventory.crates.CrateContainer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public class SetAllSlotLockPacket {

	private final boolean locked;

	public SetAllSlotLockPacket(boolean locked) {
		this.locked = locked;
	}

	public static SetAllSlotLockPacket decode(FriendlyByteBuf buf) {
		return new SetAllSlotLockPacket(buf.readBoolean());
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(this.locked);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
			ctx.get().enqueueWork(() -> {
				if (ctx.get().getSender().containerMenu instanceof CrateContainer crate) {
					crate.getInventory().setAllSlotsLocked(this.locked);
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
}