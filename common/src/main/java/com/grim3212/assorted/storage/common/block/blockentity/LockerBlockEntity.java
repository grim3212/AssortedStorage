package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.LockerHalf;
import com.grim3212.assorted.storage.common.block.LockerBlock;
import com.grim3212.assorted.storage.common.inventory.DualLockerInventory;
import com.grim3212.assorted.storage.common.inventory.LockerContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class LockerBlockEntity extends BaseStorageBlockEntity {

    public LockerBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.LOCKER.get(), pos, state, 45);
    }

    @Override
    public IPlatformInventoryStorageHandler getStorageHandler() {
        if (this.platformInventoryStorageHandler == null) {
            this.platformInventoryStorageHandler = this.createStorageHandler();
        }

        return this.platformInventoryStorageHandler;
    }

    @Override
    public IPlatformInventoryStorageHandler createStorageHandler() {
        if (this.getBlockState().getValue(LockerBlock.HALF) == LockerHalf.BOTTOM && level.getBlockEntity(worldPosition.above()) instanceof LockerBlockEntity lockerUp) {
            return Services.INVENTORY.createStorageInventoryHandler(new DualLockerInventory(this, this.getItemStackStorageHandler(), lockerUp.getItemStackStorageHandler()));
        }

        if (this.getBlockState().getValue(LockerBlock.HALF) == LockerHalf.TOP && level.getBlockEntity(worldPosition.below()) instanceof LockerBlockEntity lockerDown) {
            return Services.INVENTORY.createStorageInventoryHandler(new DualLockerInventory(this, lockerDown.getItemStackStorageHandler(), this.getItemStackStorageHandler()));
        }

        return super.createStorageHandler();
    }

    public void refreshStorageHandler() {
        this.platformInventoryStorageHandler = this.createStorageHandler();
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
        if (this.getBlockState().getValue(LockerBlock.HALF) == LockerHalf.BOTTOM && this.getLevel().getBlockEntity(this.getBlockPos().above()) instanceof LockerBlockEntity lockerUp) {
            return LockerContainer.createDualLockerContainer(windowId, player, new DualLockerInventory(this, this.getItemStackStorageHandler(), lockerUp.getItemStackStorageHandler()));
        }

        if (this.getBlockState().getValue(LockerBlock.HALF) == LockerHalf.TOP && this.getLevel().getBlockEntity(this.getBlockPos().below()) instanceof LockerBlockEntity lockerBelow) {
            return LockerContainer.createDualLockerContainer(windowId, player, new DualLockerInventory(this, lockerBelow.getItemStackStorageHandler(), this.getItemStackStorageHandler()));
        }

        return LockerContainer.createLockerContainer(windowId, player, this.getItemStackStorageHandler());
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(Constants.MOD_ID + ".container.locker");
    }
}
