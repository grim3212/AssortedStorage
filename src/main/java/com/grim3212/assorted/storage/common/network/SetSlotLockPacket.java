package com.grim3212.assorted.storage.common.network;

import java.util.function.Supplier;

import com.grim3212.assorted.storage.common.inventory.crates.CrateContainer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public class SetSlotLockPacket {

	private final boolean locked;
	private final int slot;

	public SetSlotLockPacket(int slot, boolean locked) {
		this.slot = slot;
		this.locked = locked;
	}

	public static SetSlotLockPacket decode(FriendlyByteBuf buf) {
		return new SetSlotLockPacket(buf.readInt(), buf.readBoolean());
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(this.slot);
		buf.writeBoolean(this.locked);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
			ctx.get().enqueueWork(() -> {
				if (ctx.get().getSender().containerMenu instanceof CrateContainer crate) {
					crate.getInventory().setSlotLocked(this.slot, this.locked);
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
}