package com.grim3212.assorted.storage.common.block.tileentity;

import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.inventory.StorageContainer;
import com.grim3212.assorted.storage.common.util.StorageLockCode;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

@OnlyIn(value = Dist.CLIENT, _interface = IStorage.class)
public abstract class BaseStorageTileEntity extends TileEntity implements ISidedInventory, INamedContainerProvider, INamed, IStorage, ITickableTileEntity, ILockeable {

	private NonNullList<ItemStack> chestContents;
	protected int numPlayersUsing;
	private int ticksSinceSync;
	protected float rotation;
	protected float prevRotation;
	private StorageLockCode lockCode = StorageLockCode.EMPTY_CODE;
	private ITextComponent customName;

	protected BaseStorageTileEntity(TileEntityType<?> typeIn) {
		this(typeIn, 27);
	}

	protected BaseStorageTileEntity(TileEntityType<?> typeIn, int inventorySize) {
		super(typeIn);

		this.chestContents = NonNullList.<ItemStack>withSize(inventorySize, ItemStack.EMPTY);
	}

	@Override
	public boolean isLocked() {
		return this.lockCode != null && this.lockCode != StorageLockCode.EMPTY_CODE;
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

	protected abstract ITextComponent getDefaultName();

	@Override
	public void tick() {
		int i = this.worldPosition.getX();
		int j = this.worldPosition.getY();
		int k = this.worldPosition.getZ();
		++this.ticksSinceSync;
		this.numPlayersUsing = getNumberOfPlayersUsing(this.level, this, this.ticksSinceSync, i, j, k, this.numPlayersUsing);
		this.prevRotation = this.rotation;
		if (this.numPlayersUsing > 0 && this.rotation == 0.0F) {
			this.playSound(SoundEvents.CHEST_OPEN);
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
				this.playSound(SoundEvents.CHEST_CLOSE);
			}

			if (this.rotation < 0.0F) {
				this.rotation = 0.0F;
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getRotation(float partialTicks) {
		return MathHelper.lerp(partialTicks, this.prevRotation, this.rotation);
	}

	public static int getNumberOfPlayersUsing(World worldIn, BaseStorageTileEntity lockableTileEntity, int ticksSinceSync, int x, int y, int z, int numPlayersUsing) {
		if (!worldIn.isClientSide && numPlayersUsing != 0 && (ticksSinceSync + x + y + z) % 200 == 0) {
			numPlayersUsing = getNumberOfPlayersUsing(worldIn, lockableTileEntity, x, y, z);
		}

		return numPlayersUsing;
	}

	public static int getNumberOfPlayersUsing(World world, BaseStorageTileEntity lockableTileEntity, int x, int y, int z) {
		int i = 0;

		for (PlayerEntity playerentity : world.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB((double) ((float) x - 5.0F), (double) ((float) y - 5.0F), (double) ((float) z - 5.0F), (double) ((float) (x + 1) + 5.0F), (double) ((float) (y + 1) + 5.0F), (double) ((float) (z + 1) + 5.0F)))) {
			if (playerentity.containerMenu instanceof StorageContainer) {
				++i;
			}
		}

		return i;
	}

	private void playSound(SoundEvent soundIn) {
		double d0 = (double) this.worldPosition.getX() + 0.5D;
		double d1 = (double) this.worldPosition.getY() + 0.5D;
		double d2 = (double) this.worldPosition.getZ() + 0.5D;

		this.level.playSound((PlayerEntity) null, d0, d1, d2, soundIn, SoundCategory.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
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
	public void startOpen(PlayerEntity player) {
		if (!player.isSpectator()) {
			if (this.numPlayersUsing < 0) {
				this.numPlayersUsing = 0;
			}

			++this.numPlayersUsing;
			this.onOpenOrClose();
		}
	}

	@Override
	public void stopOpen(PlayerEntity player) {
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
		}
	}

	public NonNullList<ItemStack> getItems() {
		return this.chestContents;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		this.chestContents = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

		ItemStackHelper.loadAllItems(nbt, this.chestContents);

		if (nbt.contains("CustomName", 8)) {
			this.customName = ITextComponent.Serializer.fromJson(nbt.getString("CustomName"));
		}

		this.readPacketNBT(nbt);
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		super.save(compound);

		ItemStackHelper.saveAllItems(compound, this.chestContents);

		if (this.customName != null) {
			compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
		}

		this.writePacketNBT(compound);

		return compound;
	}

	public CompoundNBT saveToNbt(CompoundNBT compound) {
		ItemStackHelper.saveAllItems(compound, this.chestContents, false);
		return compound;
	}

	public void writePacketNBT(CompoundNBT cmp) {
		this.lockCode.write(cmp);
	}

	public void readPacketNBT(CompoundNBT cmp) {
		this.lockCode = StorageLockCode.read(cmp);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return save(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbtTagCompound = new CompoundNBT();
		writePacketNBT(nbtTagCompound);
		return new SUpdateTileEntityPacket(this.worldPosition, 1, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.readPacketNBT(pkt.getTag());
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return storageItemHandler.cast();
		}
		return super.getCapability(cap, side);
	}

	private LazyOptional<?> storageItemHandler = LazyOptional.of(() -> createSidedHandler());

	protected IItemHandler createSidedHandler() {
		return new SidedInvWrapper(this, null);
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
	public void setCustomName(ITextComponent name) {
		this.customName = name;
	}

	@Override
	public ITextComponent getName() {
		return this.customName != null ? this.customName : this.getDefaultName();
	}

	@Override
	public ITextComponent getDisplayName() {
		return this.getName();
	}

	@Override
	@Nullable
	public ITextComponent getCustomName() {
		return this.customName;
	}

	@Override
	public ItemStack getItem(int index) {
		return this.getItems().get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack itemstack = ItemStackHelper.removeItem(this.getItems(), index, count);
		if (!itemstack.isEmpty()) {
			this.setChanged();
		}

		return itemstack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ItemStackHelper.takeItem(this.getItems(), index);
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
	public boolean stillValid(PlayerEntity player) {
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
}
