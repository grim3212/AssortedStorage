package com.grim3212.assorted.storage.client.model;

import com.mojang.serialization.Codec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.StringRepresentable;

import java.util.function.Function;

/**
 * Names the storage entity models that a special item renderer can pick from.
 * <p>
 * Item rendering is data driven in 26.2 - an item model json names a {@code minecraft:special}
 * renderer type and its options - so the model to use has to be expressible in a codec rather than
 * being wired up in code by a {@code BlockEntityWithoutLevelRenderer} registration.
 */
public enum StorageModelType implements StringRepresentable {
    CABINET("cabinet", StorageModelLayers.CABINET, CabinetModel::new),
    GLASS_CABINET("glass_cabinet", StorageModelLayers.GLASS_CABINET, CabinetModel::new),
    SAFE("safe", StorageModelLayers.SAFE, SafeModel::new),
    LOCKER("locker", StorageModelLayers.LOCKER, LockerModel::new),
    DUAL_LOCKER("dual_locker", StorageModelLayers.DUAL_LOCKER, DualLockerModel::new),
    WAREHOUSE_CRATE("warehouse_crate", StorageModelLayers.WAREHOUSE_CRATE, WarehouseCrateModel::new),
    CHEST("chest", StorageModelLayers.LOCKED_CHEST, ChestModel::new);

    public static final Codec<StorageModelType> CODEC = StringRepresentable.fromEnum(StorageModelType::values);

    private final String name;
    private final ModelLayerLocation layer;
    private final Function<ModelPart, BaseStorageModel> factory;

    StorageModelType(String name, ModelLayerLocation layer, Function<ModelPart, BaseStorageModel> factory) {
        this.name = name;
        this.layer = layer;
        this.factory = factory;
    }

    public BaseStorageModel bake(EntityModelSet models) {
        return this.factory.apply(models.bakeLayer(this.layer));
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
