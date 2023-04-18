package com.grim3212.assorted.storage.mixin.block;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DoorBlock.class)
public interface DoorBlockAccessor {
    @Accessor
    BlockSetType getType();
}
