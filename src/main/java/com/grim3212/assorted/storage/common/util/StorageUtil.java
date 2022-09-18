package com.grim3212.assorted.storage.common.util;

import com.grim3212.assorted.storage.common.block.blockentity.ILockable;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingItemHandler;
import com.grim3212.assorted.storage.common.item.StorageItems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public class StorageUtil {

	public static ItemStack setCodeOnStack(String code, ItemStack stack) {
		return setCodeOnStack(new StorageLockCode(code), stack);
	}

	public static ItemStack setCodeOnStack(StorageLockCode code, ItemStack stack) {
		ItemStack output = stack.copy();

		if (output.hasTag()) {
			code.write(output.getTag());
		} else {
			CompoundTag tag = new CompoundTag();
			code.write(tag);
			output.setTag(tag);
		}

		return output;
	}

	public static void writeCodeToStack(String code, ItemStack stack) {
		writeCodeToStack(new StorageLockCode(code), stack);
	}

	public static void writeCodeToStack(StorageLockCode code, ItemStack stack) {
		if (stack.hasTag()) {
			code.write(stack.getTag());
		} else {
			CompoundTag tag = new CompoundTag();
			code.write(tag);
			stack.setTag(tag);
		}
	}

	public static String getCode(BlockEntity te) {
		if (te instanceof ILockable) {
			return ((ILockable) te).getLockCode();
		}
		return "";
	}

	public static String getCode(ItemStack stack) {
		if (stack.hasTag()) {
			String code = stack.getTag().contains("Storage_Lock", 8) ? stack.getTag().getString("Storage_Lock") : "";
			return code;
		}
		return "";
	}

	public static boolean hasCodeWithMatch(ItemStack stack, String testCode) {
		String code = getCode(stack);
		return !code.isEmpty() && code.equals(testCode);
	}

	public static boolean hasCode(ItemStack stack) {
		String code = getCode(stack);
		return !code.isEmpty();
	}

	public static boolean canAccess(BlockGetter worldIn, BlockPos pos, Player entityplayer) {
		ILockable lockeable = (ILockable) worldIn.getBlockEntity(pos);

		if (lockeable.isLocked()) {
			for (int slot = 0; slot < entityplayer.getInventory().getContainerSize(); slot++) {
				ItemStack itemstack = entityplayer.getInventory().getItem(slot);

				if (!itemstack.isEmpty()) {
					if (itemstack.getItem() == StorageItems.LOCKSMITH_KEY.get()) {
						if (hasCodeWithMatch(itemstack, lockeable.getLockCode())) {
							return true;
						}
					} else if (itemstack.getItem() == StorageItems.KEY_RING.get()) {
						IItemHandler itemHandler = itemstack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);

						if (itemHandler instanceof KeyRingItemHandler) {
							KeyRingItemHandler handler = (KeyRingItemHandler) itemHandler;
							handler.load();

							for (int keyRingSlot = 0; keyRingSlot < handler.getSlots(); keyRingSlot++) {
								ItemStack keyRingStack = handler.getStackInSlot(keyRingSlot);

								if (!keyRingStack.isEmpty()) {
									if (keyRingStack.getItem() == StorageItems.LOCKSMITH_KEY.get()) {
										if (hasCodeWithMatch(keyRingStack, lockeable.getLockCode())) {
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
						if (hasCodeWithMatch(itemstack, stack.getTag().getString("Storage_Lock"))) {
							return true;
						}
					} else if (itemstack.getItem() == StorageItems.KEY_RING.get()) {
						IItemHandler itemHandler = itemstack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);

						if (itemHandler instanceof KeyRingItemHandler) {
							KeyRingItemHandler handler = (KeyRingItemHandler) itemHandler;
							handler.load();

							for (int keyRingSlot = 0; keyRingSlot < handler.getSlots(); keyRingSlot++) {
								ItemStack keyRingStack = handler.getStackInSlot(keyRingSlot);

								if (!keyRingStack.isEmpty()) {
									if (keyRingStack.getItem() == StorageItems.LOCKSMITH_KEY.get()) {
										if (hasCodeWithMatch(keyRingStack, stack.getTag().getString("Storage_Lock"))) {
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
