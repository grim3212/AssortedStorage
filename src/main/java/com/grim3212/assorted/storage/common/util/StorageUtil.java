package com.grim3212.assorted.storage.common.util;

import com.grim3212.assorted.storage.common.block.tileentity.ILockeable;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingItemHandler;
import com.grim3212.assorted.storage.common.item.StorageItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.items.CapabilityItemHandler;
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
			CompoundNBT tag = new CompoundNBT();
			code.write(tag);
			output.setTag(tag);
		}

		return output;
	}

	public static String getCode(TileEntity te) {
		if (te instanceof ILockeable) {
			return ((ILockeable) te).getLockCode();
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

	public static boolean canAccess(IBlockReader worldIn, BlockPos pos, PlayerEntity entityplayer) {
		ILockeable lockeable = (ILockeable) worldIn.getBlockEntity(pos);

		if (lockeable.isLocked()) {
			for (int slot = 0; slot < entityplayer.inventory.getContainerSize(); slot++) {
				ItemStack itemstack = entityplayer.inventory.getItem(slot);

				if (!itemstack.isEmpty()) {
					if (itemstack.getItem() == StorageItems.LOCKSMITH_KEY.get()) {
						if (hasCodeWithMatch(itemstack, lockeable.getLockCode())) {
							return true;
						}
					} else if (itemstack.getItem() == StorageItems.KEY_RING.get()) {
						IItemHandler itemHandler = itemstack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);

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

}
