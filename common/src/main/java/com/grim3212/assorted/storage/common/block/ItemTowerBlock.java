package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.blockentity.ItemTowerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ItemTowerBlock extends BaseStorageBlock {

    public ItemTowerBlock(Properties properties) {
        super(properties.requiresCorrectToolForDrops().strength(3.0F, 6.0F));
    }

    @Override
    protected boolean isDoorBlocked(LevelAccessor world, BlockPos pos) {
        return false;
    }

    @Override
    protected boolean canBeLocked(Level worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ItemTowerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }
}
