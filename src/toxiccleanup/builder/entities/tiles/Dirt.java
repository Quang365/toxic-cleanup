package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.machines.Machines;
import toxiccleanup.builder.machines.SolarPanel;
import toxiccleanup.builder.machines.Teleporter;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;

/**
 * A {@link Dirt} tile is a buildable ground tile with two visual states: unpaved and paved. Tiles start unpaved.
 * When the player stands on an unpaved dirt tile and presses the pave key ('f'), the tile transitions to the paved state.
 * It is rendered using the default sprite of {@link SpriteGallery#dirt}
 * Once paved and with no machines already on it, the player can build machines by clicking:
 *
 * Left-click: attempts to build a {@link SolarPanel} (costs 3 power).
 * Right-click: attempts to build a {@link Teleporter} (costs 2 power).
 * Both actions delegate to {@link Machines} which checks whether sufficient power is available before constructing the machine.
 * If power is insufficient, nothing is built.
 *
 * @multistage
 */
public class Dirt extends Tile {

    private boolean paved;

    /**
     * Constructs a new unpaved dirt tile at the given position.
     *
     * @param position The position we wish to place this newly constructed tile at.
     * @requires position.getX() >= 0, position.getX() is less than the window width, position.getY() >= 0, position.getY() is less than the window height
     *
     * @stage1
     */
    public Dirt(Positionable position) {
        super(position, SpriteGallery.dirt);
        this.paved = false;
    }

    /**
     * Returns whether this dirt tile has been paved. A paved tile uses the paved sprite group and can have machines
     * ({@link SolarPanel} or {@link Teleporter}) built on it.
     *
     * @return true if the tile has been paved; false if it is still unpaved dirt.
     *
     * @stage3
     */
    public boolean isPaved() {
        return paved;
    }

    /**
     * Transitions this tile from unpaved to paved.
     * Sets the internal paved flag to true and switches the tile's sprite group to the paved art so it renders accordingly.
     * Has no effect if the tile is already paved (calling it multiple times is safe).
     *
     * @stage3
     */
    public void pave() {
        if (!paved) {
            paved = true;

            setSprite(SpriteGallery.paved.getSprite("default"));
        }
    }

    /**
     * Asks the machine system to build a {@link SolarPanel} at this tile's position. If the spawn succeeds (i.e. sufficient power was available),
     * the new solar panel is placed on top of this tile via {@link Tile#placeOn(toxiccleanup.builder.entities.GameEntity)}.
     * If the spawn returns null (insufficient power), nothing happens.
     *
     * @param spawner the {@link Machines} instance used to attempt spawning the {@link SolarPanel}.
     *
     * @stage3
     */
    public void attemptSpawnSolarPanel(Machines spawner) {

        SolarPanel sp = spawner.spawnSolarPanel(getPosition());

        if (sp != null) {
            placeOn(sp);
        }
    }

    /**
     * Asks the machine system to build a {@link Teleporter} at this tile's position. If the spawn succeeds (i.e. sufficient power was available),
     * the new teleporter is placed on top of this tile via {@link Tile#placeOn(toxiccleanup.builder.entities.GameEntity)},
     * and its position is registered for future teleportation. If the spawn returns null (insufficient power), nothing happens.
     *
     * @param spawner the {@link Machines} instance used to attempt spawning the {@link Teleporter}.
     *
     * @stage3
     */
    public void attemptSpawnTeleporter(Machines spawner) {

        Teleporter tp = spawner.spawnTeleporter(getPosition());

        if (tp != null) {
            placeOn(tp);
        }
    }

    /**
     * Handles player interaction while the player is on this dirt tile.
     * Behaviour: - If the tile is unpaved and the pave key ('f') is pressed, the tile becomes paved.
     * - If the tile is paved and has no stacked entities, build input may place one machine: left-click attempts to place a {@link SolarPanel},
     * right-click attempts to place a {@link Teleporter}.
     * - Any placed machine is added to this tile; if placement fails (e.g. insufficient power), no machine is added.
     * - Player-over behaviour of stacked entities on this tile is also applied.
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
        if (!isPaved() && state.getKeys().isDown('f')) {
            pave();
        }

        if (isPaved() && getStackedEntities().isEmpty()) {

            Machines machines = game.getMachines();

            if (state.getMouse().isLeftPressed()) {
                attemptSpawnSolarPanel(machines);
            }

            if (state.getMouse().isRightPressed()) {
                attemptSpawnTeleporter(machines);
            }
        }

        super.playerOver(state, game);
    }
}
