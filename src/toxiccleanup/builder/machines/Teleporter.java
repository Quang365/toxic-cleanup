package toxiccleanup.builder.machines;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.entities.PlayerOverHook;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.timing.RepeatingTimer;

/**
 * A {@link Teleporter} is a machine that allows the player to instantly travel between teleporter
 * locations on the map. When the player stands on a teleporter and presses the use key ('e'),
 * they are moved to a randomly chosen other teleporter's position — provided the shared power
 * system has at least {COST} power units available. Power is NOT consumed on use;
 * it is only required to be present.
 *
 * <p>Costs {COST} power units to build. When powered, cycles through a sprite animation
 * every 12 ticks. If power drops below the requirement, the animation pauses. Rendered using
 * {@link SpriteGallery#teleporter}.
 *
 * <p><span style="color:#9B59B6;">Provided:</span> The class is provided without
 * {@code extends} or {@code implements} clauses, and with no fields or methods.
 *
 * @provided
 * @stage3
 */
public class Teleporter extends GameEntity implements PlayerOverHook, Powered {

    /**
     * The number of power units required to place this teleporter.
     *
     * @stage3
     */
    public static final int COST = 2;

    private RepeatingTimer animationTimer;
    private int frameIndex;

    /**
     * Constructs a new Teleporter at the given position.
     *
     * @param position the position we wish to spawn this Teleporter at.
     *
     * @stage3
     */
    public Teleporter(Positionable position) {
        super(position);
        this.animationTimer = new RepeatingTimer(12);
        this.frameIndex = 1;
        setSprite(SpriteGallery.teleporter.getSprite("1"));
    }

    /**
     * Returns the minimum power level required for this teleporter to animate and be used. Both the animation
     * and the {@link Teleporter#playerOver(toxiccleanup.engine.EngineState, toxiccleanup.builder.GameState)} teleportation check use
     * this value via {@link Machines#hasRequiredPower(int)}.
     *
     * @return 2, the number of power units required for this teleporter to operate.
     *
     * @stage3
     */
    @Override
    public int getPowerRequirement() {
        return 2;
    }

    /**
     * Called every game tick to advance the teleporter's animation timer. When the timer fires and the machine system has at least 2 power units,
     * the displayed sprite advances to the next animation frame. If power is insufficient, the animation pauses.
     *
     * @param engine The state of the toxiccleanup.engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     *
     * @stage3
     */
    @Override
    public void tick(EngineState engine, GameState game) {

        animationTimer.tick();

        Machines machines = game.getMachines();

        if (machines.hasRequiredPower(getPowerRequirement())) {
            if (animationTimer.isFinished()) {
                frameIndex = (frameIndex % 7) + 1;
                setSprite(SpriteGallery.teleporter.getSprite(String.valueOf(frameIndex)));
            }
        }
    }

    /**
     * Called each tick the player occupies this teleporter's grid cell. If the use key ('e') is held
     * and the machine system has at least 2 power units available, the player is teleported to a randomly selected other teleporter's
     * position via {@link Machines#getNextTeleporterPosition(Positionable)}. Power is checked but NOT deducted on use.
     *
     * @param state The state of the toxiccleanup.engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     *
     * @stage3
     */
    @Override
    public void playerOver(EngineState state, GameState game) {

        Machines machines = game.getMachines();

        if (state.getKeys().isDown('e') && machines.hasRequiredPower(getPowerRequirement())) {

            Positionable next =
                    machines.getNextTeleporterPosition(this.getPosition());

            game.getPlayer().setPosition(next);
        }
    }
}
