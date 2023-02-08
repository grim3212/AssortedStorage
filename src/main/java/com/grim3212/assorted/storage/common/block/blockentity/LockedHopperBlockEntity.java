package com.grim3212.assorted.storage.common.block.blockentity;

import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.LockedHopperBlock;
import com.grim3212.assorted.storage.common.inventory.LockedHopperContainer;
import com.grim3212.assorted.storage.common.inventory.LockedMaterialContainer;
import com.grim3212.assorted.storage.common.inventory.LockedSidedInvWrapper;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

public class LockedHopperBlockEntity extends BaseStorageBlockEntity implements Hopper {

	private int cooldownTime = -1;
	private long tickedGameTime;

	private final StorageMaterial storageMaterial;
	protected final int[] slots;

	public LockedHopperBlockEntity(BlockPos pos, BlockState state) {
		super(StorageBlockEntityTypes.LOCKED_HOPPER.get(), pos, state);

		if (state.getBlock()instanceof LockedHopperBlock lockedHopper) {
			this.storageMaterial = lockedHopper.getStorageMaterial();
			setStartingContents(this.storageMaterial != null ? this.storageMaterial.hopperSize() : 5);
		} else {
			// Default to regular chest
			this.storageMaterial = null;
			this.setStartingContents(5);
		}

		this.slots = IntStream.range(0, this.getContainerSize()).toArray();
	}
	
	public static int getHopperCooldown(StorageMaterial storageMaterial) {
		return storageMaterial == null ? HopperBlockEntity.MOVE_ITEM_SPEED : storageMaterial.hopperCooldown();
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return this.slots;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
		return new LockedHopperContainer(StorageContainerTypes.HOPPERS.get(storageMaterial).get(), windowId, player, this, storageMaterial);
	}

