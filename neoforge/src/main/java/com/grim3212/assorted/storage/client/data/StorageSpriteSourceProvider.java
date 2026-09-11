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

/** Adds this mod's textures to the vanilla atlases they are drawn from. */
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
