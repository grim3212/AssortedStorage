package com.grim3212.assorted.storage.client.data;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.item.upgrades.LevelUpgradeItem;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
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
 * Forge's {@code ItemModelProvider} / {@code ItemModelBuilder} / {@code ExistingFileHelper} are all
 * gone, and so is the idea that an item model is one json: an item points at a data-driven
 * {@code ItemModel} in {@code assets/<ns>/items/}, which names the {@code assets/<ns>/models/}
 * geometry to draw. {@link ItemModelGenerators} writes both halves.
 * <p>
 * Block items are not listed here at all - they belong to {@link StorageBlockstateProvider}, which
 * owns the special-renderer items, the hoppers and the crate controller and lets
 * {@link ModelProvider} point the rest at their block model.
 * <p>
 * <b>The bag, ender bag and shulker box item variants are gone.</b> In 1.20.1 each bag model carried
 * {@code overrides} keyed on two {@code ItemProperties} functions -
 * {@code assortedstorage:color} and {@code assortedstorage:locked} - which swapped in a dyed or
 * padlocked model per stack. {@code ItemOverrides} was deleted in 26.2 and
 * {@code registerItemProperty} has no replacement: a model is chosen before baking, from a
 * {@code minecraft:select} / {@code minecraft:condition} tree over a codec-registered property.
 * <p>
 * TODO(26.2): neither state can be expressed with a vanilla property. Both live inside
 *  {@code DataComponents.CUSTOM_DATA} - the lock under {@code Storage_Lock}, the dye under the
 *  {@code BagItem} colour tags - and the only vanilla conditional that reads a component predicate,
 *  {@code minecraft:component_matches}, needs an exact {@code NbtPredicate} value, which an
 *  arbitrary combination code cannot supply. Restoring the padlock overlay and the dyed body needs a
 *  custom {@code ConditionalItemModelProperty} registered on <em>both</em> loaders (the item json is
 *  shared), which AssortedLib does not currently expose. Until then a bag renders as its plain,
 *  undyed, unlocked model whatever is on the stack. The dyed and padlocked textures are still
 *  shipped, so nothing is lost but the wiring.
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
        generatedItem(itemModels, StorageItems.ENDER_BAG.get());

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

    /** The plain bag: its own body plus the shared strap. */
    private void bag(ItemModelGenerators itemModels, Item item) {
        layered(itemModels, item, ModelTemplates.TWO_LAYERED_ITEM, new TextureMapping()
                .put(TextureSlot.LAYER0, itemTexture(name(item)))
                .put(TextureSlot.LAYER1, itemTexture("bag_strap")));
    }

    /** A material bag: the shared body and strap with the material's own colour layer on top. */
    private void materialBag(ItemModelGenerators itemModels, BagItem item) {
        layered(itemModels, item, ModelTemplates.THREE_LAYERED_ITEM, new TextureMapping()
                .put(TextureSlot.LAYER0, itemTexture("bag_material"))
                .put(TextureSlot.LAYER1, itemTexture("bag_strap"))
                .put(TextureSlot.LAYER2, itemTexture(name(item))));
    }

    private void layered(ItemModelGenerators itemModels, Item item, ModelTemplate template, TextureMapping textures) {
        Identifier model = template.create(ModelLocationUtils.getModelLocation(item), textures, itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
    }

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
