package toxiccleanup.builder.world;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.Tickable;
import toxiccleanup.builder.ToxicCleanup;
import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.entities.tiles.ToxicField;
import toxiccleanup.builder.ui.RenderableGroup;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link World} implementation for the {@link ToxicCleanup} game.
 * A world consists of a grid of tiles. Each tick, the world progresses every tile's state,
 * and via the render method collects all renderables from each tile and its stacked entities.
 *
 * @stage1
 */
public class ToxicWorld implements RenderableGroup, Tickable, World {
    private List<Tile> tiles;

    /**
     * Constructor
     */
    public ToxicWorld() {
        this.tiles = new ArrayList<>();
    }

    /**
     * Adds the given tile to this world at the position encoded in the tile itself (Entity.getX(), Entity.getY()).
     * After calling this method, the tile will appear in the results of
     * {@link ToxicWorld#tilesAtPosition(toxiccleanup.engine.game.Positionable, toxiccleanup.engine.renderer.Dimensions)} and {@link ToxicWorld#allTiles()}.
     * @param tile The tile to place into the world.
     *
     * @stage1
     */
    @Override
    public void place(Tile tile) {
        tiles.add(tile);
    }

    /**
     * Returns a copy of the full list of tiles in this world. Modifying the returned list will not affect the world's internal tile collection
     * (although mutating tile objects within it will).
     * @return a new list containing every tile currently in the world.
     *
     * @stage1
     */
    @Override
    public List<Tile> allTiles() {
        return new ArrayList<>(tiles);
    }

    /**
     * Returns all tiles whose grid cell (determined by converting pixel coordinates to tile indices via Dimensions.pixelToTile(int))
     * matches the grid cell of the given pixel position. For example, if the tile size is 25 px, pixel position (37, 12) maps to grid cell (1, 0),
     * and all tiles at that grid cell are returned.
     * @param position   the pixel position to look up (e.g. the player's current position).
     * @param dimensions the window dimensions used for pixel-to-tile conversion.
     * @return all tiles occupying the same grid cell as position; empty if none.
     *
     * @stage1
     */
    @Override
    public List<Tile> tilesAtPosition(Positionable position, Dimensions dimensions) {
        List<Tile> result = new ArrayList<>();

        int targetX = dimensions.pixelToTile(position.getX());
        int targetY = dimensions.pixelToTile(position.getY());

        for (Tile tile : tiles) {
            int tileX = dimensions.pixelToTile(tile.getX());
            int tileY = dimensions.pixelToTile(tile.getY());

            if (tileX == targetX && tileY == targetY) {
                result.add(tile);
            }
        }

        return result;
    }

    /**
     * Returns whether any tile in the world is still toxic.
     * @return true if at least one {@link ToxicField} tile has remaining toxicity; false if all fields have been fully cleaned up
     *
     * @stage1
     */
    public boolean isToxic() {
        for (Tile tile : tiles) {
            if (tile instanceof ToxicField) {
                if (((ToxicField) tile).isToxic()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Advances every tile in the world by one tick. Each tile present in the world at tick start is updated exactly once.
     * Any stacked-entity updates/removals required by tile behavior must be reflected by method return.
     * @param state The state of the toxiccleanup.engine, including the mouse, keyboard information and dimension.
     * @param game  The state of the game, including the player and world.
     *
     * @stage1
     */
    @Override
    public void tick(EngineState state, GameState game) {
        for (Tile tile : new ArrayList<>(tiles)) {
            tile.tick(state, game);
        }
    }

    /**
     * A collection of items to render, including every tile and stacked entity in the world.
     * The order of the list must be consistent with {@link Tile#render()}; that is,
     * a tile must occur in the list before any of its stacked entities and the stacked entities order must match {@link Tile#getStackedEntities()}.
     *
     * Otherwise, any ordering is appropriate.
     * @return The list of renderables required to draw the world to the screen.
     *
     * @stage1
     */
    @Override
    public List<Renderable> render() {
        List<Renderable> result = new ArrayList<>();

        for (Tile tile : tiles) {
            result.addAll(tile.render());
        }

        return result;
    }
}
