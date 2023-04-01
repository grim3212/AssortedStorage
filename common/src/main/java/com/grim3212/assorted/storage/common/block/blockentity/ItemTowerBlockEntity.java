package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.lib.core.inventory.IInventoryStorageHandler;
import com.grim3212.assorted.lib.core.inventory.impl.SidedStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.client.model.ItemTowerModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.common.inventory.ItemTowerContainer;
import com.grim3212.assorted.storage.common.inventory.ItemTowerInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

import java.util.stream.IntStream;

public class ItemTowerBlockEntity extends BaseStorageBlockEntity {


    public ItemTowerModel model;

    public ItemTowerBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.ITEM_TOWER.get(), pos, state, 18);
    }

    @Override
    public IInventoryStorageHandler createStorageHandler() {
        return Services.INVENTORY.createStorageInventoryHandler(new SidedStorageHandler(new ItemTowerInventory(getItemTowers(), this.worldPosition), null));
    }

    public void animate(int animId) {
        if (model == null) {
            this.model = new ItemTowerModel(Minecraft.getInstance().getEntityModels().bakeLayer(StorageModelLayers.ITEM_TOWER));
        }

        this.model.setAnimation(animId);
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
        return ItemTowerContainer.createItemTowerContainer(windowId, player, new ItemTowerInventory(getItemTowers(), this.worldPosition));
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(Constants.MOD_ID + ".container.item_tower");
    }

    protected static final int[] ITEM_TOWER_SLOTS = IntStream.range(0, 18).toArray();

    @Override
    public int[] getSlotsForFace(Direction side) {
        return ITEM_TOWER_SLOTS;
    }

    public NonNullList<ItemTowerBlockEntity> getItemTowers() {
        if (this.level == null) {
            return NonNullList.create();
        }

        BlockState state = level.getBlockState(worldPosition);
        NonNullList<ItemTowerBlockEntity> itemTowers = NonNullList.create();

        int downBlocks = 1;
        while (level.getBlockState(worldPosition.below(downBlocks)) == state) {
            downBlocks++;
        }

        int upBlocks = 1;
        BlockPos bottomPos = worldPosition.below(downBlocks);
        while (level.getBlockState(bottomPos.above(upBlocks)) == state) {
            itemTowers.add((ItemTowerBlockEntity) level.getBlockEntity(bottomPos.above(upBlocks)));
            upBlocks++;
        }

        return itemTowers;
    }
}
