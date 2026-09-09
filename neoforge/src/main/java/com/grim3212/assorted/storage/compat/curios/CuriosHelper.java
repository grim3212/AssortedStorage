package com.grim3212.assorted.storage.compat.curios;

import com.grim3212.assorted.storage.api.StorageAccessUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class CuriosHelper {

    public static boolean hasCodeMatch(@Nullable LivingEntity entity, String lockCode) {
        LazyOptional<ICuriosItemHandler> lazyHandler = CuriosApi.getCuriosInventory(entity);
        if (lazyHandler.isPresent() && lazyHandler.resolve().isPresent()) {
            ICuriosItemHandler handler = lazyHandler.resolve().get();

            IItemHandlerModifiable curios = handler.getEquippedCurios();

            for (int slot = 0; slot < curios.getSlots(); slot++) {
                ItemStack stack = curios.getStackInSlot(slot);
                if (StorageAccessUtil.canStackAccess(stack, lockCode)) {
                    return true;
                }
            }
        }

        return lockCode == null || lockCode.isEmpty();
    }
}