	@Override
	protected Component getDefaultName() {
		if (this.storageMaterial == null) {
			return Component.translatable(AssortedStorage.MODID + ".container.locked_hopper");
		}

		return Component.translatable(AssortedStorage.MODID + ".container.hopper_" + this.storageMaterial.toString());
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		requestModelDataUpdate();
		if (level instanceof ClientLevel) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
		}
	}

	@Override
	public void setLockCode(String s) {
		super.setLockCode(s);
		requestModelDataUpdate();
		if (level instanceof ClientLevel) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
		}
	}

	@Override
	public ModelData getModelData() {
		return ModelData.builder().with(ModelProperties.IS_LOCKED, this.isLocked()).build();
	}

	@Override
	protected SoundEvent openSound() {
		return null;
	}

	@Override
	protected SoundEvent closeSound() {
		return null;
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);

		this.cooldownTime = nbt.getInt("TransferCooldown");
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putInt("TransferCooldown", this.cooldownTime);
	}

	@Override
	public int getNumberOfPlayersUsing(Level world, BaseStorageBlockEntity lockableTileEntity, int x, int y, int z) {
		int i = 0;

		for (Player playerentity : world.getEntitiesOfClass(Player.class, new AABB((double) ((float) x - 5.0F), (double) ((float) y - 5.0F), (double) ((float) z - 5.0F), (double) ((float) (x + 1) + 5.0F), (double) ((float) (y + 1) + 5.0F), (double) ((float) (z + 1) + 5.0F)))) {
			if (playerentity.containerMenu instanceof LockedMaterialContainer) {
				++i;
			}
		}

		return i;
	}

	@Override
	protected IItemHandler createSidedHandler() {
		return new ItemHandler(this);
	}

	public static void pushItemsTick(Level level, BlockPos pos, BlockState state, LockedHopperBlockEntity hopperBE) {
		--hopperBE.cooldownTime;
		hopperBE.tickedGameTime = level.getGameTime();
		if (!hopperBE.isOnCooldown()) {
			hopperBE.setCooldown(0);
			tryMoveItems(level, pos, state, hopperBE, () -> {
				return suckInItems(level, hopperBE);
			});
		}

	}

	private static boolean tryMoveItems(Level level, BlockPos pos, BlockState state, LockedHopperBlockEntity hopperBE, BooleanSupplier supplier) {
		if (level.isClientSide) {
			return false;
		} else {
			if (!hopperBE.isOnCooldown() && state.getValue(LockedHopperBlock.ENABLED)) {
				boolean flag = false;
				if (!hopperBE.isEmpty()) {
					flag = ejectItems(level, pos, state, hopperBE);
				}

				if (!hopperBE.inventoryFull()) {
					flag |= supplier.getAsBoolean();
				}

				if (flag) {
					hopperBE.setCooldown(getHopperCooldown(hopperBE.storageMaterial));
					setChanged(level, pos, state);
					return true;
				}
			}

			return false;
		}
	}

	private boolean inventoryFull() {
		for (ItemStack itemstack : this.getItems()) {
			if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
				return false;
			}
		}

		return true;
	}

	private static boolean isEmpty(IItemHandler itemHandler) {
		for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
			ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
			if (stackInSlot.getCount() > 0) {
				return false;
			}
		}
		return true;
	}

	private static Optional<Pair<IItemHandler, Object>> getItemHandler(Level level, Hopper hopper, Direction hopperFacing) {
		double x = hopper.getLevelX() + (double) hopperFacing.getStepX();
		double y = hopper.getLevelY() + (double) hopperFacing.getStepY();
		double z = hopper.getLevelZ() + (double) hopperFacing.getStepZ();
		return VanillaInventoryCodeHooks.getItemHandler(level, x, y, z, hopperFacing.getOpposite());
	}

	private static boolean isFull(IItemHandler itemHandler) {
		for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
			ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
			if (stackInSlot.isEmpty() || stackInSlot.getCount() < itemHandler.getSlotLimit(slot)) {
				return false;
			}
		}
		return true;
	}

	private static ItemStack putStackInInventoryAllSlots(LockedHopperBlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack) {
		for (int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++) {
			stack = insertStack(source, destination, destInventory, stack, slot);
		}
		return stack;
	}

	private static ItemStack insertStack(LockedHopperBlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot) {
		ItemStack itemstack = destInventory.getStackInSlot(slot);

		if (destInventory.insertItem(slot, stack, true).isEmpty()) {
			boolean insertedItem = false;
			boolean inventoryWasEmpty = isEmpty(destInventory);

			if (itemstack.isEmpty()) {
				destInventory.insertItem(slot, stack, false);
				stack = ItemStack.EMPTY;
				insertedItem = true;
			} else if (ItemHandlerHelper.canItemStacksStack(itemstack, stack)) {
				int originalSize = stack.getCount();
				stack = destInventory.insertItem(slot, stack, false);
				insertedItem = originalSize < stack.getCount();
			}

			if (insertedItem) {
				if (inventoryWasEmpty) {
					if (destination instanceof LockedHopperBlockEntity hopperBE) {

						if (!hopperBE.isOnCustomCooldown()) {
							int k = 0;
							if (source instanceof LockedHopperBlockEntity) {
								if (hopperBE.getLastUpdateTime() >= ((LockedHopperBlockEntity) source).getLastUpdateTime()) {
									k = 1;
								}
							}
							hopperBE.setCooldown(getHopperCooldown(hopperBE.storageMaterial) - k);
						}
					}
				}
			}
		} else if (destInventory instanceof LockedItemHandler lockedInv) {
			if (lockedInv.insertItem(slot, stack, true, source.getLockCode()).isEmpty()) {
				boolean insertedItem = false;
				boolean inventoryWasEmpty = isEmpty(destInventory);

				if (itemstack.isEmpty()) {
					lockedInv.insertItem(slot, stack, false, source.getLockCode());
					stack = ItemStack.EMPTY;
					insertedItem = true;
				} else if (ItemHandlerHelper.canItemStacksStack(itemstack, stack)) {
					int originalSize = stack.getCount();
					stack = lockedInv.insertItem(slot, stack, false, source.getLockCode());
					insertedItem = originalSize < stack.getCount();
				}

				if (insertedItem) {
					if (inventoryWasEmpty) {
						if (destination instanceof LockedHopperBlockEntity hopperBE) {
							if (!hopperBE.isOnCustomCooldown()) {
								int k = 0;
								if (source instanceof LockedHopperBlockEntity) {
									if (hopperBE.getLastUpdateTime() >= ((LockedHopperBlockEntity) source).getLastUpdateTime()) {
										k = 1;
									}
								}
								hopperBE.setCooldown(getHopperCooldown(hopperBE.storageMaterial) - k);
							}
						}
					}
				}
			}
		}

		return stack;
	}

	private static boolean insertHook(LockedHopperBlockEntity hopper) {
		Direction hopperFacing = hopper.getBlockState().getValue(LockedHopperBlock.FACING);
		return getItemHandler(hopper.getLevel(), hopper, hopperFacing).map(destinationResult -> {
			IItemHandler itemHandler = destinationResult.getKey();
			Object destination = destinationResult.getValue();
			if (isFull(itemHandler)) {
				return false;
			} else {
				for (int i = 0; i < hopper.getContainerSize(); ++i) {
					if (!hopper.getItem(i).isEmpty()) {
						ItemStack originalSlotContents = hopper.getItem(i).copy();
						ItemStack insertStack = hopper.removeItem(i, 1);
						ItemStack remainder = putStackInInventoryAllSlots(hopper, destination, itemHandler, insertStack);

						if (remainder.isEmpty()) {
							return true;
						}

						hopper.setItem(i, originalSlotContents);
					}
				}

				return false;
			}
		}).orElse(false);
	}

	@Nullable
	public static Boolean extractHook(Level level, LockedHopperBlockEntity dest) {
		return getItemHandler(level, dest, Direction.UP).map(itemHandlerResult -> {
			IItemHandler handler = itemHandlerResult.getKey();

			if (handler instanceof LockedItemHandler lockedItemHandler) {
				for (int i = 0; i < handler.getSlots(); i++) {
					ItemStack extractItem = lockedItemHandler.extractItem(i, 1, true, dest.getLockCode());
					if (!extractItem.isEmpty()) {
						for (int j = 0; j < dest.getContainerSize(); j++) {
							ItemStack destStack = dest.getItem(j);
							if (dest.canPlaceItem(j, extractItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize() && destStack.getCount() < dest.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(extractItem, destStack))) {
								extractItem = lockedItemHandler.extractItem(i, 1, false, dest.getLockCode());
								if (destStack.isEmpty())
									dest.setItem(j, extractItem);
								else {
									destStack.grow(1);
									dest.setItem(j, destStack);
								}
								dest.setChanged();
								return true;
							}
						}
					}
				}
			} else {
				for (int i = 0; i < handler.getSlots(); i++) {
					ItemStack extractItem = handler.extractItem(i, 1, true);
					if (!extractItem.isEmpty()) {
						for (int j = 0; j < dest.getContainerSize(); j++) {
							ItemStack destStack = dest.getItem(j);
							if (dest.canPlaceItem(j, extractItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize() && destStack.getCount() < dest.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(extractItem, destStack))) {
								extractItem = handler.extractItem(i, 1, false);
								if (destStack.isEmpty())
									dest.setItem(j, extractItem);
								else {
									destStack.grow(1);
									dest.setItem(j, destStack);
								}
								dest.setChanged();
								return true;
							}
						}
					}
				}
			}

			return false;
		}).orElse(null);
	}

	private static boolean ejectItems(Level level, BlockPos pos, BlockState state, LockedHopperBlockEntity hopper) {
		if (insertHook(hopper))
			return true;
		Container container = getAttachedContainer(level, pos, state);
		if (container == null) {
			return false;
		} else {
			Direction direction = state.getValue(LockedHopperBlock.FACING).getOpposite();
			if (isFullContainer(container, direction)) {
				return false;
			} else {
				for (int i = 0; i < hopper.getContainerSize(); ++i) {
					if (!hopper.getItem(i).isEmpty()) {
						ItemStack itemstack = hopper.getItem(i).copy();
						ItemStack itemstack1 = addItem(hopper, container, hopper.removeItem(i, 1), direction);
						if (itemstack1.isEmpty()) {
							container.setChanged();
							return true;
						}

						hopper.setItem(i, itemstack);
					}
				}

				return false;
			}
		}
	}

	private static IntStream getSlots(Container container, Direction dir) {
		return container instanceof WorldlyContainer ? IntStream.of(((WorldlyContainer) container).getSlotsForFace(dir)) : IntStream.range(0, container.getContainerSize());
	}

	private static boolean isFullContainer(Container container, Direction dir) {
		return getSlots(container, dir).allMatch((p_59379_) -> {
			ItemStack itemstack = container.getItem(p_59379_);
			return itemstack.getCount() >= itemstack.getMaxStackSize();
		});
	}

	private static boolean isEmptyContainer(Container container, Direction dir) {
		return getSlots(container, dir).allMatch((p_59319_) -> {
			return container.getItem(p_59319_).isEmpty();
		});
	}

	public static boolean suckInItems(Level level, LockedHopperBlockEntity hopper) {
		Boolean ret = extractHook(level, hopper);
		if (ret != null)
			return ret;
		Container container = getSourceContainer(level, hopper);
		if (container != null) {
			Direction direction = Direction.DOWN;
			return isEmptyContainer(container, direction) ? false : getSlots(container, direction).anyMatch((p_59363_) -> {
				return tryTakeInItemFromSlot(hopper, container, p_59363_, direction);
			});
		} else {
			for (ItemEntity itementity : getItemsAtAndAbove(level, hopper)) {
				if (addItem(hopper, itementity)) {
					return true;
				}
			}

			return false;
		}
	}

	private static boolean tryTakeInItemFromSlot(Hopper self, Container dest, int slot, Direction dir) {
		ItemStack itemstack = dest.getItem(slot);
		if (!itemstack.isEmpty() && canTakeItemFromContainer(dest, self, itemstack, slot, dir)) {
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = addItem(dest, self, dest.removeItem(slot, 1), (Direction) null);
			if (itemstack2.isEmpty()) {
				dest.setChanged();
				return true;
			}

			dest.setItem(slot, itemstack1);
		}

		return false;
	}

	public static boolean addItem(Container p_59332_, ItemEntity p_59333_) {
		boolean flag = false;
		ItemStack itemstack = p_59333_.getItem().copy();
		ItemStack itemstack1 = addItem((Container) null, p_59332_, itemstack, (Direction) null);
		if (itemstack1.isEmpty()) {
			flag = true;
			p_59333_.discard();
		} else {
			p_59333_.setItem(itemstack1);
		}

		return flag;
	}

	public static ItemStack addItem(@Nullable Container self, Container dest, ItemStack stack, @Nullable Direction dir) {
		if (dest instanceof WorldlyContainer worldlycontainer && dir != null) {
			int[] aint = worldlycontainer.getSlotsForFace(dir);

			for (int k = 0; k < aint.length && !stack.isEmpty(); ++k) {
				stack = tryMoveInItem(self, dest, stack, aint[k], dir);
			}
		} else {
			int i = dest.getContainerSize();

			for (int j = 0; j < i && !stack.isEmpty(); ++j) {
				stack = tryMoveInItem(self, dest, stack, j, dir);
			}
		}

		return stack;
	}

	private static boolean canPlaceItemInContainer(Container dest, @Nullable Container self, ItemStack stack, int slot, @Nullable Direction dir) {
		if (!dest.canPlaceItem(slot, stack)) {
			return false;
		} else {
			if (dest instanceof LockedWorldlyContainer destLocked) {
				if (self instanceof ILockable lockedBE) {
					return destLocked.canPlaceItemThroughFace(slot, stack, dir, lockedBE.getLockCode(), false);
				} else {
					return destLocked.canPlaceItemThroughFace(slot, stack, dir, "", self == null);
				}
			}

			return !(dest instanceof WorldlyContainer) || ((WorldlyContainer) dest).canPlaceItemThroughFace(slot, stack, dir);
		}
	}

	private static boolean canTakeItemFromContainer(Container dest, @Nullable Container self, ItemStack stack, int slot, Direction dir) {
		if (dest instanceof LockedWorldlyContainer destLocked) {
			if (self instanceof ILockable lockedBE) {
				return destLocked.canTakeItemThroughFace(slot, stack, dir, lockedBE.getLockCode(), false);
			}
		}

		return !(dest instanceof WorldlyContainer) || ((WorldlyContainer) dest).canTakeItemThroughFace(slot, stack, dir);
	}

	private static ItemStack tryMoveInItem(@Nullable Container self, Container dest, ItemStack stack, int slot, @Nullable Direction dir) {
		ItemStack itemstack = dest.getItem(slot);
		if (canPlaceItemInContainer(dest, self, stack, slot, dir)) {
			boolean flag = false;
			boolean flag1 = dest.isEmpty();
			if (itemstack.isEmpty()) {
				dest.setItem(slot, stack);
				stack = ItemStack.EMPTY;
				flag = true;
			} else if (canMergeItems(itemstack, stack)) {
				int i = stack.getMaxStackSize() - itemstack.getCount();
				int j = Math.min(stack.getCount(), i);
				stack.shrink(j);
				itemstack.grow(j);
				flag = j > 0;
			}

			if (flag) {
				if (flag1) {
					if (dest instanceof LockedHopperBlockEntity) {
						LockedHopperBlockEntity hopperblockentity1 = (LockedHopperBlockEntity) dest;
						if (!hopperblockentity1.isOnCustomCooldown()) {
							int k = 0;
							if (self instanceof LockedHopperBlockEntity) {
								LockedHopperBlockEntity hopperblockentity = (LockedHopperBlockEntity) self;
								if (hopperblockentity1.tickedGameTime >= hopperblockentity.tickedGameTime) {
									k = 1;
								}
							}

							hopperblockentity1.setCooldown(getHopperCooldown(hopperblockentity1.storageMaterial) - k);
						}
					}
				}

				dest.setChanged();
			}
		}

		return stack;
	}

	@Nullable
	private static Container getAttachedContainer(Level p_155593_, BlockPos p_155594_, BlockState p_155595_) {
		Direction direction = p_155595_.getValue(LockedHopperBlock.FACING);
		return getContainerAt(p_155593_, p_155594_.relative(direction));
	}

	@Nullable
	private static Container getSourceContainer(Level p_155597_, Hopper p_155598_) {
		return getContainerAt(p_155597_, p_155598_.getLevelX(), p_155598_.getLevelY() + 1.0D, p_155598_.getLevelZ());
	}

	public static List<ItemEntity> getItemsAtAndAbove(Level p_155590_, Hopper p_155591_) {
		return p_155591_.getSuckShape().toAabbs().stream().flatMap((p_155558_) -> {
			return p_155590_.getEntitiesOfClass(ItemEntity.class, p_155558_.move(p_155591_.getLevelX() - 0.5D, p_155591_.getLevelY() - 0.5D, p_155591_.getLevelZ() - 0.5D), EntitySelector.ENTITY_STILL_ALIVE).stream();
		}).collect(Collectors.toList());
	}

	@Nullable
	public static Container getContainerAt(Level p_59391_, BlockPos p_59392_) {
		return getContainerAt(p_59391_, (double) p_59392_.getX() + 0.5D, (double) p_59392_.getY() + 0.5D, (double) p_59392_.getZ() + 0.5D);
	}

	@Nullable
	private static Container getContainerAt(Level p_59348_, double p_59349_, double p_59350_, double p_59351_) {
		Container container = null;
		BlockPos blockpos = new BlockPos(p_59349_, p_59350_, p_59351_);
		BlockState blockstate = p_59348_.getBlockState(blockpos);
		Block block = blockstate.getBlock();
		if (block instanceof WorldlyContainerHolder) {
			container = ((WorldlyContainerHolder) block).getContainer(blockstate, p_59348_, blockpos);
		} else if (blockstate.hasBlockEntity()) {
			BlockEntity blockentity = p_59348_.getBlockEntity(blockpos);
			if (blockentity instanceof Container) {
				container = (Container) blockentity;
				if (container instanceof ChestBlockEntity && block instanceof ChestBlock) {
					container = ChestBlock.getContainer((ChestBlock) block, blockstate, p_59348_, blockpos, true);
				}
			}
		}

		if (container == null) {
			List<Entity> list = p_59348_.getEntities((Entity) null, new AABB(p_59349_ - 0.5D, p_59350_ - 0.5D, p_59351_ - 0.5D, p_59349_ + 0.5D, p_59350_ + 0.5D, p_59351_ + 0.5D), EntitySelector.CONTAINER_ENTITY_SELECTOR);
			if (!list.isEmpty()) {
				container = (Container) list.get(p_59348_.random.nextInt(list.size()));
			}
		}

		return container;
	}

	private static boolean canMergeItems(ItemStack p_59345_, ItemStack p_59346_) {
		if (!p_59345_.is(p_59346_.getItem())) {
			return false;
		} else if (p_59345_.getDamageValue() != p_59346_.getDamageValue()) {
			return false;
		} else if (p_59345_.getCount() > p_59345_.getMaxStackSize()) {
			return false;
		} else {
			return ItemStack.tagMatches(p_59345_, p_59346_);
		}
	}

	public double getLevelX() {
		return (double) this.worldPosition.getX() + 0.5D;
	}

	public double getLevelY() {
		return (double) this.worldPosition.getY() + 0.5D;
	}

	public double getLevelZ() {
		return (double) this.worldPosition.getZ() + 0.5D;
	}

	public void setCooldown(int p_59396_) {
		this.cooldownTime = p_59396_;
	}

	private boolean isOnCooldown() {
		return this.cooldownTime > 0;
	}

	public boolean isOnCustomCooldown() {
		return this.cooldownTime > getHopperCooldown(storageMaterial);
	}

	public static void entityInside(Level level, BlockPos pos, BlockState state, Entity entity, LockedHopperBlockEntity hopperBE) {
		if (entity instanceof ItemEntity && Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move((double) (-pos.getX()), (double) (-pos.getY()), (double) (-pos.getZ()))), hopperBE.getSuckShape(), BooleanOp.AND)) {
			tryMoveItems(level, pos, state, hopperBE, () -> {
				return addItem(hopperBE, (ItemEntity) entity);
			});
		}
	}

	public long getLastUpdateTime() {
		return this.tickedGameTime;
	}

	public static class ItemHandler extends LockedSidedInvWrapper {
		private final LockedHopperBlockEntity hopper;

		public ItemHandler(LockedHopperBlockEntity hopper) {
			super(hopper, null);
			this.hopper = hopper;
		}

		@Override
		@NotNull
		public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
			if (simulate) {
				return super.insertItem(slot, stack, simulate);
			} else {
				boolean wasEmpty = getInv().isEmpty();

				int originalStackSize = stack.getCount();
				stack = super.insertItem(slot, stack, simulate);

				if (wasEmpty && originalStackSize > stack.getCount()) {
					if (!hopper.isOnCustomCooldown()) {
						hopper.setCooldown(getHopperCooldown(hopper.storageMaterial));
					}
				}

				return stack;
			}
		}
	}
}
