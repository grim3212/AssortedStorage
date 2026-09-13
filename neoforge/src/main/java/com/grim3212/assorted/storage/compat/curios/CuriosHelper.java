package com.grim3212.assorted.storage.compat.curios;

import com.grim3212.assorted.storage.api.StorageAccessUtil;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

/**
 * The search goes through the API's own {@code findCurios(Predicate)} rather than a walk over
 * {@code getEquippedCurios()}, which hands back an {@code IItemHandlerModifiable} - deprecated for
 * removal in 26.2.
 */
public class CuriosHelper {

    public static boolean hasCodeMatch(@Nullable LivingEntity entity, String lockCode) {
        if (entity != null) {
            Optional<ICuriosItemHandler> handler = CuriosApi.getCuriosInventory(entity);
            if (handler.isPresent() && !handler.get().findCurios(stack -> StorageAccessUtil.canStackAccess(stack, lockCode)).isEmpty()) {
                return true;
            }
        }

        return lockCode == null || lockCode.isEmpty();
    }
}
