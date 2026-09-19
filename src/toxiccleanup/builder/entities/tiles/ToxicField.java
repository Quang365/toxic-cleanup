package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.entities.PlayerOverHook;
import toxiccleanup.builder.machines.Adjustable;
import toxiccleanup.builder.machines.Machines;
import toxiccleanup.builder.machines.Pump;
import toxiccleanup.builder.world.ToxicWorld;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;

/**
 * A {@link ToxicField} represents a contaminated flower field tile that can be purified using a {@link Pump}.
 * A toxic field begins fully toxic and progresses through four visual states as its toxicity is reduced via {@link Adjustable#adjust(int)}:
 *
 * Starting toxic
 * Cleanup begun
 * Cleanup nearly done
 * Cleanup finished: flowers restored
 * Rendered using {@link SpriteGallery#toxicField}.
 *
 * @multistage
 */
public class ToxicField extends Tile implements PlayerOverHook, Adjustable {
    private int toxicity;

    /**
     * Constructs a new toxic field tile at the given position. With toxicity 6.
     *
     * @param position The position we wish to place this newly constructed tile at.
     * @requires position.getX() >= 0, position.getX() is less than the window width, position.getY() >= 0, position.getY() is less than the window height
     *
     * @stage1
     */
    public ToxicField(Positionable position) {
        super(position, SpriteGallery.toxicField);
        this.toxicity = 6;
    }

    /**
     * Reduces the toxicity of this field by amount. The tile's sprite is updated to reflect the new toxicity level:
     * Toxicity ≥ 3: default toxic appearance.
     * Toxicity = 2: cleanup starting (slightly less toxic).
     * Toxicity = 1: cleanup almost done.
     * Toxicity = 0: fully cleaned - flowers restored. All stacked entities (e.g. the Pump) are marked for removal.
     *
     * @param amount the amount to subtract from the current toxicity level (typically 1).
     *
     * @stage1
     */
    @Override
    public void adjust(int amount) {
        toxicity -= amount;

        if (toxicity < 0) {
            toxicity = 0;
        }

        if (toxicity >= 3) {
            updateSprite("default");
        } else if (toxicity == 2) {
            updateSprite("cleanupstart");
        } else if (toxicity == 1) {
            updateSprite("cleanupmid");
        } else {
            updateSprite("cleanupdone");

            for (GameEntity entity : getStackedEntities()) {
                entity.markForRemoval();
            }
        }
    }

    /**
     * Returns whether this field still contains any toxicity. Used by {@link ToxicWorld#isToxic()} to determine whether the game has been won.
     * Also used by the pump-spawning logic to prevent placing a pump on an already-clean field.
     *
     * @return true if toxicity is greater than 0; false if the field has been fully cleaned up (toxicity = 0).
     *
     * @stage1
     */
    public boolean isToxic() {
        return toxicity > 0;
    }

    /**
     * Handles player interaction while the player is on this toxic field tile.
     * Behaviour: - Applies player-over behaviour for any stacked entities on this tile.
     * - If the field is eligible for building (toxic and no stacked entities) and the left mouse button is pressed,
     * attempts to place a {@link Pump} on this tile via the machine system.
     * - If pump placement succeeds, the pump is stacked on this tile; otherwise, tile state is unchanged.
     * @param state The state of the toxiccleanup.engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     *
     * @stage3
     */
    @Override
    public void playerOver(EngineState state, GameState game) {

        super.playerOver(state, game);

        if (!getStackedEntities().isEmpty()) {
            return;
        }

        if (!isToxic()) {
            return;
        }

        if (state.getMouse().isLeftPressed()) {

            Machines machines = game.getMachines();

            Pump pump = machines.spawnPump(getPosition(), this);

            if (pump != null) {
                placeOn(pump);
            }
        }
    }
}
