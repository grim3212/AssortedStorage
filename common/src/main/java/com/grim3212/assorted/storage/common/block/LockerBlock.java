package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.lib.core.inventory.locking.StorageLockCode;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.common.block.blockentity.BaseStorageBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.LockerBlockEntity;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LockerBlock extends BaseStorageBlock {

    public LockerBlock(Properties properties) {
        super(properties.requiresCorrectToolForDrops().strength(3.0F, 6.0F));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LockerBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (isDualLocker(worldIn, pos) && isTopLocker(worldIn, pos))
            return super.getShape(state, worldIn, pos, context);

        return ObsidianSafeBlock.SAFE_SHAPE;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos.below()) == state)
            return super.getDestroyProgress(state, player, worldIn, pos.below());

        return super.getDestroyProgress(state, player, worldIn, pos);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);

            if (tileentity instanceof LockerBlockEntity storageBlockEntity) {

                if (storageBlockEntity.isLocked() && worldIn.getBlockState(pos.above()) != state && worldIn.getBlockState(pos.below()) != state) {
                    ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
                    CompoundTag tag = new CompoundTag();
                    new StorageLockCode(storageBlockEntity.getLockCode()).write(tag);
                    lockStack.setTag(tag);
                    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), lockStack);
                }

                StorageUtil.dropContents(worldIn, pos, storageBlockEntity.getItemStackStorageHandler());
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }

            if (state.hasBlockEntity() && !state.is(newState.getBlock())) {
                worldIn.removeBlockEntity(pos);
            }
        }
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if ((worldIn.getBlockState(pos.below()).getBlock() == this && worldIn.getBlockState(pos.below(2)).getBlock() == this) || (worldIn.getBlockState(pos.above()).getBlock() == this && worldIn.getBlockState(pos.above(2)).getBlock() == this)) {
            return;
        }

        super.setPlacedBy(worldIn, pos, state, placer, stack);

        BlockEntity tileentitytop = worldIn.getBlockEntity(pos.above());
        if (isBottomLocker(worldIn, pos) && tileentitytop != null && tileentitytop instanceof LockerBlockEntity topLocker) {
            BaseStorageBlockEntity tileentitythis = (BaseStorageBlockEntity) worldIn.getBlockEntity(pos);

            if (topLocker.isLocked()) {
                tileentitythis.setLockCode(topLocker.getLockCode());
            }
        }
    }

    public static boolean isDualLocker(BlockGetter world, BlockPos pos) {
        return isTopLocker(world, pos) || isBottomLocker(world, pos);
    }

    public static boolean isTopLocker(BlockGetter world, BlockPos pos) {
        return world.getBlockState(pos.below()) == world.getBlockState(pos);
    }

    public static boolean isBottomLocker(BlockGetter world, BlockPos pos) {
        return world.getBlockState(pos.above()) == world.getBlockState(pos);
    }

    @Override
    protected boolean removeLock(Level worldIn, BlockPos pos, Player entityplayer) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof LockerBlockEntity lockerBlockEntity) {
            if (worldIn.getBlockEntity(pos.above()) instanceof LockerBlockEntity topLocker) {

                lockerBlockEntity.setLockCode("");
                topLocker.setLockCode("");

                worldIn.playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
                return true;

            } else if (worldIn.getBlockEntity(pos.below()) instanceof LockerBlockEntity bottomLocker) {

                lockerBlockEntity.setLockCode("");
                bottomLocker.setLockCode("");

                worldIn.playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
                return true;
            }
        }

        return super.removeLock(worldIn, pos, entityplayer);
    }

    @Override
    protected boolean placeLock(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
        ItemStack itemstack = entityplayer.getItemInHand(hand);
        String code = StorageUtil.getCode(itemstack);
        if (code.isEmpty()) {
            return false;
        }

        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof LockerBlockEntity lockerBlockEntity) {
            if (worldIn.getBlockEntity(pos.above()) instanceof LockerBlockEntity topLocker) {
                if (!entityplayer.isCreative())
                    itemstack.shrink(1);
                lockerBlockEntity.setLockCode(code);
                topLocker.setLockCode(code);
                worldIn.playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
                return true;

            } else if (worldIn.getBlockEntity(pos.below()) instanceof LockerBlockEntity bottomLocker) {
                if (!entityplayer.isCreative())
                    itemstack.shrink(1);
                lockerBlockEntity.setLockCode(code);
                bottomLocker.setLockCode(code);
                worldIn.playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
                return true;
            }
        }

        return super.placeLock(worldIn, pos, entityplayer, hand);
    }
}
