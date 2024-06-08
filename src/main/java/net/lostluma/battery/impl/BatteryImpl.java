package net.lostluma.battery.impl;

import net.lostluma.battery.api.Battery;
import net.lostluma.battery.api.State;
import net.lostluma.battery.api.Technology;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;

public final class BatteryImpl implements Battery {
    final long ptr;
    private final ManagerImpl manager;

    private final Technology technology;
    private final @Nullable String vendor;
    private final @Nullable String model;
    private final @Nullable String serialNumber;

    private float stateOfCharge;
    private float energy;
    private float energyFull;
    private float energyFullDesign;
    private float energyRate;
    private float voltage;
    private float stateOfHealth;
    private State state;
    private float temperature = Float.NaN;
    private long cycleCount = Long.MIN_VALUE;
    private @Nullable Duration timeToFull;
    private @Nullable Duration timeToEmpty;

    // Constructor for native library
    private BatteryImpl(long ptr, ManagerImpl manager, Technology technology, @Nullable String vendor, @Nullable String model, @Nullable String serialNumber) {
        this.ptr = ptr;
        this.manager = manager;

        this.technology = technology;
        this.vendor = vendor;
        this.model = model;
        this.serialNumber = serialNumber;
    }

    @Override
    public void update() throws IOException, RuntimeException {
        if (this.manager.isActive()) {
            this.update0();
        } else {
            throw new RuntimeException("Attached manager is closed.");
        }
    }

    private native void update0() throws IOException;

    // Allows native library to update all fields at once
    private void update0(float stateOfCharge, float energy, float energyFull, float energyRate, float energyFullDesign, float voltage, float stateOfHealth, State state, float temperature, long cycleCount, float timeToFull, float timeToEmpty) {
        this.stateOfCharge = stateOfCharge;
        this.energy = energy;
        this.energyFull = energyFull;
        this.energyRate = energyRate;
        this.energyFullDesign = energyFullDesign;
        this.voltage = voltage;
        this.stateOfHealth = stateOfHealth;
        this.state = state;
        this.temperature = temperature;
        this.cycleCount = cycleCount;

        if (Float.isNaN(timeToFull)) {
            this.timeToFull = null;
        } else {
            this.timeToFull = Duration.of((long) timeToFull, ChronoUnit.SECONDS);
        }

        if (Float.isNaN(timeToEmpty)) {
            this.timeToEmpty = null;
        } else {
            this.timeToEmpty = Duration.of((long) timeToEmpty, ChronoUnit.SECONDS);
        }
    }

    @Override
    public float stateOfCharge() {
        return this.stateOfCharge;
    }

    @Override
    public float energy() {
        return this.energy;
    }

    @Override
    public float energyFull() {
        return this.energyFull;
    }

    @Override
    public float energyFullDesign() {
        return this.energyFullDesign;
    }

    @Override
    public float energyRate() {
        return this.energyRate;
    }

    @Override
    public float voltage() {
        return this.voltage;
    }

    @Override
    public float stateOfHealth() {
        return this.stateOfHealth;
    }

    @Override
    public @NotNull State state() {
        return this.state;
    }

    @Override
    public @NotNull Technology technology() {
        return this.technology;
    }

    @Override
    public @NotNull Optional<Float> temperature() {
        float value = this.temperature;

        if (Float.isNaN(value)) {
            return Optional.empty();
        } else {
            return Optional.of(value);
        }
    }

    @Override
    public @NotNull OptionalLong cycleCount() {
        long value = this.cycleCount;

        if (value == Integer.MIN_VALUE) {
            return OptionalLong.empty();
        } else {
            return OptionalLong.of(value);
        }
    }

    @Override
    public @NotNull Optional<String> vendor() {
        if (Objects.isNull(this.vendor)) {
            return Optional.empty();
        } else {
            return Optional.of(this.vendor);
        }
    }

    @Override
    public @NotNull Optional<String> model() {
        if (Objects.isNull(this.model)) {
            return Optional.empty();
        } else {
            return Optional.of(this.model);
        }
    }

    @Override
    public @NotNull Optional<String> serialNumber() {
        if (Objects.isNull(this.serialNumber)) {
            return Optional.empty();
        } else {
            return Optional.of(this.serialNumber);
        }
    }

    @Override
    public @NotNull Optional<Duration> timeToFull() {
        Duration value = this.timeToFull;

        if (Objects.isNull(value)) {
            return Optional.empty();
        } else {
            return Optional.of(value);
        }
    }

    @Override
    public @NotNull Optional<Duration> timeToEmpty() {
        Duration value = this.timeToEmpty;

        if (Objects.isNull(value)) {
            return Optional.empty();
        } else {
            return Optional.of(value);
        }
    }
}
