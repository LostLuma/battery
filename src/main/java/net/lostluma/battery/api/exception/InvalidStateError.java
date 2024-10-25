package net.lostluma.battery.api.exception;

import org.jetbrains.annotations.ApiStatus;

/**
 * Error thrown when calling a method on a closed object.
 */
// TODO (breaking): Subclass Error instead
public class InvalidStateError extends RuntimeException {
    @ApiStatus.Internal
    public InvalidStateError(String message) {
        super(message);
    }
}
