package toxiccleanup.builder.machines;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.timing.RepeatingTimer;

/**
 * A {@link Pump} is a machine that removes toxicity from a {toxiccleanup.builder.entities.tiles.ToxicField}
 * over time. Every 100 game ticks it calls {@link Adjustable#adjust(int)} on
 * its target with {@code 1}, reducing the field's toxicity by 1. The pump only operates when
 * the shared power system has at least 2 power units available - if power drops below 2, both
 * the animation and the pumping pause until power is restored.
 *
 * <p>The pump stops and is removed from the field once the field's toxicity reaches 0 (i.e.
 * when the field is fully cleaned up).
 *
 * <p>Costs {COST} power units to build. The pump cycles through a sprite animation every
 * 4 ticks while powered. Rendered using {@link SpriteGallery#pump}.
 *
 * <p><span style="color:#9B59B6;">Provided:</span> The class is provided without
 * {@code extends} or {@code implements} clauses, and with no fields or methods.
 *
 * @provided
 * @stage3
 */
public class Pump extends GameEntity implements Powered {

    /**
     * The number of power units required to place this pump.
     *
     * @stage3
     */
    public static final int COST = 5;

    private RepeatingTimer animationTimer;
    private RepeatingTimer pumpTimer;

    private Adjustable target;

    private int frameIndex;

    /**
     * Constructs a new Pump at the given position. Initializes an animation timer that fires every 4 ticks and a pump timer that fires every 100 ticks.
     * The given {@link Adjustable} is the target whose value is reduced by 1 each time the pump timer fires (provided sufficient power is available).
     *
     * @param position the position we wish to spawn this Pump at.
     * @param pumpTarget the object whose adjustable value (e.g. toxicity) will be reduced each time the pump fires.
     *
     * @stage3
     */
    public Pump(Positionable position, Adjustable pumpTarget) {
        super(position);
        this.target = pumpTarget;

        this.animationTimer = new RepeatingTimer(4);
        this.pumpTimer = new RepeatingTimer(100);

        this.frameIndex = 1;
        setSprite(SpriteGallery.pump.getSprite("1"));
    }

    /**
     * Returns the minimum power level required for this pump to operate. The pump's animation and pumping action only proceed when the shared power system meets this threshold.
     *
     * @return 2, the number of power units required for the pump to function.
     *
     * @stage3
     */
    @Override
    public int getPowerRequirement() {
        return 2;
    }

    /**
     * Called every game tick to advance the pump's internal timers. Both the animation timer (every 4 ticks) and the pump timer (every 100 ticks) are ticked unconditionally,
     * but their effects only apply when the shared power system has at least 2 power units:
     * When the animation timer fires and power ≥ 2: the displayed sprite advances to the next animation frame, looping back to frame 1 after the last frame.
     * When the pump timer fires and power ≥ 2: {@link Adjustable#adjust(int)} is called on the pump's target with 1, reducing its toxicity by 1.
     * This may cause the target field to mark the pump for removal if toxicity reaches 0.
     * If power drops below 2, the animation freezes and no pumping occurs until power is restored.
     *
     * @param state The state of the toxiccleanup.engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     *
     * @stage3
     */
    @Override
    public void tick(EngineState state, GameState game) {

        animationTimer.tick();
        pumpTimer.tick();

        Machines machines = game.getMachines();

        if (machines.hasRequiredPower(getPowerRequirement())) {
            if (animationTimer.isFinished()) {
                frameIndex = (frameIndex % 10) + 1;
                setSprite(SpriteGallery.pump.getSprite(String.valueOf(frameIndex)));
            }
            if (pumpTimer.isFinished()) {
                target.adjust(1);
            }
        }
    }
}
