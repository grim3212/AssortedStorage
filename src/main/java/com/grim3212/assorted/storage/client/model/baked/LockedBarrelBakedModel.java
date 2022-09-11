package com.grim3212.assorted.storage.client.model.baked;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.grim3212.assorted.storage.common.block.blockentity.LockedBarrelBlockEntity;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;

public class LockedBarrelBakedModel extends BakedModelWrapper<BakedModel> {
	protected final ModelBakery bakery;
	protected final Function<Material, TextureAtlasSprite> spriteGetter;
	protected final ModelState transform;
	protected final ResourceLocation name;
	protected final IGeometryBakingContext owner;
	protected final ItemOverrides overrides;
	protected final TextureAtlasSprite baseSprite;
	private final BakedModel lockedModel;

	public LockedBarrelBakedModel(BakedModel unlockedModel, BakedModel lockedModel, IGeometryBakingContext owner, TextureAtlasSprite baseSprite, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation name) {
		super(unlockedModel);
		this.lockedModel = lockedModel;
		this.bakery = bakery;
		this.spriteGetter = spriteGetter;
		this.transform = transform;
		this.name = name;
		this.owner = owner;
		this.overrides = overrides;
		this.baseSprite = baseSprite;
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand) {
		return getQuads(state, side, rand, ModelData.EMPTY, RenderType.solid());
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, @Nullable RenderType renderType) {
		if (extraData.get(LockedBarrelBlockEntity.IS_LOCKED) != null) {
			if (extraData.get(LockedBarrelBlockEntity.IS_LOCKED)) {
				return this.lockedModel.getQuads(state, side, rand, extraData, RenderType.solid());
			}
		}

		return this.originalModel.getQuads(state, side, rand, extraData, RenderType.solid());
	}

	@Override
	public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
		return ChunkRenderTypeSet.of(RenderType.solid());
	}

	public final LockedBarrelItemOverrideList INSTANCE = new LockedBarrelItemOverrideList();

	@Override
	public ItemOverrides getOverrides() {
		return INSTANCE;
	}

	public static final class LockedBarrelItemOverrideList extends ItemOverrides {

		@Override
		public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int field) {
			LockedBarrelBakedModel barrelModel = (LockedBarrelBakedModel) originalModel;

			if (StorageUtil.hasCode(stack)) {
				return barrelModel.lockedModel;
			}

			return barrelModel.originalModel;
		}
	}
}
