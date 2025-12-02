package com.comp2042.model;

import com.comp2042.util.EventSource;
import com.comp2042.util.EventType;

/**
 * An immutable data transfer object (DTO) representing a specific game movement event.
 * <p>
 * This class packages the type of movement and, crucially, the <b>source</b> of the event.
 * It allows the {@link com.comp2042.controllers.GameController} to distinguish between:
 * <ul>
 * <li><b>User Input:</b> The player pressing the 'Down' key (Soft Drop).</li>
 * <li><b>System Event:</b> The game loop timer ticking (Gravity).</li>
 * </ul>
 * </p>
 * <p>
 * This distinction is essential for the scoring system, as players are awarded points
 * for manually dropping bricks, but not for natural gravity falls.
 * </p>
 */
public final class MoveEvent {

    private final EventType eventType;
    private final EventSource eventSource;

    /**
     * Constructs a new MoveEvent.
     *
     * @param eventType the specific action type (e.g., DOWN, LEFT, RIGHT).
     * @param eventSource the origin of the event (USER or THREAD).
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /**
     * Retrieves the origin of this event.
     * <p>
     * Used by the controller to determine if scoring logic should be applied.
     * </p>
     *
     * @return {@link EventSource#USER} if triggered by keyboard, or {@link EventSource#THREAD} if by the game loop.
     */
    public EventSource getEventSource() {
        return eventSource;
    }
}