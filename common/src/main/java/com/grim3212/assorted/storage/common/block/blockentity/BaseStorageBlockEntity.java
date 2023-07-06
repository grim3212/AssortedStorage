package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.core.inventory.INamed;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.core.inventory.impl.LockedItemStackStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.api.blockentity.IStorage;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.LockedBarrelBlock;
import com.grim3212.assorted.storage.common.inventory.StorageContainer;
import com.grim3212.assorted.storage.common.inventory.StorageItemStackStorageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public abstract class BaseStorageBlockEntity extends BlockEntity implements MenuProvider, INamed, IStorage, ILockable, IInventoryBlockEntity {

    public int numPlayersUsing;
    private int ticksSinceSync;
    protected float rotation;
    protected float prevRotation;
    private String lockCode = "";
    private Component customName;
    protected IPlatformInventoryStorageHandler platformInventoryStorageHandler;
    private LockedItemStackStorageHandler storageHandler;

    protected BaseStorageBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        this(typeIn, pos, state, 27);
    }

    protected BaseStorageBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state, int inventorySize) {
        super(typeIn, pos, state);

        this.storageHandler = new StorageItemStackStorageHandler(this, inventorySize);
    }

    @Override
    public IPlatformInventoryStorageHandler getStorageHandler() {
        if (this.platformInventoryStorageHandler == null) {
            this.platformInventoryStorageHandler = this.createStorageHandler();
        }

        return this.platformInventoryStorageHandler;
    }

    public IPlatformInventoryStorageHandler createStorageHandler() {
        return Services.INVENTORY.createStorageInventoryHandler(this.storageHandler);
    }

    public LockedItemStackStorageHandler getItemStackStorageHandler() {
        return this.storageHandler;
    }

    public void setStorageHandler(LockedItemStackStorageHandler storageHandler) {
        this.storageHandler = storageHandler;
    }

    @Override
    public boolean isLocked() {
        return this.lockCode != null && !this.lockCode.isEmpty();
    }

    @Override
    public String getLockCode() {
        return this.lockCode;
    }

    @Override
    public void setLockCode(String s) {
        if (s == null || s.isEmpty())
            this.lockCode = "";
        else
            this.lockCode = s;

        this.setChanged();
        this.modelDataUpdate();
    }

    protected void modelDataUpdate() {
        Level level = this.getLevel();
        if (level != null && level.isClientSide) {
            ClientServices.MODELS.requestModelDataRefresh(this);
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        if (this.selfInventory()) {
            if (nbt.contains("Inventory")) {
                this.storageHandler.deserializeNBT(nbt.getCompound("Inventory"));
            } else if (nbt.contains("Items")) {
                // Backwards compatible, will not be saved again like this
                this.storageHandler.deserializeNBT(nbt);
            }
        }

        if (nbt.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson(nbt.getString("CustomName"));
        }

        this.lockCode = StorageUtil.readLock(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (this.selfInventory()) {
            compound.put("Inventory", this.storageHandler.serializeNBT());
        }

        if (this.customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(this.customName));
        }

        StorageUtil.writeLock(compound, this.lockCode);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    protected boolean selfInventory() {
        return true;
    }

    protected abstract Component getDefaultName();

    protected SoundEvent openSound() {
        return SoundEvents.CHEST_OPEN;
    }

    protected SoundEvent closeSound() {
        return SoundEvents.CHEST_CLOSE;
    }

    public void tick() {
        int i = this.worldPosition.getX();
        int j = this.worldPosition.getY();
        int k = this.worldPosition.getZ();
        ++this.ticksSinceSync;
        this.numPlayersUsing = getNumberOfPlayersUsing(this.level, this, this.ticksSinceSync, i, j, k, this.numPlayersUsing);
        this.prevRotation = this.rotation;
        if (this.numPlayersUsing > 0 && this.rotation == 0.0F) {
            this.playSound(openSound());
        }

        if (this.numPlayersUsing == 0 && this.rotation > 0.0F || this.numPlayersUsing > 0 && this.rotation < 1.0F) {
            float f1 = this.rotation;
            if (this.numPlayersUsing > 0) {
                this.rotation += 0.1F;
            } else {
                this.rotation -= 0.1F;
            }

            if (this.rotation > 1.0F) {
                this.rotation = 1.0F;
            }

            if (this.rotation < 0.5F && f1 >= 0.5F) {
                this.playSound(closeSound());
            }

            if (this.rotation < 0.0F) {
                this.rotation = 0.0F;
            }
        }
    }

    @Override
    public float getRotation(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevRotation, this.rotation);
    }

    public int getNumberOfPlayersUsing(Level worldIn, BaseStorageBlockEntity lockableTileEntity, int ticksSinceSync, int x, int y, int z, int numPlayersUsing) {
        if (!worldIn.isClientSide && numPlayersUsing != 0 && (ticksSinceSync + x + y + z) % 200 == 0) {
            numPlayersUsing = getNumberOfPlayersUsing(worldIn, lockableTileEntity, x, y, z);
        }

        return numPlayersUsing;
    }

    public int getNumberOfPlayersUsing(Level world, BaseStorageBlockEntity lockableTileEntity, int x, int y, int z) {
        int i = 0;

        for (Player playerentity : world.getEntitiesOfClass(Player.class, new AABB((double) ((float) x - 5.0F), (double) ((float) y - 5.0F), (double) ((float) z - 5.0F), (double) ((float) (x + 1) + 5.0F), (double) ((float) (y + 1) + 5.0F), (double) ((float) (z + 1) + 5.0F)))) {
            if (playerentity.containerMenu instanceof StorageContainer) {
                ++i;
            }
        }

        return i;
    }

    public int getNumberOfPlayersUsing(Level world, BaseStorageBlockEntity lockableTileEntity) {
        if (lockableTileEntity != null) {
            return this.getNumberOfPlayersUsing(world, lockableTileEntity, lockableTileEntity.worldPosition.getX(), lockableTileEntity.worldPosition.getY(), lockableTileEntity.worldPosition.getZ());
        }

        return 0;
    }

    private void playSound(SoundEvent soundIn) {
        double d0 = (double) this.worldPosition.getX() + 0.5D;
        double d1 = (double) this.worldPosition.getY() + 0.5D;
        double d2 = (double) this.worldPosition.getZ() + 0.5D;

        this.level.playSound((Player) null, d0, d1, d2, soundIn, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    public void onOpenOrClose() {
        Block block = this.getBlockState().getBlock();

        if (block instanceof BaseStorageBlock) {
            this.level.blockEvent(this.worldPosition, block, 1, this.numPlayersUsing);
            this.level.updateNeighborsAt(this.worldPosition, block);
        } else if (block instanceof LockedBarrelBlock) {
            this.level.setBlock(this.getBlockPos(), getBlockState().setValue(BarrelBlock.OPEN, this.numPlayersUsing > 0), 3);
        }
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.platformInventoryStorageHandler != null) {
            this.platformInventoryStorageHandler.invalidate();
        }
    }

    @Override
    public void setCustomName(Component name) {
        this.customName = name;
    }

    @Override
    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.customName;
    }
}
