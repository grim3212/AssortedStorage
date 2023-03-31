package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.lib.core.inventory.INamed;
import com.grim3212.assorted.lib.core.inventory.LockedWorldlyContainer;
import com.grim3212.assorted.lib.core.inventory.impl.LockedSidedInvWrapper;
import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.lib.core.inventory.locking.StorageLockCode;
import com.grim3212.assorted.storage.api.blockentity.IStorage;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.LockedBarrelBlock;
import com.grim3212.assorted.storage.common.inventory.StorageContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public abstract class BaseStorageBlockEntity extends BlockEntity implements LockedWorldlyContainer, MenuProvider, INamed, IStorage, ILockable {

    private NonNullList<ItemStack> chestContents;
    protected int numPlayersUsing;
    private int ticksSinceSync;
    protected float rotation;
    protected float prevRotation;
    private StorageLockCode lockCode = StorageLockCode.EMPTY_CODE;
    private Component customName;

    protected BaseStorageBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        this(typeIn, pos, state, 27);
    }

    protected BaseStorageBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state, int inventorySize) {
        super(typeIn, pos, state);

        if (this.selfInventory())
            setStartingContents(inventorySize);
    }

    public void setStartingContents(int inventorySize) {
        this.chestContents = NonNullList.<ItemStack>withSize(inventorySize, ItemStack.EMPTY);
    }

    public void setItems(NonNullList<ItemStack> itemsIn) {
        if (itemsIn.size() == this.chestContents.size()) {
            this.chestContents = itemsIn;
        }

        this.chestContents = NonNullList.<ItemStack>withSize(this.chestContents.size(), ItemStack.EMPTY);

        for (int i = 0; i < itemsIn.size(); i++) {
            this.chestContents.set(i, itemsIn.get(i));
        }

        this.setChanged();
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

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (this.selfInventory()) {
            this.chestContents = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

            ContainerHelper.loadAllItems(nbt, this.chestContents);
        }

        if (nbt.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson(nbt.getString("CustomName"));
        }

        this.lockCode = StorageLockCode.read(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (this.selfInventory()) {
            ContainerHelper.saveAllItems(compound, this.chestContents);
        }

        if (this.customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(this.customName));
        }

        this.lockCode.write(compound);
    }

    public CompoundTag saveToNbt(CompoundTag compound) {
        ContainerHelper.saveAllItems(compound, this.chestContents, false);
        return compound;
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
    public int getContainerSize() {
        return this.getItems().size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.chestContents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
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

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.onOpenOrClose();
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
            this.onOpenOrClose();
        }
    }

    protected void onOpenOrClose() {
        Block block = this.getBlockState().getBlock();

        if (block instanceof BaseStorageBlock) {
            this.level.blockEvent(this.worldPosition, block, 1, this.numPlayersUsing);
            this.level.updateNeighborsAt(this.worldPosition, block);
        } else if (block instanceof LockedBarrelBlock) {
            this.level.setBlock(this.getBlockPos(), getBlockState().setValue(BarrelBlock.OPEN, this.numPlayersUsing > 0), 3);
        }
    }

    public NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
            return storageItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private LazyOptional<?> storageItemHandler = LazyOptional.of(() -> createSidedHandler());

    protected IItemHandler createSidedHandler() {
        return new LockedSidedInvWrapper(this, null);
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void setRemoved() {
        super.setRemoved();
        this.storageItemHandler.invalidate();
    }

    @Override
    public void clearContent() {
        this.getItems().clear();
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

    @Override
    public ItemStack getItem(int index) {
        return this.getItems().get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = ContainerHelper.removeItem(this.getItems(), index, count);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.getItems(), index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.getItems().set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    protected static final int[] DEFAULT_SLOTS = IntStream.range(0, 27).toArray();

    @Override
    public int[] getSlotsForFace(Direction side) {
        return DEFAULT_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
        return !this.isLocked();
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return !this.isLocked();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction, String code, boolean force) {
        return force || (this.isLocked() ? this.lockCode.getLockCode().equals(code) : true);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction, String code, boolean force) {
        return force || (this.isLocked() ? this.lockCode.getLockCode().equals(code) : true);
    }
}
