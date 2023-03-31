package com.grim3212.assorted.storage.common.network;


import com.grim3212.assorted.storage.common.inventory.LocksmithWorkbenchContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

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

    public static void handle(SetLockPacket packet, Player player) {
        ((LocksmithWorkbenchContainer) player.containerMenu).updateLock(packet.lock);
    }
}