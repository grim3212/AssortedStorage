package com.grim3212.assorted.storage.compat.trinkets;

import com.grim3212.assorted.storage.api.StorageAccessUtil;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrinketHelper {

    public static boolean hasCodeMatch(@Nullable LivingEntity entity, String lockCode) {
        return TrinketsApi.getTrinketComponent(entity)
                .map(TrinketComponent::getAllEquipped)
                .orElse(List.of())
                .stream()
                .map(Tuple::getB)
                .anyMatch(stack -> StorageAccessUtil.canStackAccess(stack, lockCode));
    }
}
