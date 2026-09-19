package toxiccleanup.builder;

import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.world.World;
import toxiccleanup.builder.machines.Machines;
import toxiccleanup.builder.player.Player;
import toxiccleanup.builder.player.PlayerManager;
import toxiccleanup.engine.EngineState;

/**
 * The ToxicCleanup-specific implementation of the GameState interface.
 * This class bundles together the three core components of the game (the {@link World}, the {@link PlayerManager}, and the {@link Machines} system)
 * into a single snapshot that is passed to every {@link Tickable#tick(EngineState, GameState)} call each frame.
 * Components that receive a {@link GameState} can use it to read or modify the world, query the player's position and HP,
 * and interact with the power and machine system.
 *
 * @multistage
 */
public class ToxicCleanupGameState extends Object implements GameState {
    private final PlayerManager playerManager;
    private final World world;
    private final Machines machines;

    /**
     * Constructs a new {@link ToxicCleanupGameState} wrapping only the player manager.
     * Use this constructor when only player-related state is needed and world or machine access is not required.
     *
     * @param playerManager the player manager, used to query position, HP, and move the player.
     * @stage0
     */
    public ToxicCleanupGameState(PlayerManager playerManager, World world, Machines machines) {
        this.playerManager = playerManager;
        this.world = world;
        this.machines = machines;
    }

    /**
     * Returns the current state of the player. Useful for retrieving the player's location.
     *
     * @return The player of the game.
     * @stage0
     */
    @Override
    public Player getPlayer() {
        return playerManager;
    }

    /**
     * Returns the current state of the game world.
     * The returned world is mutable, that is, calling mutator methods such as {@link World#place(Tile)} will modify the world.
     *
     * @return The game world.
     * @stage0
     */
    @Override
    public World getWorld() {
        return world;
    }

    /**
     * Returns the current state of the machine system.
     *
     * @return the {@link Machines} instance, providing access to machine spawning, teleporter locations, and the power system.
     * @stage0
     */
    @Override
    public Machines getMachines() {
        return machines;
    }
}
