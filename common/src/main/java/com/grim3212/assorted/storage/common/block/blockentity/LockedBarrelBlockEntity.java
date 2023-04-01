package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.data.IModelDataBuilder;
import com.grim3212.assorted.lib.core.block.IBlockEntityWithModelData;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.block.LockedBarrelBlock;
import com.grim3212.assorted.storage.common.inventory.LockedMaterialContainer;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.properties.StorageModelProperties;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

public class LockedBarrelBlockEntity extends BaseStorageBlockEntity implements IBlockEntityWithModelData {

    private final StorageMaterial storageMaterial;
    protected final int[] slots;

    public LockedBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.LOCKED_BARREL.get(), pos, state);

        if (state.getBlock() instanceof LockedBarrelBlock lockedBarrel) {
            this.storageMaterial = lockedBarrel.getStorageMaterial();
            setStartingContents(this.storageMaterial != null ? this.storageMaterial.totalItems() : 27);
        } else {
            // Default to regular chest
            this.storageMaterial = null;
            this.setStartingContents(27);
        }

        this.slots = IntStream.range(0, this.getContainerSize()).toArray();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return this.slots;
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
        return new LockedMaterialContainer(StorageContainerTypes.BARRELS.get(storageMaterial).get(), windowId, player, this, storageMaterial, false);
    }

    @Override
    protected Component getDefaultName() {
        if (this.storageMaterial == null) {
            return Component.translatable(Constants.MOD_ID + ".container.locked_barrel");
        }

        return Component.translatable(Constants.MOD_ID + ".container.barrel_" + this.storageMaterial.toString());
    }

    @Override
    public void setLockCode(String s) {
        super.setLockCode(s);
        if (level instanceof ClientLevel) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }

    @Override
    protected SoundEvent openSound() {
        return SoundEvents.BARREL_OPEN;
    }

    @Override
    protected SoundEvent closeSound() {
        return SoundEvents.BARREL_CLOSE;
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

    @Override
    public @NotNull IBlockModelData getBlockModelData() {
        return IModelDataBuilder.create().withInitial(StorageModelProperties.IS_LOCKED, this.isLocked()).build();
    }
}
