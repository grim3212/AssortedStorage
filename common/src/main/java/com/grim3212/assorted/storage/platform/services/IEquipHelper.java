package com.grim3212.assorted.storage.platform.services;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface IEquipHelper {

    boolean doesCodeMatch(@Nullable LivingEntity entity, String lockCode);
}
