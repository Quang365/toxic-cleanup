package toxiccleanup.builder.entities.tiles;

import toxiccleanup.engine.game.Positionable;

/**
 * A tile factory uses the {@link TileFactory#fromSymbol(Positionable, char)} method to construct new tile instances from a set encoding.
 */
public class TileFactory {
    /**
     * Constructor
     */
    public TileFactory() {

    }

    /**
     * Constructs a new {@link Tile} based on the symbol encoded at the given position. The following table enumerates the tile encodings.
     * Character Tile
     * d Dirt
     * t ToxicField
     * g Grass
     * l Chasm (set to 'left' facing sprite)
     * L Chasm (set to 'leftslope' facing sprite)
     * r Chasm (set to 'right' facing sprite)
     * R Chasm (set to 'rightslope' facing sprite)
     * c Chasm
     *
     * Any characters not listed above should throw an {@link IllegalArgumentException}.
     *
     * @param position the position we wish to place our next tile at.
     * @param symbol A symbol to identify the tile type.
     * @return A new tile at the given x,y coordinate of the type specified by the symbol.
     * @throws IllegalArgumentException - If symbol does not correspond to a tile.
     * @requires position.getX() >= 0, position.getY() >= 0
     *
     * @stage1
     */
    public static Tile fromSymbol(Positionable position, char symbol) {
        switch (symbol) {
            case 'd':
                return new Dirt(position);

            case 't':
                return new ToxicField(position);

            case 'g':
                return new Grass(position);

            case 'l':
                return new Chasm(position, "left");

            case 'L':
                return new Chasm(position, "leftslope");

            case 'r':
                return new Chasm(position, "right");

            case 'R':
                return new Chasm(position, "rightslope");

            case 'c':
                return new Chasm(position);

            default:
                throw new IllegalArgumentException("Invalid tile symbol: " + symbol);
        }
    }
}

