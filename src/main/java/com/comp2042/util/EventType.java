package com.comp2042.util;

/**
 * Enumerates the specific types of gameplay actions available in the system.
 * <p>
 * While some events (like LEFT/RIGHT) trigger direct method calls in the current architecture,
 * this Enum defines the complete set of possible move semantics for future extensibility.
 * </p>
 */
public enum EventType {
    DOWN, LEFT, RIGHT, ROTATE, HOLD
}