package com.grim3212.assorted.storage.api;


import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingItemHandler;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;

public class StorageAccessUtil {

    public static boolean canAccess(BlockGetter worldIn, BlockPos pos, Player entityplayer) {
        ILockable lockeable = (ILockable) worldIn.getBlockEntity(pos);

        if (lockeable.isLocked()) {
            for (int slot = 0; slot < entityplayer.getInventory().getContainerSize(); slot++) {
                ItemStack itemstack = entityplayer.getInventory().getItem(slot);

                if (!itemstack.isEmpty()) {
                    if (itemstack.getItem() == StorageItems.LOCKSMITH_KEY.get()) {
                        if (StorageUtil.hasCodeWithMatch(itemstack, lockeable.getLockCode())) {
                            return true;
                        }
                    } else if (itemstack.getItem() == StorageItems.KEY_RING.get()) {
                        IItemStorageHandler itemStorageHandler = Services.INVENTORY.getItemStorageHandler(itemstack).orElse(null);

                        if (itemStorageHandler instanceof KeyRingItemHandler) {
                            KeyRingItemHandler handler = (KeyRingItemHandler) itemStorageHandler;
                            handler.load();

                            for (int keyRingSlot = 0; keyRingSlot < itemStorageHandler.getSlots(); keyRingSlot++) {
                                ItemStack keyRingStack = itemStorageHandler.getStackInSlot(keyRingSlot);

                                if (!keyRingStack.isEmpty()) {
                                    if (keyRingStack.getItem() == StorageItems.LOCKSMITH_KEY.get()) {
                                        if (StorageUtil.hasCodeWithMatch(keyRingStack, lockeable.getLockCode())) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return false;
        }

        return true;
    }

    public static boolean canAccess(ItemStack stack, Player entityplayer) {
        if (stack.hasTag() && stack.getTag().contains("Storage_Lock")) {
            for (int slot = 0; slot < entityplayer.getInventory().getContainerSize(); slot++) {
                ItemStack itemstack = entityplayer.getInventory().getItem(slot);

                if (!itemstack.isEmpty()) {
                    if (itemstack.getItem() == StorageItems.LOCKSMITH_KEY.get()) {
                        if (StorageUtil.hasCodeWithMatch(itemstack, stack.getTag().getString("Storage_Lock"))) {
                            return true;
                        }
                    } else if (itemstack.getItem() == StorageItems.KEY_RING.get()) {
                        IItemStorageHandler itemStorageHandler = Services.INVENTORY.getItemStorageHandler(itemstack).orElse(null);

                        if (itemStorageHandler instanceof KeyRingItemHandler) {
                            KeyRingItemHandler handler = (KeyRingItemHandler) itemStorageHandler;
                            handler.load();

                            for (int keyRingSlot = 0; keyRingSlot < itemStorageHandler.getSlots(); keyRingSlot++) {
                                ItemStack keyRingStack = itemStorageHandler.getStackInSlot(keyRingSlot);

                                if (!keyRingStack.isEmpty()) {
                                    if (keyRingStack.getItem() == StorageItems.LOCKSMITH_KEY.get()) {
                                        if (StorageUtil.hasCodeWithMatch(keyRingStack, stack.getTag().getString("Storage_Lock"))) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return false;
        }

        return true;
    }
}
