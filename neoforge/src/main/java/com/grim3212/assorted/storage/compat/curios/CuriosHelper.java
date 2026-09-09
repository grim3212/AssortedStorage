package com.grim3212.assorted.storage.compat.curios;

import com.grim3212.assorted.storage.api.StorageAccessUtil;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

/**
 * Curios 16.0.0+26.2 dropped {@code LazyOptional} along with NeoForge: {@code getCuriosInventory}
 * hands back a plain {@link Optional} now. The walk over {@code getEquippedCurios()} is gone too -
 * that returns an {@code IItemHandlerModifiable}, which is {@code @Deprecated(forRemoval)} in 26.2 -
 * so the search goes through {@code findCurios(Predicate)}, which is the API's own way of asking the
 * same question and returns the equipped stacks directly.
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
