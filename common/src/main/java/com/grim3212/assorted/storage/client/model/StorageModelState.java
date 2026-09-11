package com.grim3212.assorted.storage.client.model;

/**
 * Per-submission state for the storage entity models. A model is set up later, once per submission,
 * so anything that varies per block has to travel here.
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
