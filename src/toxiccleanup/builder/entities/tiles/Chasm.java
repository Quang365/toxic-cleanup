package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.PlayerOverHook;
import toxiccleanup.builder.machines.Adjustable;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;

/**
 * A {@link Chasm} tile represents a dangerous pit in the game world. There are two variants:
 * Fallable chasm (default constructor): occupies a full grid cell and deals 1 damage to the player each tick they stand on it via {@link PlayerOverHook#playerOver(toxiccleanup.engine.EngineState, toxiccleanup.builder.GameState)}.
 * Chasm edge (facing constructor): a decorative border tile that visually frames the pit using directional sprites ('left', 'leftslope', 'right', 'rightslope'). Edge tiles do NOT deal damage to the player.
 * Rendered using {@link SpriteGallery#chasm}.
 *
 * @stage1
 */
public class Chasm extends Tile implements PlayerOverHook {
    private boolean fallable;

    /**
     * Constructs a new Chasm tile at the given position. Using the default sprite, and set to be 'fallable' for player interactions (meaning it can deal damage to the player when overlapping).
     *
     * @param position the position we wish to place our newly spawned Chasm Tile at.
     * @requires position.getX() >= 0, position.getX() is less than the window width, position.getY() >= 0, position.getY() is less than the window height
     *
     * @stage
     */
    public Chasm(Positionable position) {
        super(position, SpriteGallery.chasm);
        this.fallable = true;
    }

    /**
     * Constructs a new Chasm tile at the given position displaying the given facing sprite representing a chasm edge.
     * The facing parameter controls which directional sprite to use, and the resulting Chasm tile will NOT be 'fallable' for player interactions
     * (i.e. it will not deal damage to the player when overlapping).
     *
     * @param position the position we wish to place our newly spawned Chasm Tile at.
     * @param facing controls which facing sprite to use if the Chasm is intended to be set to a chasm edge sprite that does NOT deal damage to a player on it.
     * @requires position.getX() >= 0, position.getX() is less than the window width, position.getY() >= 0, position.getY() is less than the window height, "left", "leftslope", "right", or "rightslope" for facing
     *
     * @stage1
     */
    public Chasm(Positionable position, String facing) {
        super(position, SpriteGallery.chasm);
        this.fallable = false;

        updateSprite(facing);
    }

    /**
     * Called each tick the player occupies this tile's grid cell. If this chasm is fallable (i.e. constructed via the default constructor),
     * deals 1 damage to the player by calling {@link Adjustable#adjust(int)} with 1. Edge tiles (constructed with a facing) are not fallable and do nothing.
     * @param engine The state of the toxiccleanup.engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     *
     * @stage1
     */
    @Override
    public void playerOver(EngineState engine, GameState game) {
        super.playerOver(engine, game);

        if (fallable) {
            game.getPlayer().adjust(1);
        }
    }
}
