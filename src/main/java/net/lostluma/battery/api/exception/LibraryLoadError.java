package net.lostluma.battery.api.exception;

import org.jetbrains.annotations.ApiStatus;

/**
 * Error thrown when the native backend library could not be loaded.
 */
public class LibraryLoadError extends Error {
    @ApiStatus.Internal
    public LibraryLoadError(String message) {
        super(message);
    }

    @ApiStatus.Internal
    public LibraryLoadError(Throwable exception) {
        super(exception);
    }
}
