package com.grim3212.assorted.storage.common.inventory;

import com.grim3212.assorted.storage.common.block.blockentity.BaseStorageBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;

public class ShulkerItemStackStorageHandler extends StorageItemStackStorageHandler {

    public ShulkerItemStackStorageHandler(BaseStorageBlockEntity lockable, int size) {
        super(lockable, size);
    }

    public ShulkerItemStackStorageHandler(BaseStorageBlockEntity lockable, NonNullList<ItemStack> stacks) {
        super(lockable, stacks);
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            if (lockable.numPlayersUsing < 0) {
                lockable.numPlayersUsing = 0;
            }

            ++lockable.numPlayersUsing;
            lockable.getLevel().blockEvent(lockable.getBlockPos(), lockable.getBlockState().getBlock(), 1, lockable.numPlayersUsing);
            if (lockable.numPlayersUsing == 1) {
                lockable.getLevel().gameEvent(player, GameEvent.CONTAINER_OPEN, lockable.getBlockPos());
                lockable.getLevel().playSound((Player) null, lockable.getBlockPos(), SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F, lockable.getLevel().random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            --lockable.numPlayersUsing;
            lockable.getLevel().blockEvent(lockable.getBlockPos(), lockable.getBlockState().getBlock(), 1, lockable.numPlayersUsing);
            if (lockable.numPlayersUsing <= 0) {
                lockable.getLevel().gameEvent(player, GameEvent.CONTAINER_CLOSE, lockable.getBlockPos());
                lockable.getLevel().playSound((Player) null, lockable.getBlockPos(), SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F, lockable.getLevel().random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }
}
