package com.grim3212.assorted.storage.platform;

import com.grim3212.assorted.storage.platform.services.IEquipHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * The Trinkets integration is gone. Trinkets' last release is 3.10.0 for 1.21.1 (July 2024) - there
 * is no 26.2 build and no artifact to resolve - so {@code TrinketHelper}, the {@code data/trinkets}
 * slot definitions and the dependency were removed rather than left as code that cannot compile.
 * <p>
 * <b>Behaviour change:</b> a key worn in a Trinkets belt slot no longer opens a locked container on
 * Fabric. Only the NeoForge half still has an equipment integration (Curios). Restoring this needs
 * Trinkets, or an equivalent accessory API, to ship for 26.2.
 */
public class FabricEquipHelper implements IEquipHelper {
    @Override
    public boolean doesCodeMatch(@Nullable LivingEntity entity, String lockCode) {
        return lockCode == null || lockCode.isEmpty();
    }
}
