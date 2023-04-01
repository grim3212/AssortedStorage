package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.storage.common.block.LockerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class LockerItem extends BlockItem {

    public LockerItem(Block locker, Properties builder) {
        super(locker, builder);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!super.canPlace(context, state))
            return false;

        if (world.getBlockState(pos.below()).getBlock() == this.getBlock()) {
            return !LockerBlock.isTopLocker(world, pos.below());
        } else if (world.getBlockState(pos.above()).getBlock() == this.getBlock()) {
            return !LockerBlock.isBottomLocker(world, pos.above());
        }

        return true;
    }
}
