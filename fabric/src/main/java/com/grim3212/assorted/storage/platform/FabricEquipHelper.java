package com.grim3212.assorted.storage.platform;

import com.grim3212.assorted.storage.platform.services.IEquipHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Fabric has no accessory slot integration, so a worn key never opens a lock here; NeoForge checks
 * Curios slots.
 */
public class FabricEquipHelper implements IEquipHelper {
    @Override
    public boolean doesCodeMatch(@Nullable LivingEntity entity, String lockCode) {
        return lockCode == null || lockCode.isEmpty();
    }
}
