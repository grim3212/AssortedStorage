package com.grim3212.assorted.storage.platform;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.compat.curios.CuriosHelper;
import com.grim3212.assorted.storage.platform.services.IEquipHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class ForgeEquipHelper implements IEquipHelper {
    @Override
    public boolean doesCodeMatch(@Nullable LivingEntity entity, String lockCode) {
        return Services.PLATFORM.isModLoaded("curios") ? CuriosHelper.hasCodeMatch(entity, lockCode) : lockCode == null || lockCode.isEmpty();
    }

}
