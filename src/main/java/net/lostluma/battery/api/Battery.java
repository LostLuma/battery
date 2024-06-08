package net.lostluma.battery.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.OptionalLong;

/**
 * Represents battery information at a point in time.
 * <p>
 * Subsequent calls to the same method will return the same information.
 * You must instead use the {@link Battery#update()} method to update information held in the battery.
 * <p>
 * The documentation here is derived from the documentation for the underlying library, provided for convenience.
 * You may read it <a href="https://docs.rs/starship-battery/0.8.3/starship_battery/index.html">on docs.rs</a> for more details.
 */
public interface Battery {
    /**
     * The current state of charge.
     * <p>
     * The State of Charge (or SOC) is an expression of battery capacity as a percentage of maximum capacity.
     * <p>
     * In plain english: it is how much energy your battery has (expressed in percent). This is the value which operating systems and desktop managers display in the taskbar.
     * <p>
     * This can roughly be calculated as {@code battery.energy() / battery.energyFull()},
     * but you should always use {@code battery.stateOfCharge()} instead of the manual calculation
     * because many device drivers provide this value more precisely, which this method takes into account.
     *
     * @return the battery's state of charge.
     */
    float stateOfCharge();

    /**
     * @return the amount of energy currently available in the battery, in watt-hours.
     */
    float energy();

    /**
     * @return the amount of energy in the battery when it’s considered full, in watt-hours.
     */
    float energyFull();

    /**
     * @return the amount of energy the battery is designed to hold when it’s considered full, in watt-hours.
     */
    float energyFullDesign();

    /**
     * @return the amount of energy currently being drained from the battery, in watts.
     */
    float energyRate();

    /**
     * @return the battery's voltage, in volts.
     */
    float voltage();

    /**
     * The current state of health.
     * <p>
     * The State of Health (or SOH) is an indicator of the point that has been reached in the battery's life cycle and a measure of its condition.
     * <p>
     * In plain english: it is how much energy in percent your battery can hold when fully charged. New battery - 100 %, old and degraded battery - a notably lower percentage.
     *
     * @return the battery's state of health.
     */
    float stateOfHealth();

    /**
     * @return the battery's state.
     */
    @NotNull State state();

    /**
     * @return the battery's technology.
     */
    @NotNull Technology technology();

    /**
     * @return the battery's temperature in Celsius, if available.
     */
    @NotNull Optional<Float> temperature();

    /**
     * Total number of charge / discharge cycles.
     *
     * @return the battery's cycle count, if available.
     */
    @NotNull OptionalLong cycleCount();

    /**
     * @return the battery's vendor, if available.
     */
    @NotNull Optional<String> vendor();

    /**
     * @return the battery's model, if available.
     */
    @NotNull Optional<String> model();

    /**
     * @return the battery's serial number, if available.
     */
    @NotNull Optional<String> serialNumber();

    /**
     * Time until the battery is full.
     * <p>
     * This value may change vastly when updating the battery. Any aggregation should be made by the caller.
     *
     * @return amount of time until the battery is full, if it is currently charging.
     */
    @NotNull Optional<Duration> timeToFull();

    /**
     * Time until the battery is empty.
     * <p>
     * This value may change vastly when updating the battery. Any aggregation should be made by the caller.
     *
     * @return amount of time until the battery is empty, if it is currently discharging.
     */
    @NotNull Optional<Duration> timeToEmpty();

    /**
     * Refresh battery information in-place.
     *
     * @throws IOException battery information couldn't be refreshed.
     * @throws RuntimeException the associated manager is not active.
     */
    void update() throws IOException, RuntimeException;
}
