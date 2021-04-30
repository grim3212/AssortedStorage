package com.grim3212.assorted.storage.common.block;

import java.util.List;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.tileentity.GoldSafeTileEntity;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageLockCode;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class GoldSafeBlock extends BaseStorageBlock {

	public static final ResourceLocation CONTENTS = new ResourceLocation(AssortedStorage.MODID, "contents");

	public GoldSafeBlock(Properties properties) {
		super(properties.setRequiresTool().hardnessAndResistance(50.0F, 1200.0F));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new GoldSafeTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return ObsidianSafeBlock.SAFE_SHAPE;
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.matchesBlock(newState.getBlock())) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof GoldSafeTileEntity) {
				GoldSafeTileEntity goldsafetileentity = (GoldSafeTileEntity) tileentity;

				if (goldsafetileentity.isLocked()) {
					ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
					CompoundNBT tag = new CompoundNBT();
					new StorageLockCode(goldsafetileentity.getLockCode()).write(tag);
					lockStack.setTag(tag);
					InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), lockStack);
				}

				worldIn.updateComparatorOutputLevel(pos, state.getBlock());
			}

			if (state.hasTileEntity() && (!state.matchesBlock(newState.getBlock()) || !newState.hasTileEntity())) {
				worldIn.removeTileEntity(pos);
			}
		}
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		ItemStack itemstack = super.getItem(worldIn, pos, state);
		GoldSafeTileEntity goldsafetileentity = (GoldSafeTileEntity) worldIn.getTileEntity(pos);
		CompoundNBT compoundnbt = goldsafetileentity.saveToNbt(new CompoundNBT());
		if (!compoundnbt.isEmpty()) {
			itemstack.setTagInfo("BlockEntityTag", compoundnbt);
		}

		return itemstack;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof GoldSafeTileEntity) {
			GoldSafeTileEntity goldsafetileentity = (GoldSafeTileEntity) tileentity;
			if (!worldIn.isRemote && player.isCreative() && !goldsafetileentity.isEmpty()) {
				ItemStack itemstack = new ItemStack(StorageBlocks.GOLD_SAFE.get());
				CompoundNBT compoundnbt = goldsafetileentity.saveToNbt(new CompoundNBT());
				if (!compoundnbt.isEmpty()) {
					itemstack.setTagInfo("BlockEntityTag", compoundnbt);
				}

				if (goldsafetileentity.hasCustomName()) {
					itemstack.setDisplayName(goldsafetileentity.getCustomName());
				}

				ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickupDelay();
				worldIn.addEntity(itementity);
			}
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		TileEntity tileentity = builder.get(LootParameters.BLOCK_ENTITY);
		if (tileentity instanceof GoldSafeTileEntity) {
			GoldSafeTileEntity goldsafetileentity = (GoldSafeTileEntity) tileentity;
			builder = builder.withDynamicDrop(CONTENTS, (context, stackConsumer) -> {
				for (int i = 0; i < goldsafetileentity.getSizeInventory(); ++i) {
					stackConsumer.accept(goldsafetileentity.getStackInSlot(i));
				}

			});
		}

		return super.getDrops(state, builder);
	}

	@Override
	public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		CompoundNBT compoundnbt = stack.getChildTag("BlockEntityTag");
		if (compoundnbt != null) {
			if (compoundnbt.contains("LootTable", 8)) {
				tooltip.add(new StringTextComponent("???????"));
			}

			if (compoundnbt.contains("Items", 9)) {
				NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
				ItemStackHelper.loadAllItems(compoundnbt, nonnulllist);
				int i = 0;
				int j = 0;

				for (ItemStack itemstack : nonnulllist) {
					if (!itemstack.isEmpty()) {
						++j;
						if (i <= 4) {
							++i;
							IFormattableTextComponent iformattabletextcomponent = itemstack.getDisplayName().deepCopy();
							iformattabletextcomponent.appendString(" x").appendString(String.valueOf(itemstack.getCount()));
							tooltip.add(iformattabletextcomponent);
						}
					}
				}

				if (j - i > 0) {
					tooltip.add((new TranslationTextComponent("container.shulkerBox.more", j - i)).mergeStyle(TextFormatting.ITALIC));
				}
			}
		}
	}
}
