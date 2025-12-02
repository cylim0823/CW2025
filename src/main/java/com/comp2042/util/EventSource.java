package com.comp2042.util;

/**
 * Identifies the origin of a game input event.
 * <p>
 * This distinction is critical for scoring:
 * <ul>
 * <li>{@link #USER}: Action initiated by the player (e.g., pressing Down). Usually awards points.</li>
 * <li>{@link #THREAD}: Action initiated by the game loop (Gravity). Usually awards no points.</li>
 * </ul>
 * </p>
 */
public enum EventSource {
    USER, THREAD
}