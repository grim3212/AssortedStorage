package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.lib.client.util.RenderHelper;
import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.api.StreamHelper;
import com.grim3212.assorted.storage.client.StorageClient;
import com.grim3212.assorted.storage.client.blockentity.crateupgrades.AmountUpgradeRenderer;
import com.grim3212.assorted.storage.client.blockentity.crateupgrades.PadlockUpgradeRenderer;
import com.grim3212.assorted.storage.client.blockentity.crateupgrades.VoidUpgradeRenderer;
import com.grim3212.assorted.storage.client.blockentity.state.CrateRenderState;
import com.grim3212.assorted.storage.client.util.ClientResources;
import com.grim3212.assorted.storage.common.block.CrateBlock;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.inventory.crates.CrateSidedInv;
import com.grim3212.assorted.storage.common.item.PadlockItem;
import com.grim3212.assorted.storage.common.item.upgrades.AmountUpgradeItem;
import com.grim3212.assorted.storage.common.item.upgrades.VoidUpgradeItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CrateBlockEntityRenderer implements BlockEntityRenderer<CrateBlockEntity, CrateRenderState> {

    private final ItemModelResolver itemModelResolver;
    private final int viewDistance;

    public static final RenderType ICONS = RenderTypes.text(ClientResources.CRATE_ICONS_LOCATION);

    public CrateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.viewDistance = StorageClient.CLIENT_CONFIG.crateMaxRenderDistance.get();
    }

    @Override
    public int getViewDistance() {
        return this.viewDistance;
    }

    @Override
    public CrateRenderState createRenderState() {
        return new CrateRenderState();
    }

    @Override
    public void extractRenderState(CrateBlockEntity crate, CrateRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(crate, state, partialTicks, cameraPosition, breakProgress);

        CrateSidedInv inventory = crate.getItemStackStorageHandler();
        int slots = crate.getLayout().getNumStacks();

        state.facing = crate.getBlockState().getValue(CrateBlock.FACING);
        state.layout = crate.getLayout();
        state.itemLightCoords = inventory.hasGlowUpgrade() ? LightCoordsUtil.FULL_BRIGHT : state.lightCoords;
        state.anySlotLocked = inventory.anySlotsLocked();
        state.blockEntity = crate;

        int seed = (int) crate.getBlockPos().asLong();
        List<ItemStackRenderState> items = new ArrayList<>(slots);
        state.itemRotations = new float[slots];
        state.slotLocked = new boolean[slots];

        for (int slot = 0; slot < slots; slot++) {
            LargeItemStack largeStack = inventory.getLargeItemStack(slot);
            ItemStackRenderState itemState = new ItemStackRenderState();
            this.itemModelResolver.updateForTopItem(itemState, largeStack.getStack(), ItemDisplayContext.GUI, crate.getLevel(), null, seed + slot);
            items.add(itemState);
            state.itemRotations[slot] = largeStack.getRotation();
            state.slotLocked[slot] = inventory.isSlotLocked(slot);
        }
        state.items = items;

        // Unique upgrades only
        state.upgrades = inventory.getEnhancements().stream().filter(StreamHelper.distinctByKey(ItemStack::getItem)).toList();
    }

    @Override
    public void submit(CrateRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        submitItems(state, poseStack, submitNodeCollector);
        submitUpgrades(state, poseStack, submitNodeCollector);
        submitSlotLocks(state, poseStack, submitNodeCollector);
    }

    private void submitItems(CrateRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        poseStack.pushPose();
        faceCrate(state.facing, poseStack);
        poseStack.translate(0.0D, 0.0D, 0.449D);

        poseStack.scale(0.5f, 0.5f, 0.5f);
        // Flatten the item so it sits against the crate face rather than sticking out of it.
        poseStack.scale(1.0F, 1.0F, 0.001F);

        switch (state.layout) {
            case SINGLE:
                submitItemInSlot(state, 0, poseStack, submitNodeCollector, 0.0D, 0.0D);
                break;
            case DOUBLE:
                poseStack.scale(0.5f, 0.5f, 0.5f);
                submitItemInSlot(state, 0, poseStack, submitNodeCollector, 0.0D, 0.875D);
                submitItemInSlot(state, 1, poseStack, submitNodeCollector, 0.0D, -0.875D);
                break;
            case TRIPLE:
                poseStack.scale(0.5f, 0.5f, 0.5f);
                submitItemInSlot(state, 0, poseStack, submitNodeCollector, 0.0D, 0.875D);
                submitItemInSlot(state, 1, poseStack, submitNodeCollector, -0.875D, -0.875D);
                submitItemInSlot(state, 2, poseStack, submitNodeCollector, 0.875D, -0.875D);
                break;
            case QUADRUPLE:
                poseStack.scale(0.5f, 0.5f, 0.5f);
                submitItemInSlot(state, 0, poseStack, submitNodeCollector, -0.875D, 0.875D);
                submitItemInSlot(state, 1, poseStack, submitNodeCollector, 0.875D, 0.875D);
                submitItemInSlot(state, 2, poseStack, submitNodeCollector, -0.875D, -0.875D);
                submitItemInSlot(state, 3, poseStack, submitNodeCollector, 0.875D, -0.875D);
                break;
        }

        poseStack.popPose();
    }

    private void submitItemInSlot(CrateRenderState state, int slot, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, double x, double y) {
        ItemStackRenderState itemState = state.items.get(slot);
        if (itemState.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(x, y, 0.0D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-state.itemRotations[slot] * 360.0F / 16.0F));
        itemState.submit(poseStack, submitNodeCollector, state.itemLightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    private void submitUpgrades(CrateRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        CrateBlockEntity crate = state.blockEntity;
        if (crate == null) {
            return;
        }

        poseStack.pushPose();
        for (ItemStack stack : state.upgrades) {
            if (stack.getItem() instanceof PadlockItem) {
                PadlockUpgradeRenderer.INSTANCE.render(crate, stack, 0.0F, poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY);
            } else if (stack.getItem() instanceof VoidUpgradeItem) {
                VoidUpgradeRenderer.INSTANCE.render(crate, stack, 0.0F, poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY);
            } else if (stack.getItem() instanceof AmountUpgradeItem) {
                AmountUpgradeRenderer.INSTANCE.render(crate, stack, 0.0F, poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY);
            }
        }
        poseStack.popPose();
    }

    private void submitSlotLocks(CrateRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        if (!state.anySlotLocked) {
            return;
        }

        poseStack.pushPose();
        faceCrate(state.facing, poseStack);
        poseStack.translate(-0.5D, 0.5D, 0.45D);
        float scale = 0.012F * 0.6666667F;
        poseStack.scale(scale, -scale, scale);

        switch (state.layout) {
            case SINGLE:
                submitLockIcons(poseStack, submitNodeCollector, state.lightCoords, state.slotLocked, new int[]{102}, new int[]{18});
                break;
            case DOUBLE:
                submitLockIcons(poseStack, submitNodeCollector, state.lightCoords, state.slotLocked, new int[]{102, 102}, new int[]{18, 72});
                break;
            case TRIPLE:
                submitLockIcons(poseStack, submitNodeCollector, state.lightCoords, state.slotLocked, new int[]{102, 47, 102}, new int[]{18, 72, 72});
                break;
            case QUADRUPLE:
                submitLockIcons(poseStack, submitNodeCollector, state.lightCoords, state.slotLocked, new int[]{47, 102, 47, 102}, new int[]{18, 18, 72, 72});
                break;
        }

        poseStack.popPose();
    }

    /**
     * The lock icons are one textured quad each, which is exactly what
     * {@code SubmitNodeCollector#submitCustomGeometry} - the one remaining escape hatch to a raw
     * {@code VertexConsumer} - is for. The whole crate goes in one callback, and the pose it hands
     * back has to be copied into a local stack because the caller's stack is long gone by the time
     * the geometry is actually built.
     */
    private static void submitLockIcons(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, boolean[] locked, int[] xs, int[] ys) {
        submitNodeCollector.submitCustomGeometry(poseStack, ICONS, (pose, buffer) -> {
            PoseStack local = new PoseStack();
            local.last().set(pose);
            for (int slot = 0; slot < xs.length; slot++) {
                if (slot < locked.length && locked[slot]) {
                    RenderHelper.lightedBlit(buffer, local, xs[slot], ys[slot], 0, 2, 0, 5, 5, 8, 8, packedLight);
                }
            }
        });
    }

    /**
     * Rotates into the crate's face. Shared by the item, upgrade and lock passes, which all repeated
     * it inline.
     */
    public static void faceCrate(Direction facing, PoseStack poseStack) {
        poseStack.translate(0.5D, 0.5D, 0.5D);
        if (facing.getAxis().isHorizontal()) {
            poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(facing == Direction.DOWN ? 90F : 270F));
        }
    }
}
