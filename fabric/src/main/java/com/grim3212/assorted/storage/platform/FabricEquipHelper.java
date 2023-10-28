package com.grim3212.assorted.storage.platform;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.compat.trinkets.TrinketHelper;
import com.grim3212.assorted.storage.platform.services.IEquipHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class FabricEquipHelper implements IEquipHelper {
    @Override
    public boolean doesCodeMatch(@Nullable LivingEntity entity, String lockCode) {
        return Services.PLATFORM.isModLoaded("trinkets") ? TrinketHelper.hasCodeMatch(entity, lockCode) : lockCode == null || lockCode.isEmpty();
    }
}
