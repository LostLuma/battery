package net.lostluma.battery.impl;

import net.lostluma.battery.api.Battery;
import net.lostluma.battery.api.Manager;
import net.lostluma.battery.api.exception.LibraryLoadError;
import net.lostluma.battery.impl.util.LibraryUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

public final class ManagerImpl implements Manager {
    private final long ptr;
    private boolean active;

    // I didn't want Battery to implement AutoCloseable
    // So instead we free its memory using this here :)
    private final ReferenceQueue<Battery> queue;
    private final Map<PhantomReference<?>, Long> references;

    public ManagerImpl() throws IOException, LibraryLoadError {
        LibraryUtil.load();

        this.ptr = create();
        this.active = true;

        this.queue = new ReferenceQueue<>();
        this.references = new IdentityHashMap<>();
    }

    boolean isActive() {
        return this.active;
    }

    @Override
    public @NotNull Collection<Battery> batteries() throws IOException, RuntimeException {
        if (this.isActive()) {
            BatteryImpl[] batteries = this.batteries0();

            Reference<?> ref;

            while ((ref = this.queue.poll()) != null) {
                this.dropBattery(references.remove(ref));
            }

            for (BatteryImpl battery : batteries) {
                this.references.put(new PhantomReference<>(battery, this.queue), battery.ptr);
            }

            return Arrays.asList(batteries);
        } else {
            throw new RuntimeException("Manager can not be used after being closed!");
        }
    }

    @Override
    public void close() {
        if (!this.active) {
            return;
        }

        this.active = false;
        this.drop(this.ptr);

        for (Map.Entry<PhantomReference<?>, Long> entry : this.references.entrySet()) {
            this.dropBattery(entry.getValue());
        }
    }

    private static native long create() throws IOException;
    private native BatteryImpl[] batteries0() throws IOException;

    private native void drop(long ptr);
    private native void dropBattery(long ptr);
}
