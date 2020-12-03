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
import net.minecraft.util.INameable;
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
public abstract class BaseStorageTileEntity extends TileEntity implements ISidedInventory, INamedContainerProvider, INameable, IStorage, ITickableTileEntity {

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

	public boolean isLocked() {
		return this.lockCode != null && this.lockCode != StorageLockCode.EMPTY_CODE;
	}

	public String getLockCode() {
		return this.lockCode.getLockCode();
	}

	public void setLockCode(String s) {
		if (s == null || s.isEmpty())
			this.lockCode = StorageLockCode.EMPTY_CODE;
		else
			this.lockCode = new StorageLockCode(s);
	}

	@Override
	public int getSizeInventory() {
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
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		++this.ticksSinceSync;
		this.numPlayersUsing = getNumberOfPlayersUsing(this.world, this, this.ticksSinceSync, i, j, k, this.numPlayersUsing);
		this.prevRotation = this.rotation;
		if (this.numPlayersUsing > 0 && this.rotation == 0.0F) {
			this.playSound(SoundEvents.BLOCK_CHEST_OPEN);
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
				this.playSound(SoundEvents.BLOCK_CHEST_CLOSE);
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
		if (!worldIn.isRemote && numPlayersUsing != 0 && (ticksSinceSync + x + y + z) % 200 == 0) {
			numPlayersUsing = getNumberOfPlayersUsing(worldIn, lockableTileEntity, x, y, z);
		}

		return numPlayersUsing;
	}

	public static int getNumberOfPlayersUsing(World world, BaseStorageTileEntity lockableTileEntity, int x, int y, int z) {
		int i = 0;

		for (PlayerEntity playerentity : world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB((double) ((float) x - 5.0F), (double) ((float) y - 5.0F), (double) ((float) z - 5.0F), (double) ((float) (x + 1) + 5.0F), (double) ((float) (y + 1) + 5.0F), (double) ((float) (z + 1) + 5.0F)))) {
			if (playerentity.openContainer instanceof StorageContainer) {
				++i;
			}
		}

		return i;
	}

	private void playSound(SoundEvent soundIn) {
		double d0 = (double) this.pos.getX() + 0.5D;
		double d1 = (double) this.pos.getY() + 0.5D;
		double d2 = (double) this.pos.getZ() + 0.5D;

		this.world.playSound((PlayerEntity) null, d0, d1, d2, soundIn, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			this.numPlayersUsing = type;
			return true;
		} else {
			return super.receiveClientEvent(id, type);
		}
	}

	@Override
	public void openInventory(PlayerEntity player) {
		if (!player.isSpectator()) {
			if (this.numPlayersUsing < 0) {
				this.numPlayersUsing = 0;
			}

			++this.numPlayersUsing;
			this.onOpenOrClose();
		}
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		if (!player.isSpectator()) {
			--this.numPlayersUsing;
			this.onOpenOrClose();
		}
	}

	protected void onOpenOrClose() {
		Block block = this.getBlockState().getBlock();

		if (block instanceof BaseStorageBlock) {
			this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
			this.world.notifyNeighborsOfStateChange(this.pos, block);
		}
	}

	public NonNullList<ItemStack> getItems() {
		return this.chestContents;
	}

	public abstract Block getBlockToUse();

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

		ItemStackHelper.loadAllItems(nbt, this.chestContents);

		if (nbt.contains("CustomName", 8)) {
			this.customName = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName"));
		}

		this.readPacketNBT(nbt);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);

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
		return write(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbtTagCompound = new CompoundNBT();
		writePacketNBT(nbtTagCompound);
		return new SUpdateTileEntityPacket(this.pos, 1, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.readPacketNBT(pkt.getNbtCompound());
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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
	public void remove() {
		super.remove();
		this.storageItemHandler.invalidate();
	}

	@Override
	public void clear() {
		this.getItems().clear();
	}

	public void setCustomName(ITextComponent name) {
		this.customName = name;
	}

	public ITextComponent getName() {
		return this.customName != null ? this.customName : this.getDefaultName();
	}

	public ITextComponent getDisplayName() {
		return this.getName();
	}

	@Nullable
	public ITextComponent getCustomName() {
		return this.customName;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return this.getItems().get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack = ItemStackHelper.getAndSplit(this.getItems(), index, count);
		if (!itemstack.isEmpty()) {
			this.markDirty();
		}

		return itemstack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(this.getItems(), index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.getItems().set(index, stack);
		if (stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}

		this.markDirty();
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		if (this.world.getTileEntity(this.pos) != this) {
			return false;
		} else {
			return !(player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) > 64.0D);
		}
	}

	protected static final int[] DEFAULT_SLOTS = IntStream.range(0, 27).toArray();

	@Override
	public int[] getSlotsForFace(Direction side) {
		return DEFAULT_SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return !this.isLocked();
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return !this.isLocked();
	}
}
