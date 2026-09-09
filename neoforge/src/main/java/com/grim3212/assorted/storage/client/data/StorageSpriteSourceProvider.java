package com.grim3212.assorted.storage.client.data;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.client.model.StorageModels;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.AtlasIds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.data.SpriteSourceProvider;

import java.util.concurrent.CompletableFuture;

/**
 * {@code SpriteSourceProvider} survived, but it moved from {@code net.minecraftforge.common.data} to
 * {@code net.neoforged.neoforge.client.data} and it is a {@code JsonCodecProvider} now, so it takes
 * a registry lookup future instead of an {@code ExistingFileHelper}; the hook it asks a subclass to
 * fill is {@code JsonCodecProvider#gather} rather than {@code addSources}. The atlas constants moved
 * out of the provider itself into vanilla's {@link AtlasIds}.
 */
public class StorageSpriteSourceProvider extends SpriteSourceProvider {

    public StorageSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Constants.MOD_ID);
    }

    @Override
    protected void gather() {
        for (Identifier tex : StorageModels.CHEST_LOCATIONS.values()) {
            atlas(AtlasIds.CHESTS).addSource(new SingleFile(tex));
        }

        for (Identifier tex : StorageModels.SHULKER_LOCATIONS.values()) {
            atlas(AtlasIds.SHULKER_BOXES).addSource(new SingleFile(tex));
        }
    }

}
