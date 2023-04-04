package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.DualLockerInventory;
import com.grim3212.assorted.storage.common.inventory.LockerContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class LockerBlockEntity extends BaseStorageBlockEntity {

    enum LastCachedInventory {
        TOP,
        BOTTOM,
        SELF
    }

    private LastCachedInventory lastCachedInventory = null;

    public LockerBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.LOCKER.get(), pos, state, 45);
    }

    @Override
    public IPlatformInventoryStorageHandler getStorageHandler() {
        if (this.platformInventoryStorageHandler == null || this.lastCachedInventory == null) {
            this.platformInventoryStorageHandler = this.createStorageHandler();
        } else {
            this.checkCache();
        }


        return this.platformInventoryStorageHandler;
    }

    private void checkCache() {
        //TODO: Look into something like how double chests work so we don't need this AFAIK
        if (level.getBlockEntity(worldPosition.above()) instanceof LockerBlockEntity topLocker) {
            if (this.lastCachedInventory == LastCachedInventory.TOP) {
                // We are up to date
                return;
            }
            this.lastCachedInventory = LastCachedInventory.TOP;
            this.platformInventoryStorageHandler = Services.INVENTORY.createStorageInventoryHandler(new DualLockerInventory(this.getItemStackStorageHandler(), topLocker.getItemStackStorageHandler()));
            return;
        }

        if (level.getBlockEntity(worldPosition.below()) instanceof LockerBlockEntity bottomLocker) {
            if (this.lastCachedInventory == LastCachedInventory.BOTTOM) {
                // We are up to date
                return;
            }
            this.lastCachedInventory = LastCachedInventory.BOTTOM;
            this.platformInventoryStorageHandler = Services.INVENTORY.createStorageInventoryHandler(new DualLockerInventory(bottomLocker.getItemStackStorageHandler(), this.getItemStackStorageHandler()));
            return;
        }

        if (this.lastCachedInventory == LastCachedInventory.SELF) {
            // We are up to date
            return;
        }
        this.lastCachedInventory = LastCachedInventory.SELF;
        this.platformInventoryStorageHandler = super.createStorageHandler();
    }

    @Override
    public IPlatformInventoryStorageHandler createStorageHandler() {
        if (level.getBlockEntity(worldPosition.above()) instanceof LockerBlockEntity lockerUp) {
            this.lastCachedInventory = LastCachedInventory.TOP;
            return Services.INVENTORY.createStorageInventoryHandler(new DualLockerInventory(this.getItemStackStorageHandler(), lockerUp.getItemStackStorageHandler()));
        }

        if (level.getBlockEntity(worldPosition.below()) instanceof LockerBlockEntity lockerDown) {
            this.lastCachedInventory = LastCachedInventory.BOTTOM;
            return Services.INVENTORY.createStorageInventoryHandler(new DualLockerInventory(lockerDown.getItemStackStorageHandler(), this.getItemStackStorageHandler()));
        }

        this.lastCachedInventory = LastCachedInventory.SELF;
        return super.createStorageHandler();
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
        if (level.getBlockEntity(worldPosition.above()) instanceof LockerBlockEntity lockerUp) {
            return LockerContainer.createDualLockerContainer(windowId, player, new DualLockerInventory(this.getItemStackStorageHandler(), lockerUp.getItemStackStorageHandler()));
        }

        return LockerContainer.createLockerContainer(windowId, player, this.getItemStackStorageHandler());
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(Constants.MOD_ID + ".container.locker");
    }

    public boolean isUpperLocker() {
        if (this.level == null)
            return false;
        return this.level.getBlockState(worldPosition.below()) == this.level.getBlockState(worldPosition);
    }

    public boolean hasUpperLocker() {
        if (this.level == null)
            return false;
        return this.level.getBlockState(this.worldPosition.above()) == this.level.getBlockState(worldPosition);
    }

    public LockerBlockEntity getUpperLocker() {
        if (this.level == null || !hasUpperLocker())
            return null;
        return (LockerBlockEntity) this.level.getBlockEntity(worldPosition.above());
    }

    public boolean isBottomLocker() {
        if (this.level == null)
            return false;
        return this.level.getBlockState(worldPosition.above()) == this.level.getBlockState(worldPosition);
    }

    public boolean hasBottomLocker() {
        if (this.level == null)
            return false;
        return this.level.getBlockState(this.worldPosition.below()) == this.level.getBlockState(worldPosition);
    }

    public LockerBlockEntity getBottomLocker() {
        if (this.level == null || !hasUpperLocker())
            return null;
        return (LockerBlockEntity) this.level.getBlockEntity(worldPosition.below());
    }
}
