package com.grim3212.assorted.storage.common.network;

import com.grim3212.assorted.storage.common.inventory.crates.CrateContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

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

    public static void handle(SetSlotLockPacket packet, Player player) {
        if (player.containerMenu instanceof CrateContainer crate) {
            crate.getCrateBlockEntity().getItemStackStorageHandler().setSlotLocked(packet.slot, packet.locked);
        }
    }
}