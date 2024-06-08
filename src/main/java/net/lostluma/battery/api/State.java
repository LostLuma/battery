package net.lostluma.battery.api;

/**
 * Possible battery states.
 * <p>
 * Unknown can mean that either the controller returned unknown, or another error occurred when retrieving the value.
 */
public enum State {
    UNKNOWN,
    CHARGING,
    DISCHARGING,
    EMPTY,
    FULL,
}
