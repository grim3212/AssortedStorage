package com.grim3212.assorted.storage.common.network;

import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    // FriendlyByteBuf lost readItem/writeItem; an ItemStack goes over the wire through its own
    // StreamCodec, which needs the registry access a RegistryFriendlyByteBuf carries. Every payload
    // LibPayload builds is encoded against one, so the cast always holds.
    public static SyncCrate decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int slot = buf.readInt();
        ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf);
        return new SyncCrate(pos, slot, new LargeItemStack(stack, buf.readInt(), buf.readInt(), buf.readBoolean()));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.slot);
        ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, this.stack.getStack());
        buf.writeInt(this.stack.getAmount());
        buf.writeInt(this.stack.getRotation());
        buf.writeBoolean(this.stack.isLocked());
    }

    public static void handle(SyncCrate packet, Player player) {
        BlockEntity blockEntity = player.level().getBlockEntity(packet.pos);
        if (blockEntity instanceof CrateBlockEntity crate) {
            crate.getItemStackStorageHandler().applySyncedItem(packet.slot, packet.stack);
        }
    }
}