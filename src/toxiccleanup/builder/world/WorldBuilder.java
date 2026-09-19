package toxiccleanup.builder.world;

import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.entities.tiles.TileFactory;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.util.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Load an instance of a world from a string representation.
 * Each line of the file, separated by new line characters, corresponds to a row of tiles in the world.
 * Each character represents a tile according to {@link TileFactory#fromSymbol(toxiccleanup.engine.game.Positionable, char)}.
 *
 * @stage1
 */
public class WorldBuilder {
    /**
     * Constructor
     */
    public WorldBuilder() {

    }

    /**
     * Read the encoded world text and construct the corresponding list of tiles.
     * Each line in the text corresponds to one row of tiles. Each character corresponds to one tile.
     * A character at row 3, column 10 in the text corresponds to a tile at y = 2 and x = 9.
     * You have to use Dimensions.tileToPixel(int) to convert these tile coordinates to pixel coordinates when creating the tile instances.
     *
     * The tile type for each character is determined based on {@link TileFactory#fromSymbol(toxiccleanup.engine.game.Positionable, char)}.
     *
     * The world is square. Let n = dimensions.windowSize() / dimensions.tileSize().
     * The encoding must contain exactly n lines, and each line must contain exactly n characters.
     *
     * If the number of rows, any row length, or any tile symbol is invalid, a {@link WorldLoadException} is thrown.
     *
     * @param dimensions The dimensions of the world. The tile encoding must correspond to these dimensions.
     * @param text The text encoding of a world.
     * @return A list of tiles loaded from the given string.
     * @throws WorldLoadException If the number of lines doesn't match the required amount according to the dimensions.
     * @throws WorldLoadException If the length of any line doesn't match the required amount according to the dimensions.
     * @throws WorldLoadException If any character doesn't correspond to a tile according to {@link TileFactory#fromSymbol(toxiccleanup.engine.game.Positionable, char)}.
     * @requires dimensions.windowSize() % dimensions.tileSize() == 0
     *
     * @stage1
     */
    public static List<Tile> fromString(Dimensions dimensions, String text)
            throws WorldLoadException {

        int n = dimensions.windowSize() / dimensions.tileSize();
        String[] lines = text.split("\n");

        if (lines.length != n) {
            throw new WorldLoadException("Invalid number of rows");
        }

        List<Tile> tiles = new ArrayList<>();

        for (int y = 0; y < n; y++) {
            String line = lines[y];

            if (line.length() != n) {
                throw new WorldLoadException("Invalid row length");
            }

            for (int x = 0; x < n; x++) {
                char c = line.charAt(x);

                int pixelX = dimensions.tileToPixel(x);
                int pixelY = dimensions.tileToPixel(y);

                Position pos = new Position(pixelX, pixelY);

                try {
                    Tile tile = TileFactory.fromSymbol(pos, c);
                    tiles.add(tile);
                } catch (IllegalArgumentException e) {
                    throw new WorldLoadException("Invalid symbol");
                }
            }
        }

        return tiles;
    }

    /**
     * Read the provided file and attempt to create a new world based on the tile encoding in the file.
     * See {@link WorldBuilder#fromString(Dimensions, String)} for a description of how the tile encoding is read.
     *
     * @param dimensions The dimensions of the world. The tile encoding must correspond to these dimensions.
     * @param filepath The path to a file containing a tile encoding.
     * @return A new world containing all tiles in the specified file.
     * @throws IOException If the file path doesn't exist or otherwise can't be read, as thrown by FileManager.readFile(String).
     * @throws WorldLoadException If the tile encoding is invalid (according to {@link WorldBuilder#fromString(Dimensions, String)}).
     *
     * @stage1
     */
    public static ToxicWorld fromFile(Dimensions dimensions, String filepath)
            throws IOException, WorldLoadException {

        FileManager fm = new FileManager();
        String text = fm.readFile(filepath);

        List<Tile> tiles = fromString(dimensions, text);

        return fromTiles(tiles);
    }

    /**
     * Constructs a new {@link ToxicWorld} pre-populated with the given tiles.
     * Tiles are added to the world in reverse order so that no test inadvertently depends on the insertion order of tiles;
     * the world's tile ordering is unspecified.
     *
     * @param tiles the list of tiles to place into the new world.
     * @return a new {@link ToxicWorld} containing all given tiles.
     *
     * stage1
     */
    public static ToxicWorld fromTiles(List<Tile> tiles) {
        ToxicWorld world = new ToxicWorld();

        for (int i = tiles.size() - 1; i >= 0; i--) {
            world.place(tiles.get(i));
        }

        return world;
    }
}
