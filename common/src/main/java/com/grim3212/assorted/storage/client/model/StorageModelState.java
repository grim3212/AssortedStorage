package com.grim3212.assorted.storage.client.model;

/**
 * Per-submission state for the storage entity models.
 * <p>
 * In 26.2 a model is not drawn where it is submitted: {@code SubmitNodeCollector#submitModel} records
 * the model together with a state object and {@code ModelFeatureRenderer} calls
 * {@link net.minecraft.client.model.Model#setupAnim} on it later, once per submission. Anything that
 * used to be a mutable field on the shared model instance therefore has to travel in this state, or
 * every storage block in view would draw with the last one's door angle.
 *
 * @param doorAngle    The door/lid opening angle, in degrees.
 * @param renderHandle Whether to draw the handle ({@code true}) or the padlock ({@code false}).
 */
public record StorageModelState(float doorAngle, boolean renderHandle) {

    /** A closed, unlocked model - what the item renderers draw. */
    public static final StorageModelState CLOSED_UNLOCKED = new StorageModelState(0.0F, true);

    /** A closed, locked model - for the storage blocks that are always locked. */
    public static final StorageModelState CLOSED_LOCKED = new StorageModelState(0.0F, false);
}
