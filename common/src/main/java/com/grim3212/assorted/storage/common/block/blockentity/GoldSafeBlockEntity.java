package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.block.GoldSafeBlock;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.inventory.StorageContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class GoldSafeBlockEntity extends BaseStorageBlockEntity {

    public GoldSafeBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.GOLD_SAFE.get(), pos, state, 36);
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
        return StorageContainer.createGoldSafeContainer(windowId, player, this);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(Constants.MOD_ID + ".container.gold_safe");
    }

    protected static final int[] GOLD_SAFE_SLOTS = IntStream.range(0, 36).toArray();

    @Override
    public int[] getSlotsForFace(Direction side) {
        return GOLD_SAFE_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return !(Block.byItem(itemStackIn.getItem()) instanceof LockedShulkerBoxBlock || Block.byItem(itemStackIn.getItem()) instanceof ShulkerBoxBlock || Block.byItem(itemStackIn.getItem()) instanceof GoldSafeBlock) && super.canPlaceItemThroughFace(index, itemStackIn, direction);
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction, String lockCode, boolean force) {
        return !(Block.byItem(itemStackIn.getItem()) instanceof LockedShulkerBoxBlock || Block.byItem(itemStackIn.getItem()) instanceof ShulkerBoxBlock || Block.byItem(itemStackIn.getItem()) instanceof GoldSafeBlock) && itemStackIn.getItem().canFitInsideContainerItems() && super.canPlaceItemThroughFace(index, itemStackIn, direction, lockCode, force);
    }
}
