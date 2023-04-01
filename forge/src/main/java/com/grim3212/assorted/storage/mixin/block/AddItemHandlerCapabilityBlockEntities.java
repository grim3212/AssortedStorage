package com.grim3212.assorted.storage.mixin.block;

import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.inventory.ForgeInventoryStorageHandler;
import com.grim3212.assorted.storage.common.block.blockentity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({BaseStorageBlockEntity.class, CrateBlockEntity.class, LockedShulkerBoxBlockEntity.class, LockedEnderChestBlockEntity.class, CrateControllerBlockEntity.class})
public class AddItemHandlerCapabilityBlockEntities extends BlockEntity {
    public AddItemHandlerCapabilityBlockEntities(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (!this.isRemoved() && cap == ForgeCapabilities.ITEM_HANDLER) {
            if (this instanceof IInventoryBlockEntity inv) {
                return ((ForgeInventoryStorageHandler) inv.getStorageHandler()).getCapability(side).cast();
            }
        }

        return super.getCapability(cap, side);
    }
}
