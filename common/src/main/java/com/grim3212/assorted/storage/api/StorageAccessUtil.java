package com.grim3212.assorted.storage.api;


import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingItemHandler;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.platform.StorageServices;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;

public class StorageAccessUtil {

    public static boolean canAccess(BlockGetter worldIn, BlockPos pos, Player entityplayer) {
        ILockable lockeable = (ILockable) worldIn.getBlockEntity(pos);

        if (lockeable.isLocked()) {
            // Check the players slots first
            for (int slot = 0; slot < entityplayer.getInventory().getContainerSize(); slot++) {
                ItemStack itemstack = entityplayer.getInventory().getItem(slot);

                if (canStackAccess(itemstack, lockeable.getLockCode())) {
                    return true;
                }
            }

            // Check if the items might be equipped
            return StorageServices.EQUIP.doesCodeMatch(entityplayer, lockeable.getLockCode());
        }

        return true;
    }

    public static boolean canAccess(ItemStack stack, Player entityplayer) {
        if (stack.hasTag() && stack.getTag().contains("Storage_Lock")) {
            String currentLock = stack.getTag().getString("Storage_Lock");
            if (currentLock == null || currentLock.isEmpty()) {
                return true;
            }

            for (int slot = 0; slot < entityplayer.getInventory().getContainerSize(); slot++) {
                ItemStack itemstack = entityplayer.getInventory().getItem(slot);

                if (canStackAccess(itemstack, currentLock)) {
                    return true;
                }
            }

            // Check if the items might be equipped
            return StorageServices.EQUIP.doesCodeMatch(entityplayer, currentLock);
        }

        return true;
    }

    public static boolean canStackAccess(ItemStack stack, String lockCode) {
        if (lockCode == null || lockCode.isEmpty()) {
            return true;
        }

        if (stack.isEmpty()) {
            return false;
        }

        if (stack.getItem() == StorageItems.LOCKSMITH_KEY.get()) {
            if (StorageUtil.hasCodeWithMatch(stack, lockCode)) {
                return true;
            }
        }

        if (stack.getItem() == StorageItems.KEY_RING.get()) {
            IItemStorageHandler itemStorageHandler = Services.INVENTORY.getItemStorageHandler(stack).orElse(null);

            if (itemStorageHandler instanceof KeyRingItemHandler keyRingItemHandler) {
                keyRingItemHandler.load();

                for (int keyRingSlot = 0; keyRingSlot < itemStorageHandler.getSlots(); keyRingSlot++) {
                    ItemStack keyRingStack = itemStorageHandler.getStackInSlot(keyRingSlot);

                    if (!keyRingStack.isEmpty()) {
                        if (keyRingStack.getItem() == StorageItems.LOCKSMITH_KEY.get()) {
                            if (StorageUtil.hasCodeWithMatch(keyRingStack, lockCode)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}
