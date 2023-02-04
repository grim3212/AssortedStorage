package com.grim3212.assorted.storage.common.network;

import java.util.function.Supplier;

import com.grim3212.assorted.storage.common.block.blockentity.StorageCrateBlockEntity;
import com.grim3212.assorted.storage.common.util.LargeItemStack;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public class SyncStorageCrate {

	private final BlockPos pos;
	private final int slot;
	private final LargeItemStack stack;

	public SyncStorageCrate(BlockPos pos, int slot, LargeItemStack stack) {
		this.pos = pos;
		this.slot = slot;
		this.stack = stack;
	}

	public static SyncStorageCrate decode(FriendlyByteBuf buf) {
		return new SyncStorageCrate(buf.readBlockPos(), buf.readInt(), new LargeItemStack(buf.readItem(), buf.readInt(), buf.readInt(), buf.readBoolean()));
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeInt(this.slot);
		buf.writeItem(this.stack.getStack());
		buf.writeInt(this.stack.getAmount());
		buf.writeInt(this.stack.getRotation());
		buf.writeBoolean(this.stack.isLocked());
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
			ctx.get().enqueueWork(() -> {
				BlockEntity blockEntity = Minecraft.getInstance().player.getCommandSenderWorld().getBlockEntity(this.pos);
				if (blockEntity instanceof StorageCrateBlockEntity crate) {
					crate.setItem(this.slot, this.stack);
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
}