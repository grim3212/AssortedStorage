package com.grim3212.assorted.storage.common.network;

import com.grim3212.assorted.storage.common.inventory.crates.CrateContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

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

    public static void handle(SetAllSlotLockPacket packet, Player player) {
        if (player.containerMenu instanceof CrateContainer crate) {
            crate.getCrateBlockEntity().getItemStackStorageHandler().setAllSlotsLocked(packet.locked);
        }
    }
}