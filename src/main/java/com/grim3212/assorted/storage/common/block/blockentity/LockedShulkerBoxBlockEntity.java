package com.grim3212.assorted.storage.common.block.blockentity;

import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.IStorageMaterial;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.inventory.LockedMaterialContainer;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.util.StorageLockCode;
import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity.AnimationStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;

public class LockedShulkerBoxBlockEntity extends RandomizableContainerBlockEntity implements LockedWorldlyContainer, ILockable {

	private final StorageMaterial storageMaterial;
	private int openCount;
	private AnimationStatus animationStatus = AnimationStatus.CLOSED;
	private float progress;
	private float progressOld;
	private NonNullList<ItemStack> itemStacks;
	private final int[] slots;
	private DyeColor color = null;
	private StorageLockCode lockCode = StorageLockCode.EMPTY_CODE;

	public LockedShulkerBoxBlockEntity(StorageMaterial storageMaterial, BlockPos pos, BlockState state) {
		super(StorageBlockEntityTypes.LOCKED_SHULKER_BOX.get(), pos, state);
		this.storageMaterial = storageMaterial;
		int totalItems = storageMaterial != null ? storageMaterial.totalItems() : 27;
		this.itemStacks = NonNullList.withSize(totalItems, ItemStack.EMPTY);
		this.slots = IntStream.range(0, totalItems).toArray();
	}

	public LockedShulkerBoxBlockEntity(BlockPos pos, BlockState state) {
		super(StorageBlockEntityTypes.LOCKED_SHULKER_BOX.get(), pos, state);
		int totalItems = 27;
		if (state.getBlock()instanceof IStorageMaterial storageMaterial) {
			this.storageMaterial = storageMaterial.getStorageMaterial();
			totalItems = this.storageMaterial != null ? this.storageMaterial.totalItems() : 27;
		} else {
			this.storageMaterial = null;
		}
		this.itemStacks = NonNullList.withSize(totalItems, ItemStack.EMPTY);
		this.slots = IntStream.range(0, totalItems).toArray();
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
	public int getContainerSize() {
		return this.itemStacks.size();
	}

	@Override
	public boolean triggerEvent(int p_59678_, int p_59679_) {
		if (p_59678_ == 1) {
			this.openCount = p_59679_;
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

	@Override
	public void startOpen(Player player) {
		if (!player.isSpectator()) {
			if (this.openCount < 0) {
				this.openCount = 0;
			}

			++this.openCount;
			this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
			if (this.openCount == 1) {
				this.level.gameEvent(player, GameEvent.CONTAINER_OPEN, this.worldPosition);
				this.level.playSound((Player) null, this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
			}
		}

	}

	@Override
	public void stopOpen(Player player) {
		if (!player.isSpectator()) {
			--this.openCount;
			this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
			if (this.openCount <= 0) {
				this.level.gameEvent(player, GameEvent.CONTAINER_CLOSE, this.worldPosition);
				this.level.playSound((Player) null, this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
			}
		}

	}

	@Override
	protected Component getDefaultName() {
		if (this.storageMaterial == null) {
			return Component.translatable(AssortedStorage.MODID + ".container.locked_shulker_box");
		}

		return Component.translatable(AssortedStorage.MODID + ".container.shulker_" + this.storageMaterial.toString());
	}

	public AABB getBoundingBox(BlockState state) {
		return Shulker.getProgressAabb(state.getValue(LockedShulkerBoxBlock.FACING), 0.5F * this.getProgress(1.0F));
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		this.loadFromTag(compound);
		this.color = colorFromCompound(compound);
		this.lockCode = StorageLockCode.read(compound);
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		if (!this.trySaveLootTable(compound)) {
			ContainerHelper.saveAllItems(compound, this.itemStacks, false);
		}
		compound.putInt("Color", this.colorToSave());
		this.lockCode.write(compound);
	}

	public void loadFromTag(CompoundTag compound) {
		this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(compound) && compound.contains("Items", 9)) {
			ContainerHelper.loadAllItems(compound, this.itemStacks);
		}
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return this.itemStacks;
	}

	@Override
	public void setItems(NonNullList<ItemStack> stacks) {
		if (stacks.size() == this.itemStacks.size()) {
			this.itemStacks = stacks;
		}

		this.itemStacks = NonNullList.<ItemStack>withSize(this.itemStacks.size(), ItemStack.EMPTY);

		for (int i = 0; i < stacks.size(); i++) {
			this.itemStacks.set(i, stacks.get(i));
		}

		this.setChanged();
	}

	@Override
	public int[] getSlotsForFace(Direction dir) {
		return slots;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
		return !this.isLocked() && !(Block.byItem(stack.getItem()) instanceof LockedShulkerBoxBlock || Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock) && stack.getItem().canFitInsideContainerItems();
	}

	@Override
	public boolean canTakeItemThroughFace(int p_59682_, ItemStack p_59683_, Direction p_59684_) {
		return !this.isLocked();
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction, String code, boolean force) {
		if (!(Block.byItem(itemStackIn.getItem()) instanceof LockedShulkerBoxBlock || Block.byItem(itemStackIn.getItem()) instanceof ShulkerBoxBlock) && itemStackIn.getItem().canFitInsideContainerItems()) {
			return force || (this.isLocked() ? this.lockCode.getLockCode().equals(code) : true);
		}

		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction, String code, boolean force) {
		return force || (this.isLocked() ? this.lockCode.getLockCode().equals(code) : true);
	}

	public float getProgress(float p_59658_) {
		return Mth.lerp(p_59658_, this.progressOld, this.progress);
	}

	public boolean isClosed() {
		return this.animationStatus == AnimationStatus.CLOSED;
	}

	@Override
	protected IItemHandler createUnSidedHandler() {
		return new LockedSidedInvWrapper(this, Direction.UP);
	}

	@Override
	protected AbstractContainerMenu createMenu(int windowId, Inventory playerInv) {
		return new LockedMaterialContainer(StorageContainerTypes.SHULKERS.get(storageMaterial).get(), windowId, playerInv, this, storageMaterial, true);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
		return new LockedMaterialContainer(StorageContainerTypes.SHULKERS.get(storageMaterial).get(), windowId, player, this, storageMaterial, true);
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
