package com.grim3212.assorted.storage.common.events;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.events.UseBlockEvent;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.api.crates.ICrateUpgrade;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.item.PadlockItem;
import com.grim3212.assorted.storage.common.item.upgrades.LevelUpgradeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
            if (level.getBlockEntity(pos) instanceof CrateBlockEntity crate) {
                int firstEmptySlot = firstEmptyUpgradeSlot(crate);
                if (firstEmptySlot > 0) {
                    return install(level, crate, firstEmptySlot, stack);
                }
            }

            return InteractionResult.PASS;
        } else if (stack.getItem() instanceof PadlockItem) {
            if (level.getBlockEntity(pos) instanceof CrateBlockEntity crate) {
                if (!crate.isLocked() && StorageUtil.hasCode(stack)) {
                    // The 0 slot is for Padlocks only
                    return install(level, crate, 0, stack);
                }
            }

            return InteractionResult.PASS;
        } else if (stack.getItem() instanceof ICrateUpgrade) {
            if (level.getBlockEntity(pos) instanceof CrateBlockEntity crate) {
                int firstEmptySlot = firstEmptyUpgradeSlot(crate);
                if (firstEmptySlot > 0) {
                    boolean alreadyExists = crate.getItemStackStorageHandler().getEnhancements().stream().anyMatch(slotStack -> slotStack.getItem() == stack.getItem());
                    if (!alreadyExists) {
                        return install(level, crate, firstEmptySlot, stack);
                    }
                }
            }
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    /** The first free upgrade slot, or 0 - the padlock's own slot, never free to an upgrade - when there is none. */
    private static int firstEmptyUpgradeSlot(CrateBlockEntity crate) {
        NonNullList<ItemStack> enhancements = crate.getItemStackStorageHandler().getEnhancements();
        return enhancements.subList(1, enhancements.size()).indexOf(ItemStack.EMPTY) + 1;
    }

    /**
     * Only the server fits the upgrade, through the handler that sends the crate on to every client
     * that can see it. A client writing to its own copy instead is what made an upgrade show up for
     * the player who placed it and nobody else.
     */
    private static InteractionResult install(Level level, CrateBlockEntity crate, int slot, ItemStack stack) {
        if (!level.isClientSide()) {
            crate.getItemStackStorageHandler().setEnhancement(slot, stack.copyWithCount(1));
        }

        stack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}
