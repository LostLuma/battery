package net.lostluma.battery.api.exception;

/**
 * Error thrown when the native backend library could not be loaded.
 */
public class LibraryLoadError extends Error {
    public LibraryLoadError(String message) {
        super(message);
    }

    public LibraryLoadError(Throwable exception) {
        super(exception);
    }
}
