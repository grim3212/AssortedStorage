package com.grim3212.assorted.storage.client.model.baked;

import com.google.common.collect.ImmutableList;
import com.grim3212.assorted.lib.client.model.ItemOverridesExtension;
import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.common.properties.StorageModelProperties;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class LockedBakedModel implements IDataAwareBakedModel {

    protected final ModelBaker bakery;
    protected final Function<Material, TextureAtlasSprite> spriteGetter;
    protected final ModelState transform;
    protected final ResourceLocation name;
    protected final IModelBakingContext context;
    private final BakedModel unlockedModel;
    private final BakedModel lockedModel;
    private final LockedBarrelItemOverrideList itemOverrideList;
    private final TextureAtlasSprite particle;

    public LockedBakedModel(IModelBakingContext context, BakedModel unlockedModel, BakedModel lockedModel, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation name) {
        this.unlockedModel = unlockedModel;
        this.lockedModel = lockedModel;
        this.bakery = bakery;
        this.spriteGetter = spriteGetter;
        this.transform = transform;
        this.name = name;
        this.context = context;
        this.itemOverrideList = new LockedBarrelItemOverrideList(context);
        this.particle = spriteGetter.apply(context.getMaterial("particle").orElse(null));
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.unlockedModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.unlockedModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.unlockedModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.unlockedModel.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.particle;
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.unlockedModel.getTransforms();
    }


    @Override
    public ItemOverrides getOverrides() {
        return this.itemOverrideList;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand) {
        return getQuads(state, side, rand, IBlockModelData.empty(), RenderType.solid());
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull IBlockModelData extraData, @Nullable RenderType renderType) {
        if (extraData.hasProperty(StorageModelProperties.IS_LOCKED)) {
            if (extraData.getData(StorageModelProperties.IS_LOCKED)) {
                return this.lockedModel.getQuads(state, side, rand);
            }
        }

        return this.unlockedModel.getQuads(state, side, rand);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(ItemStack stack, boolean fabulous, @NotNull RandomSource rand, @Nullable RenderType renderType) {
        return getQuads(null, null, rand, IBlockModelData.empty(), renderType);
    }

    @Override
    public @NotNull Collection<RenderType> getSupportedRenderTypes(BlockState state, RandomSource rand, IBlockModelData data) {
        return ImmutableList.of(RenderType.solid());
    }

    @Override
    public @NotNull Collection<RenderType> getSupportedRenderTypes(ItemStack stack, boolean fabulous) {
        return ImmutableList.of(RenderType.solid());
    }

    public static final class LockedBarrelItemOverrideList extends ItemOverridesExtension {

        protected LockedBarrelItemOverrideList(IModelBakingContext context) {
            super(context);
        }

        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int field) {
            LockedBakedModel lockedModel = (LockedBakedModel) originalModel;

            if (StorageUtil.hasCode(stack)) {
                return lockedModel.lockedModel;
            }

            return lockedModel.unlockedModel;
        }
    }
}
