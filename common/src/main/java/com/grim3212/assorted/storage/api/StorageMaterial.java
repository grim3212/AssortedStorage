package com.grim3212.assorted.storage.api;

import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.storage.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public enum StorageMaterial implements StringRepresentable {
    // Vanilla materials
    // WOOD(() -> ItemTags.PLANKS, new ResourceLocation("block/oak_planks"), 3, 9,
    // () ->
    // Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(2.5F)),
    STONE("stone", () -> LibCommonTags.Items.STONE, new ResourceLocation("block/stone"), 0, 3, 9, 1, 5, () -> Block.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(3.5F).requiresCorrectToolForDrops()),
    COPPER("copper", () -> LibCommonTags.Items.INGOTS_COPPER, new ResourceLocation("block/copper_block"), 1, 4, 9, 1, 6, () -> Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.COPPER).requiresCorrectToolForDrops().strength(3.0F, 6.0F)),
    IRON("iron", () -> LibCommonTags.Items.INGOTS_IRON, new ResourceLocation("block/iron_block"), 1, 5, 9, 1, 8, 5.0F, 6.0F),
    AMETHYST("amethyst", () -> LibCommonTags.Items.GEMS_AMETHYST, new ResourceLocation("block/amethyst_block"), 2, 6, 9, 1, 7, () -> Block.Properties.of().mapColor(MapColor.COLOR_PURPLE).sound(SoundType.AMETHYST).requiresCorrectToolForDrops().strength(1.5F)),
    EMERALD("emerald", () -> LibCommonTags.Items.GEMS_EMERALD, new ResourceLocation("block/emerald_block"), 2, 7, 9, 1, 9, () -> Block.Properties.of().mapColor(MapColor.EMERALD).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)),
    GOLD("gold", () -> LibCommonTags.Items.INGOTS_GOLD, new ResourceLocation("block/gold_block"), 3, 8, 9, 2, 5, () -> Block.Properties.of().mapColor(MapColor.GOLD).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(3.0F, 6.0F)),
    DIAMOND("diamond", () -> LibCommonTags.Items.GEMS_DIAMOND, new ResourceLocation("block/diamond_block"), 4, 9, 11, 3, 5, () -> Block.Properties.of().mapColor(MapColor.DIAMOND).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)),
    NETHERITE("netherite", () -> LibCommonTags.Items.INGOTS_NETHERITE, new ResourceLocation("block/netherite_block"), 5, 9, 14, 3, 9, () -> Block.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().strength(50.0F, 1200.0F)),

    // Assorted Core added materials
    ALUMINUM("aluminum", () -> StorageTags.Items.INGOTS_ALUMINUM, new ResourceLocation(Constants.MOD_ID, "block/particle/aluminum_block"), 1, 4, 9, 1, 6, 5.0F, 6.0F),
    TIN("tin", () -> StorageTags.Items.INGOTS_TIN, new ResourceLocation(Constants.MOD_ID, "block/particle/tin_block"), 1, 4, 10, 1, 6, 5.0F, 6.0F),
    TOPAZ("topaz", () -> StorageTags.Items.GEMS_TOPAZ, new ResourceLocation(Constants.MOD_ID, "block/particle/topaz_block"), 1, 5, 9, 1, 7, () -> Block.Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)),
    PERIDOT("peridot", () -> StorageTags.Items.GEMS_PERIDOT, new ResourceLocation(Constants.MOD_ID, "block/particle/peridot_block"), 2, 6, 9, 1, 7, () -> Block.Properties.of().mapColor(MapColor.COLOR_GREEN).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)),
    LEAD("lead", () -> StorageTags.Items.INGOTS_LEAD, new ResourceLocation(Constants.MOD_ID, "block/particle/lead_block"), 2, 6, 9, 1, 8, 5.0F, 6.0F),
    NICKEL("nickel", () -> StorageTags.Items.INGOTS_NICKEL, new ResourceLocation(Constants.MOD_ID, "block/particle/nickel_block"), 2, 6, 10, 1, 8, 5.0F, 6.0F),
    SAPPHIRE("sapphire", () -> StorageTags.Items.GEMS_SAPPHIRE, new ResourceLocation(Constants.MOD_ID, "block/particle/sapphire_block"), 2, 7, 9, 1, 9, () -> Block.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)),
    BRONZE("bronze", () -> StorageTags.Items.INGOTS_BRONZE, new ResourceLocation(Constants.MOD_ID, "block/particle/bronze_block"), 3, 9, 9, 2, 6, 5.0F, 6.0F),
    RUBY("ruby", () -> StorageTags.Items.GEMS_RUBY, new ResourceLocation(Constants.MOD_ID, "block/particle/ruby_block"), 3, 9, 10, 2, 6, () -> Block.Properties.of().mapColor(MapColor.COLOR_RED).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)),
    INVAR("invar", () -> StorageTags.Items.INGOTS_INVAR, new ResourceLocation(Constants.MOD_ID, "block/particle/invar_block"), 4, 9, 10, 2, 8, 5.0F, 6.0F),
    SILVER("silver", () -> StorageTags.Items.INGOTS_SILVER, new ResourceLocation(Constants.MOD_ID, "block/particle/silver_block"), 3, 9, 11, 2, 7, 5.0F, 6.0F),
    ELECTRUM("electrum", () -> StorageTags.Items.INGOTS_ELECTRUM, new ResourceLocation(Constants.MOD_ID, "block/particle/electrum_block"), 4, 9, 11, 3, 5, 5.0F, 6.0F),
    STEEL("steel", () -> StorageTags.Items.INGOTS_STEEL, new ResourceLocation(Constants.MOD_ID, "block/particle/steel_block"), 4, 9, 12, 3, 6, 5.0F, 6.0F),
    PLATINUM("platinum", () -> StorageTags.Items.INGOTS_PLATINUM, new ResourceLocation(Constants.MOD_ID, "block/particle/platinum_block"), 5, 9, 13, 3, 8, 5.0F, 6.0F);

    private final String name;
    private final Supplier<TagKey<Item>> material;
    private final ResourceLocation particle;
    private final Supplier<Block.Properties> props;
    /**
     * The level at which this storage item is relative to other storage materials
     * <p>
     * wood/stone being the worst, netherite being the best
     */
    private final int storageLevel;
    private final int xRows;
    private final int yCols;

    private final int hopperXRows;
    private final int hopperYCols;

    private StorageMaterial(String name, Supplier<TagKey<Item>> material, ResourceLocation particle, int storageLevel, int xRows, int yCols, int hopperXRows, int hopperYCols, float destroyTime, float explosionResistance) {
        this(name, material, particle, storageLevel, xRows, yCols, hopperXRows, hopperYCols, () -> Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(destroyTime, explosionResistance));
    }

    private StorageMaterial(String name, Supplier<TagKey<Item>> material, ResourceLocation particle, int storageLevel, int xRows, int yCols, int hopperXRows, int hopperYCols, Supplier<Block.Properties> props) {
        this.name = name;
        this.material = material;
        this.particle = particle;
        this.storageLevel = storageLevel;
        this.xRows = xRows;
        this.yCols = yCols;
        this.hopperXRows = hopperXRows;
        this.hopperYCols = hopperYCols;
        this.props = props;
    }

    public ResourceLocation getParticle() {
        return particle;
    }

    public int getStorageLevel() {
        return storageLevel;
    }

    public int getXRows() {
        return xRows;
    }

    public int getYCols() {
        return yCols;
    }

    public int totalItems() {
        return this.xRows * this.yCols;
    }

    public int hopperXRows() {
        return this.hopperXRows;
    }

    public int hopperYCols() {
        return this.hopperYCols;
    }

    public int hopperSize() {
        return this.hopperXRows * this.hopperYCols;
    }

    public int hopperCooldown() {
        return 8 - storageLevel;
    }

    public TagKey<Item> getMaterial() {
        return material.get();
    }

    public Block.Properties getProps() {
        return props.get();
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static void write(FriendlyByteBuf byteBuf, StorageMaterial material) {
        if (material == null) {
            byteBuf.writeVarInt(-1);
        } else {
            byteBuf.writeEnum(material);
        }
    }

    public static StorageMaterial read(FriendlyByteBuf byteBuf) {
        int matIdx = byteBuf.readVarInt();
        if (matIdx == -1) {
            return null;
        } else {
            return StorageMaterial.values()[matIdx];
        }
    }
}
