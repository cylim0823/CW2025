package com.comp2042.model;

import com.comp2042.util.EventSource;
import com.comp2042.util.EventType;

public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    public EventSource getEventSource() {
        return eventSource;
    }
}
