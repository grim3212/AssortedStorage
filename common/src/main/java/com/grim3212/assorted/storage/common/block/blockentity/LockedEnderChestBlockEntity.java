package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.core.inventory.impl.LockedItemStackStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.LockedEnderChestInventory;
import com.grim3212.assorted.storage.common.inventory.StorageContainer;
import com.grim3212.assorted.storage.common.save.EnderSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class LockedEnderChestBlockEntity extends BaseStorageBlockEntity {

    private LockedEnderChestInventory inventory;

    public LockedEnderChestBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.LOCKED_ENDER_CHEST.get(), pos, state);
    }

    @Override
    public IPlatformInventoryStorageHandler createStorageHandler() {
        if (level != null && inventory == null && isLocked()) {
            this.inventory = EnderSavedData.get(level).getInventory(this.getLockCode());
            this.inventory.addWeakListener(this);
        }
        return Services.INVENTORY.createStorageInventoryHandler(this.inventory);
    }

    @Override
    public LockedItemStackStorageHandler getItemStackStorageHandler() {
        return this.inventory;
    }

    @Override
    public void setLockCode(String s) {
        super.setLockCode(s);
        invalidateInventory();
    }

    private void invalidateInventory() {
        releasePreviousInventory();
    }

    private void releasePreviousInventory() {
        if (this.inventory != null) {
            inventory.removeWeakListener(this);
        }
        this.inventory = null;
        this.platformInventoryStorageHandler = null;
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void setRemoved() {
        this.remove = true;
        this.getStorageHandler().invalidate();
        this.invalidateInventory();
    }

    @Override
    protected boolean selfInventory() {
        return false;
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        return StorageContainer.createEnderChestContainer(windowId, playerInv, this.getStorageHandler().getItemStorageHandler());
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(Constants.MOD_ID + ".container.locked_ender_chest");
    }

    @Override
    protected SoundEvent openSound() {
        return SoundEvents.ENDER_CHEST_OPEN;
    }

    @Override
    protected SoundEvent closeSound() {
        return SoundEvents.ENDER_CHEST_CLOSE;
    }
}
