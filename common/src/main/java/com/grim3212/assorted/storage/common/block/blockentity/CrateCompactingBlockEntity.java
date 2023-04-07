package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.inventory.crates.CompactingCrateInventory;
import com.grim3212.assorted.storage.common.inventory.crates.CrateCompactingContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class CrateCompactingBlockEntity extends CrateBlockEntity {


    public CrateCompactingBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.CRATE_COMPACTING.get(), pos, state);

        this.storageHandler = new CompactingCrateInventory(this);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(Constants.MOD_ID + ".container.compacting_storage_crate");
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
        return new CrateCompactingContainer(StorageContainerTypes.CRATE_COMPACTING.get(), windowId, player, this);
    }

}
