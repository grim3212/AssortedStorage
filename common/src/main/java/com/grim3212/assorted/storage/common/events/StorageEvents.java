package com.grim3212.assorted.storage.common.events;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.events.UseBlockEvent;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.api.crates.ICrateUpgrade;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.item.PadlockItem;
import com.grim3212.assorted.storage.common.item.upgrades.LevelUpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public class StorageEvents {

    public static void init() {
        Services.EVENTS.registerEvent(UseBlockEvent.class, (final UseBlockEvent event) -> {
            InteractionResult result = useOnBlock(event.getPlayer(), event.getLevel(), event.getHand(), event.getHitResult());
            event.setResult(result);
        });
    }

    private static InteractionResult useOnBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);
        BlockPos pos = hitResult.getBlockPos();

        if (stack.getItem() instanceof LevelUpgradeItem) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof CrateBlockEntity crate) {
                int firstEmptySlot = (crate.getItemStackStorageHandler().getEnhancements().subList(1, crate.getItemStackStorageHandler().getEnhancements().size()).indexOf(ItemStack.EMPTY) + 1);
                // The 0 slot is for Padlocks only
                if (firstEmptySlot > 0) {
                    crate.getItemStackStorageHandler().getEnhancements().set(firstEmptySlot, stack.copyWithCount(1));
                    stack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }

            return InteractionResult.PASS;
        } else if (stack.getItem() instanceof PadlockItem) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof CrateBlockEntity crate) {
                if (!crate.isLocked() && StorageUtil.hasCode(stack)) {
                    crate.getItemStackStorageHandler().getEnhancements().set(0, stack.copyWithCount(1));
                    stack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }

            return InteractionResult.PASS;
        } else if (stack.getItem() instanceof ICrateUpgrade) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof CrateBlockEntity crate) {
                int firstEmptySlot = (crate.getItemStackStorageHandler().getEnhancements().subList(1, crate.getItemStackStorageHandler().getEnhancements().size()).indexOf(ItemStack.EMPTY) + 1);
                // The 0 slot is for Padlocks only
                if (firstEmptySlot > 0) {
                    boolean alreadyExists = crate.getItemStackStorageHandler().getEnhancements().stream().anyMatch(slotStack -> slotStack.getItem() == stack.getItem());
                    if (!alreadyExists) {
                        crate.getItemStackStorageHandler().getEnhancements().set(firstEmptySlot, stack.copyWithCount(1));
                        stack.shrink(1);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }
}
