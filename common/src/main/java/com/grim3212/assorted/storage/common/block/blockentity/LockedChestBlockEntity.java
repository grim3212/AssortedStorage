package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.block.LockedChestBlock;
import com.grim3212.assorted.storage.common.inventory.LockedMaterialContainer;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.inventory.StorageItemStackStorageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class LockedChestBlockEntity extends BaseStorageBlockEntity {

    private final StorageMaterial storageMaterial;

    public LockedChestBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.LOCKED_CHEST.get(), pos, state);

        if (state.getBlock() instanceof LockedChestBlock lockedChest) {
            this.storageMaterial = lockedChest.getStorageMaterial();
        } else {
            // Default to regular chest
            this.storageMaterial = null;
        }

        this.setStorageHandler(new StorageItemStackStorageHandler(this, storageMaterial != null ? storageMaterial.totalItems() : 27));
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
        return new LockedMaterialContainer(StorageContainerTypes.CHESTS.get(storageMaterial).get(), windowId, player, this.getItemStackStorageHandler(), storageMaterial, false);
    }

    @Override
    protected Component getDefaultName() {
        if (this.storageMaterial == null) {
            return Component.translatable(Constants.MOD_ID + ".container.locked_chest");
        }

        return Component.translatable(Constants.MOD_ID + ".container.chest_" + this.storageMaterial.toString());
    }

    @Override
    public int getNumberOfPlayersUsing(Level world, BaseStorageBlockEntity lockableTileEntity, int x, int y, int z) {
        int i = 0;

        for (Player playerentity : world.getEntitiesOfClass(Player.class, new AABB((double) ((float) x - 5.0F), (double) ((float) y - 5.0F), (double) ((float) z - 5.0F), (double) ((float) (x + 1) + 5.0F), (double) ((float) (y + 1) + 5.0F), (double) ((float) (z + 1) + 5.0F)))) {
            if (playerentity.containerMenu instanceof LockedMaterialContainer) {
                ++i;
            }
        }

        return i;
    }
}
