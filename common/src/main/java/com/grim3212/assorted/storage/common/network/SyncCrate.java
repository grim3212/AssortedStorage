package com.grim3212.assorted.storage.common.network;

import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SyncCrate {

    private final BlockPos pos;
    private final int slot;
    private final LargeItemStack stack;

    public SyncCrate(BlockPos pos, int slot, LargeItemStack stack) {
        this.pos = pos;
        this.slot = slot;
        this.stack = stack;
    }

    public static SyncCrate decode(FriendlyByteBuf buf) {
        return new SyncCrate(buf.readBlockPos(), buf.readInt(), new LargeItemStack(buf.readItem(), buf.readInt(), buf.readInt(), buf.readBoolean()));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.slot);
        buf.writeItem(this.stack.getStack());
        buf.writeInt(this.stack.getAmount());
        buf.writeInt(this.stack.getRotation());
        buf.writeBoolean(this.stack.isLocked());
    }

    public static void handle(SyncCrate packet, Player player) {
        BlockEntity blockEntity = Minecraft.getInstance().player.getCommandSenderWorld().getBlockEntity(packet.pos);
        if (blockEntity instanceof CrateBlockEntity crate) {
            crate.getItemStackStorageHandler().setItem(packet.slot, packet.stack);
        }
    }
}