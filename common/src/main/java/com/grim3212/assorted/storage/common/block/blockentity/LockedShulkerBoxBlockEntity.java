package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.lib.core.inventory.locking.StorageLockCode;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.block.IStorageMaterial;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.inventory.LockedMaterialContainer;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.inventory.StorageItemStackStorageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity.AnimationStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LockedShulkerBoxBlockEntity extends BaseStorageBlockEntity {

    private final StorageMaterial storageMaterial;
    private AnimationStatus animationStatus = AnimationStatus.CLOSED;
    private float progress;
    private float progressOld;
    private DyeColor color = null;
    private StorageLockCode lockCode = StorageLockCode.EMPTY_CODE;

    public LockedShulkerBoxBlockEntity(StorageMaterial storageMaterial, BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.LOCKED_SHULKER_BOX.get(), pos, state, storageMaterial != null ? storageMaterial.totalItems() : 27);
        this.storageMaterial = storageMaterial;
    }

    public LockedShulkerBoxBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.LOCKED_SHULKER_BOX.get(), pos, state);
        if (state.getBlock() instanceof IStorageMaterial storageMaterial) {
            this.storageMaterial = storageMaterial.getStorageMaterial();
        } else {
            this.storageMaterial = null;
        }
        this.setStorageHandler(new StorageItemStackStorageHandler(this, storageMaterial != null ? storageMaterial.totalItems() : 27));
    }

    public int colorToSave() {
        return this.color == null ? -1 : this.color.getId();
    }

    public DyeColor colorFromCompound(CompoundTag tag) {
        if (!tag.contains("Color"))
            return null;

        int color = tag.getInt("Color");
        return color == -1 ? null : DyeColor.byId(color);
    }

    public DyeColor getColor() {
        return color;
    }

    public void setColor(DyeColor color) {
        this.color = color;

        this.setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LockedShulkerBoxBlockEntity shulkerBE) {
        shulkerBE.updateAnimation(level, pos, state);
    }

    private void updateAnimation(Level level, BlockPos pos, BlockState state) {
        this.progressOld = this.progress;
        switch (this.animationStatus) {
            case CLOSED:
                this.progress = 0.0F;
                break;
            case OPENING:
                this.progress += 0.1F;
                if (this.progress >= 1.0F) {
                    this.animationStatus = AnimationStatus.OPENED;
                    this.progress = 1.0F;
                    doNeighborUpdates(level, pos, state);
                }

                this.moveCollidedEntities(level, pos, state);
                break;
            case CLOSING:
                this.progress -= 0.1F;
                if (this.progress <= 0.0F) {
                    this.animationStatus = AnimationStatus.CLOSED;
                    this.progress = 0.0F;
                    doNeighborUpdates(level, pos, state);
                }
                break;
            case OPENED:
                this.progress = 1.0F;
        }

    }

    public AnimationStatus getAnimationStatus() {
        return this.animationStatus;
    }

    private void moveCollidedEntities(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof LockedShulkerBoxBlock) {
            Direction direction = state.getValue(LockedShulkerBoxBlock.FACING);
            AABB aabb = Shulker.getProgressDeltaAabb(direction, this.progressOld, this.progress).move(pos);
            List<Entity> list = level.getEntities((Entity) null, aabb);
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); ++i) {
                    Entity entity = list.get(i);
                    if (entity.getPistonPushReaction() != PushReaction.IGNORE) {
                        entity.move(MoverType.SHULKER_BOX, new Vec3((aabb.getXsize() + 0.01D) * (double) direction.getStepX(), (aabb.getYsize() + 0.01D) * (double) direction.getStepY(), (aabb.getZsize() + 0.01D) * (double) direction.getStepZ()));
                    }
                }

            }
        }
    }

    @Override
    public boolean triggerEvent(int p_59678_, int p_59679_) {
        if (p_59678_ == 1) {
            this.numPlayersUsing = p_59679_;
            if (p_59679_ == 0) {
                this.animationStatus = AnimationStatus.CLOSING;
                doNeighborUpdates(this.getLevel(), this.worldPosition, this.getBlockState());
            }

            if (p_59679_ == 1) {
                this.animationStatus = AnimationStatus.OPENING;
                doNeighborUpdates(this.getLevel(), this.worldPosition, this.getBlockState());
            }

            return true;
        } else {
            return super.triggerEvent(p_59678_, p_59679_);
        }
    }

    private static void doNeighborUpdates(Level level, BlockPos pos, BlockState state) {
        state.updateNeighbourShapes(level, pos, 3);
    }

    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.numPlayersUsing);
            if (this.numPlayersUsing == 1) {
                this.level.gameEvent(player, GameEvent.CONTAINER_OPEN, this.worldPosition);
                this.level.playSound((Player) null, this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.numPlayersUsing);
            if (this.numPlayersUsing <= 0) {
                this.level.gameEvent(player, GameEvent.CONTAINER_CLOSE, this.worldPosition);
                this.level.playSound((Player) null, this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    @Override
    protected Component getDefaultName() {
        if (this.storageMaterial == null) {
            return Component.translatable(Constants.MOD_ID + ".container.locked_shulker_box");
        }

        return Component.translatable(Constants.MOD_ID + ".container.shulker_" + this.storageMaterial.toString());
    }

    public AABB getBoundingBox(BlockState state) {
        return Shulker.getProgressAabb(state.getValue(LockedShulkerBoxBlock.FACING), 0.5F * this.getProgress(1.0F));
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.color = colorFromCompound(compound);
        this.lockCode = StorageLockCode.read(compound);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("Color", this.colorToSave());
    }

    public float getProgress(float p_59658_) {
        return Mth.lerp(p_59658_, this.progressOld, this.progress);
    }

    public boolean isClosed() {
        return this.animationStatus == AnimationStatus.CLOSED;
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
        return new LockedMaterialContainer(StorageContainerTypes.SHULKERS.get(storageMaterial).get(), windowId, player, this.getItemStackStorageHandler(), storageMaterial, true);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public boolean isLocked() {
        return this.lockCode != null && this.lockCode != StorageLockCode.EMPTY_CODE;
    }

    @Override
    public StorageLockCode getStorageLockCode() {
        return this.lockCode;
    }

    @Override
    public String getLockCode() {
        return this.lockCode.getLockCode();
    }

    @Override
    public void setLockCode(String s) {
        if (s == null || s.isEmpty())
            this.lockCode = StorageLockCode.EMPTY_CODE;
        else
            this.lockCode = new StorageLockCode(s);

        this.setChanged();
    }

}
