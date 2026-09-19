package toxiccleanup.builder.machines;

import toxiccleanup.builder.ToxicCleanup;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.util.RandomNumberGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * The concrete implementation of {@link Machines} for the {@link ToxicCleanup} game. {@link MachinesManager} is responsible for:
 * Tracking the current power level (starts at 14 by default; capped at 14).
 * Spending power when a machine is built; each machine type has a fixed COST constant on its class (e.g. {@link SolarPanel#COST}, {@link Teleporter#COST}, {@link Pump#COST}).
 * Constructing and returning new machine instances when there is sufficient power, or returning null if the power cost cannot be met.
 * Tracking all teleporter positions so that the {@link Teleporter} can retrieve a destination when the player activates one.
 *
 * @multistage
 */
public class MachinesManager implements Machines {
    private int power;
    private final int maxPower = 14;
    private List<Positionable> teleporterPositions;

    /**
     * Constructs a new {@link MachinesManager} starting at full power (14). Use this constructor when the game should begin with maximum power available.
     *
     * @stage2
     */
    public MachinesManager() {
        this.power = maxPower;
        this.teleporterPositions = new ArrayList<>();
    }

    /**
     * Constructs a new {@link MachinesManager} with the given amount of starting power. Maximum power is fixed at the default (14).
     *
     * @param power the starting power level; clamped to [0, 14] if out of range.
     *
     * @stage2
     */
    public MachinesManager(int power) {
        this.power = Math.max(0, Math.min(power, maxPower));
        this.teleporterPositions = new ArrayList<>();
    }

    /**
     * Returns whether the current power level is sufficient for a machine's operation.
     *
     * @param powerRequirement the minimum number of power units needed.
     * @return true if the current power is greater than or equal to powerRequirement; false otherwise.
     *
     * @stage2
     */
    @Override
    public boolean hasRequiredPower(int powerRequirement) {
        return power >= powerRequirement;
    }

    /**
     * Returns the current power level of this machine manager.
     *
     * @return the current power, in the range [0, {@link MachinesManager#getMaxPower()}].
     *
     * @stage2
     */
    @Override
    public int getPower() {
        return power;
    }

    /**
     * Sets the power to the given value, clamped to [0, maxPower (14 by default)].
     *
     * @param value the power level to set.
     *
     * @stage2
     */
    @Override
    public void setPower(int value) {
        power = Math.max(0, Math.min(value, maxPower));
    }

    /**
     * Returns the maximum power capacity of this {@link MachinesManager}.
     *
     * @return the upper bound for power, fixed at 14 by default.
     *
     * @stage2
     */
    @Override
    public int getMaxPower() {
        return maxPower;
    }

    /**
     * Adds amount to the current power level, then clamps the result to [0, 14].
     * Pass a positive value to gain power (e.g. from a {@link SolarPanel}) or a negative value to spend power.
     * This is the primary method called by machines to change power.
     *
     * @param amount - the amount of power to add to the current total.
     *
     * @stage2
     */
    @Override
    public void adjust(int amount) {
        setPower(power + amount);
    }

    /**
     * Attempts to create a {@link SolarPanel} at the given location. If the current power is at least 3 (the solar panel's cost),
     * deducts 3 power and returns the new instance. Returns null if there is insufficient power.
     * Stage 2: Returns null. No spawn logic is required at this stage.
     * Stage 3: Implements the full spawn logic, deducting power and returning a new {@link SolarPanel} if sufficient power is available.
     *
     * @param position the position we wish to spawn the {@link SolarPanel} at.
     * @return the newly created SolarPanel, or null if power < 3.
     *
     * @stage2
     * @stage3
     */
    @Override
    public SolarPanel spawnSolarPanel(Positionable position) {
        if (!hasRequiredPower(SolarPanel.COST)) {
            return null;
        }

        adjust(-SolarPanel.COST);

        return new SolarPanel(position);
    }

    /**
     * Attempts to create a {@link Teleporter} at the given location. If the current power is at least 2 (the teleporter's cost),
     * deducts 2 power, records the teleporter's position for future {@link MachinesManager#getNextTeleporterPosition(toxiccleanup.engine.game.Positionable)} calls,
     * and returns the new instance. Returns null if there is insufficient power.
     * Stage 2: Returns null. No spawn logic is required at this stage.
     * Stage 3: Implements the full spawn logic, deducting power, recording the teleporter's position, and returning a new Teleporter if sufficient power is available.
     *
     * @param position the position we wish to spawn the {@link Teleporter} at.
     * @return the newly created Teleporter, or null if power < 2.
     *
     * @stage2
     * @stage3
     */
    @Override
    public Teleporter spawnTeleporter(Positionable position) {
        if (!hasRequiredPower(Teleporter.COST)) {
            return null;
        }

        adjust(-Teleporter.COST);

        teleporterPositions.add(position);

        return new Teleporter(position);
    }

    /**
     * Attempts to create a {@link Pump} at the given position targeting the given {@link Adjustable}.
     * If the current power is at least 5 (the pump's cost), deducts 5 power and returns a new {@link Pump} that will call {@link Adjustable#adjust(int)}
     * on adjustable every 100 ticks. Returns null if there is insufficient power.
     * Stage 2: Returns null. No spawn logic is required at this stage.
     * Stage 3: Implements the full spawn logic, deducting power and returning a new {@link Pump} if sufficient power is available.
     *
     * @param position   position we wish to spawn the {@link Pump} at.
     * @param adjustable the adjustable we wish the pump to hold a reference to.
     * @return the newly created {@link Pump}, or null if power < 5.
     *
     * @stage2
     * @stage3
     */
    @Override
    public Pump spawnPump(Positionable position, Adjustable adjustable) {
        if (!hasRequiredPower(Pump.COST)) {
            return null;
        }

        adjust(-Pump.COST);

        return new Pump(position, adjustable);
    }

    /**
     * Returns the position of a teleporter other than the one at excludedPosition, chosen at random from all registered teleporter locations.
     * This is used by the {@link Teleporter} to determine where to send the player. If only one teleporter exists, its own position is returned
     * (the player teleports in place).
     * Stage 2: Returns excludedPosition. No logic is required at this stage.
     * Stage 3: Implements the full random selection logic, returning a randomly chosen teleporter position excluding the given position
     * when multiple teleporters exist. Random selection is performed using RandomNumberGenerator.
     *
     * Specified by:
     * @param excludedPosition the position of the teleporter the player is currently on;
     *                         excluded from the result when other options exist.
     * @return the next position (Positionable) from stored {@link Teleporter} positions.
     *
     * @stage2
     * @stage3
     */
    @Override
    public Positionable getNextTeleporterPosition(Positionable excludedPosition) {

        if (teleporterPositions.size() <= 1) {
            return excludedPosition;
        }

        List<Positionable> candidates = new ArrayList<>();

        for (Positionable pos : teleporterPositions) {
            if (!pos.equals(excludedPosition)) {
                candidates.add(pos);
            }
        }

        RandomNumberGenerator rng = new RandomNumberGenerator();
        int index = rng.nextInt(candidates.size());

        return candidates.get(index);
    }
}
