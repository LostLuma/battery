package net.lostluma.battery.api;

import net.lostluma.battery.api.exception.LibraryLoadError;
import net.lostluma.battery.impl.ManagerImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

/**
 * The battery manager.
 * <p>
 * Allows fetching and refreshing battery information.
 */
public interface Manager extends AutoCloseable {
    /**
     * Tries to create a new manager.
     *
     * @return the new manager.
     * @throws IOException creating the manager failed.
     * @throws LibraryLoadError loading the native backend library failed.
     */
    static @NotNull Manager create() throws IOException, LibraryLoadError {
        return new ManagerImpl();
    }

    /**
     * Returns a collection of currently available batteries.
     * <p>
     * Note that ordering of batteries is not guaranteed, and may
     * change on subsequent calls due to the underlying OS implementation.
     *
     * @return the system's current batteries.
     * @throws IOException looking up the batteries failed.
     * @throws RuntimeException the manager is already closed.
     */
    @NotNull Collection<Battery> batteries() throws IOException, RuntimeException;

    /**
     * Close the manager.
     * <p>
     * Frees up all internal system resources created during its usage.
     */
    @Override
    void close(); // Remove throws Exception from AutoCloseable
}
