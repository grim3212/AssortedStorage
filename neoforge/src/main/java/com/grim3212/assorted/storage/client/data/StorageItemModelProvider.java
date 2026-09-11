package com.grim3212.assorted.storage.client.data;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.client.color.BagTintSource;
import com.grim3212.assorted.storage.client.properties.HasStorageTagProperty;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.item.upgrades.LevelUpgradeItem;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.renderer.item.ItemModel;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

/**
 * Item models for everything but block items, which {@link StorageBlockstateProvider} models. The
 * bags are {@code minecraft:condition} trees over {@link HasStorageTagProperty}, with the dye as a
 * tint; the shulker box item is special-rendered and needs nothing here.
 */
public class StorageItemModelProvider extends ModelProvider {

    public StorageItemModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Storage item models";
    }

    /**
     * This provider owns no blocks - {@link StorageBlockstateProvider} does - so there is nothing
     * for {@link BlockModelGenerators} to do and nothing for the blockstate validation to miss.
     */
    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> !(holder.value() instanceof BlockItem));
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        handheldItem(itemModels, StorageItems.ROTATOR_MAJIG.get());

        generatedItem(itemModels, StorageItems.LOCKSMITH_KEY.get());
        generatedItem(itemModels, StorageItems.LOCKSMITH_LOCK.get());
        generatedItem(itemModels, StorageItems.KEY_RING.get());
        enderBag(itemModels, StorageItems.ENDER_BAG.get());

        bag(itemModels, StorageItems.BAG.get());
        for (IRegistryObject<BagItem> bag : StorageItems.BAGS.values()) {
            materialBag(itemModels, bag.get());
        }

        generatedItem(itemModels, StorageItems.BLANK_UPGRADE.get());
        generatedItem(itemModels, StorageItems.VOID_UPGRADE.get());
        generatedItem(itemModels, StorageItems.REDSTONE_UPGRADE.get());
        generatedItem(itemModels, StorageItems.AMOUNT_UPGRADE.get());
        generatedItem(itemModels, StorageItems.GLOW_UPGRADE.get());
        for (IRegistryObject<LevelUpgradeItem> levelUpgrade : StorageItems.LEVEL_UPGRADES.values()) {
            generatedItem(itemModels, levelUpgrade.get());
        }
    }

    /** The ender bag carries no dye, only a padlock, so it is a single condition rather than a tree. */
    private void enderBag(ItemModelGenerators itemModels, Item item) {
        Identifier plain = ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item),
                TextureMapping.layer0(itemTexture("ender_bag")), itemModels.modelOutput);
        Identifier locked = ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item).withSuffix("_locked"),
                TextureMapping.layer0(itemTexture("ender_bag_locked")), itemModels.modelOutput);

        itemModels.itemModelOutput.accept(item, ItemModelUtils.conditional(HasStorageTagProperty.LOCKED,
                ItemModelUtils.plainModel(locked), ItemModelUtils.plainModel(plain)));
    }

    /** The plain bag: its own body plus the shared strap. */
    private void bag(ItemModelGenerators itemModels, Item item) {
        bagTree(itemModels, item,
                variant(itemModels, item, "", ModelTemplates.TWO_LAYERED_ITEM, "bag", "bag_strap", null),
                variant(itemModels, item, "_colored", ModelTemplates.TWO_LAYERED_ITEM, "bag_colored", "bag_strap", null),
                variant(itemModels, item, "_locked", ModelTemplates.THREE_LAYERED_ITEM, "bag_lock_cutout", "bag_strap_lock_cutout", null),
                variant(itemModels, item, "_locked_colored", ModelTemplates.THREE_LAYERED_ITEM, "bag_colored_lock_cutout", "bag_strap_lock_cutout", null));
    }

    /** A material bag: the shared body and strap with the material's own colour layer on top. */
    private void materialBag(ItemModelGenerators itemModels, BagItem item) {
        String material = name(item);
        bagTree(itemModels, item,
                variant(itemModels, item, "", ModelTemplates.THREE_LAYERED_ITEM, "bag_material", "bag_strap", material),
                variant(itemModels, item, "_colored", ModelTemplates.THREE_LAYERED_ITEM, "bag_material_colored", "bag_strap", material),
                variant(itemModels, item, "_locked", FOUR_LAYERED_ITEM, "bag_material_lock_cutout", "bag_strap_lock_cutout", material),
                variant(itemModels, item, "_locked_colored", FOUR_LAYERED_ITEM, "bag_material_colored_lock_cutout", "bag_strap_lock_cutout", material));
    }

    /**
     * Picks one of the four bag models from the stack. The lock picks the texture set; {@code dyed}
     * picks the greyscale body that takes the tint, because tinting the plain body comes out muddy.
     */
    private void bagTree(ItemModelGenerators itemModels, Item item, ItemModel.Unbaked plain, ItemModel.Unbaked colored, ItemModel.Unbaked locked, ItemModel.Unbaked lockedColored) {
        itemModels.itemModelOutput.accept(item, ItemModelUtils.conditional(HasStorageTagProperty.LOCKED,
                ItemModelUtils.conditional(HasStorageTagProperty.DYED, lockedColored, locked),
                ItemModelUtils.conditional(HasStorageTagProperty.DYED, colored, plain)));
    }

    /**
     * One bag variant. {@code tints} is positional - entry N tints the layer with {@code tintindex} N -
     * so both sources always sit on the body and the strap, and the material and padlock layers above
     * them are left alone, the way vanilla's leather armour leaves its overlay untinted.
     */
    private ItemModel.Unbaked variant(ItemModelGenerators itemModels, Item item, String suffix, ModelTemplate template, String body, String strap, @Nullable String material) {
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.LAYER0, itemTexture(body))
                .put(TextureSlot.LAYER1, itemTexture(strap));
        if (material != null) {
            textures.put(TextureSlot.LAYER2, itemTexture(material));
            if (template == FOUR_LAYERED_ITEM) {
                textures.put(LAYER3, itemTexture("bag_lock"));
            }
        } else if (template == ModelTemplates.THREE_LAYERED_ITEM) {
            textures.put(TextureSlot.LAYER2, itemTexture("bag_lock"));
        }

        Identifier model = template.create(ModelLocationUtils.getModelLocation(item).withSuffix(suffix), textures, itemModels.modelOutput);
        return ItemModelUtils.tintedModel(model,
                new BagTintSource(BagItem.TAG_PRIMARY_COLOR),
                new BagTintSource(BagItem.TAG_SECONDARY_COLOR));
    }

    // item/generated bakes layer0..layer4 (ItemModelGenerator.LAYERS) but vanilla only names slots up
    // to LAYER2; a locked material bag needs body, strap, material and padlock.
    private static final TextureSlot LAYER3 = TextureSlot.create("layer3");
    private static final ModelTemplate FOUR_LAYERED_ITEM = ExtendedModelTemplateBuilder.builder()
            .parent(Identifier.withDefaultNamespace("item/generated"))
            .requiredTextureSlot(TextureSlot.LAYER0)
            .requiredTextureSlot(TextureSlot.LAYER1)
            .requiredTextureSlot(TextureSlot.LAYER2)
            .requiredTextureSlot(LAYER3)
            .build();

    private void generatedItem(ItemModelGenerators itemModels, Item item) {
        itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
    }

    private void handheldItem(ItemModelGenerators itemModels, Item item) {
        itemModels.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    private static String name(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    private static Material itemTexture(String name) {
        return new Material(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "item/" + name));
    }
}
