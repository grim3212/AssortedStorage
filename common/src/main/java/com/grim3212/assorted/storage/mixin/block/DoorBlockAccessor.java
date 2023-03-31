package com.grim3212.assorted.storage.mixin.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.DoorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DoorBlock.class)
public interface DoorBlockAccessor {
    @Accessor
    SoundEvent getCloseSound();

    @Accessor
    SoundEvent getOpenSound();
}
