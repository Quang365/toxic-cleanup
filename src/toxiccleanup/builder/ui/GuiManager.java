package toxiccleanup.builder.ui;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.ToxicCleanup;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.engine.ui.Text;
import toxiccleanup.engine.ui.TextWithIcon;
import toxiccleanup.builder.SpriteGallery;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all HUD (heads-up display) elements shown during gameplay. Each tick, the {@link GuiManager} rebuilds the following on-screen elements from the current game state:
 * Power icon: a lightning-bolt icon in the top-left corner.
 * Power bar: a vertical column of {@link PowerBar} segments below the power icon, showing how much of the maximum power (14) is currently available;
 * filled segments represent available power, empty segments represent spent power.
 * Hearts: a vertical column of {@link Heart} icons in the top-right corner, one per remaining HP point.
 * Countdown timer: a text display at the bottom-left showing the remaining game time in minutes and seconds.
 * When the game ends, {@link GuiManager#win(toxiccleanup.engine.EngineState)} or {@link GuiManager#lose(toxiccleanup.engine.EngineState)} is called to overlay a win/lose message
 * in the centre of the screen. Once set, this message persists until the game restarts.
 *
 * @stage2
 */
public class GuiManager implements Overlay {
    private List<Renderable> renderables;

    private boolean isWin;
    private boolean isLose;

    private TextWithIcon message;

    /**
     * Constructs a new instance of our {@link GuiManager}.
     *
     * @stage2
     */
    public GuiManager() {
        this.renderables = new ArrayList<>();
        this.isWin = false;
        this.isLose = false;
    }

    /**
     * Called each game tick to rebuild all HUD elements from the current game state. This method recalculates and
     * recreates the power icon, power bars, heart icons, and countdown timer text on every tick so they always reflect up-to-date values.
     * Notes: This method must becalled before {@link GuiManager#render()} is invoked, as render depends on state set here.
     * The countdown is based on a 5-minute game duration, i.e. 18000 ticks at 60 ticks per second.
     * * The power icon is centred in the top-left tile, the power bars are placed below it in a vertical column extending downward,
     * and the hearts are likewise arranged downward from the top-right.
     * @param state The state of the toxiccleanup.engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     */
    @Override
    public void tick(EngineState state, GameState game) {
        renderables.clear();

        buildPower(state, game);
        buildHearts(state, game);
        buildTimer(state);
    }

    private void buildPower(EngineState state, GameState game) {
        Dimensions d = state.getDimensions();

        int power = game.getMachines().getPower();

        int iconX = d.tileToPixel(0);
        int iconY = d.tileToPixel(0);

        renderables.add(new PowerIcon(new Position(iconX, iconY)));

        for (int i = 0; i < 14; i++) {
            int x = d.tileToPixel(0);
            int y = d.tileToPixel(i + 1);

            boolean filled = i < power;

            renderables.add(new PowerBar(new Position(x, y), filled));
        }
    }

    private void buildHearts(EngineState state, GameState game) {
        Dimensions d = state.getDimensions();

        int hp = game.getPlayer().getHp();

        int maxTile = d.windowSize() / d.tileSize() - 1;

        for (int i = 0; i < hp; i++) {
            int x = d.tileToPixel(maxTile);
            int y = d.tileToPixel(i);

            renderables.add(new Heart(new Position(x, y)));
        }
    }

    private void buildTimer(EngineState state) {
        Dimensions d = state.getDimensions();

        int remainingTicks = 18000 - state.currentTick();
        if (remainingTicks < 0) {
            remainingTicks = 0;
        }

        int totalSeconds = remainingTicks / 60;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String textStr = minutes + " " + String.format("%02d", seconds);

        int maxTile = d.windowSize() / d.tileSize() - 1;

        int x = d.tileToPixel(0);
        int y = d.tileToPixel(maxTile);

        TextWithIcon t = new TextWithIcon(
                SpriteGallery.power.getSprite("icon"),
                x,
                y,
                d.tileSize()
        );

        t.update(textStr);

        renderables.addAll(t.render());
    }

    /**
     * Switches the GUI to display a centred "YOU WIN" message. Called by {@link ToxicCleanup#tick(toxiccleanup.engine.EngineState)}
     * when all toxic fields have been cleared. Once set, the win message is rendered on every subsequent call to {@link GuiManager#render()}.
     *
     * @param state the current toxiccleanup.engine state, used to determine window dimensions for positioning the text in the centre of the screen.
     *
     * @stage2
     */
    public void win(EngineState state) {
        isWin = true;
        isLose = false;
        message = createCenteredText(state, "YOU WIN");
    }

    /**
     * Switches the GUI to display a centred "GAME OVER" message. Called by {@link ToxicCleanup#tick(toxiccleanup.engine.EngineState)}
     * when all toxic fields have been cleared. Once set, the win message is rendered on every subsequent call to {@link GuiManager#render()}.
     *
     * @param state the current toxiccleanup.engine state, used to determine window dimensions for positioning the text in the centre of the screen.
     *
     * @stage2
     */
    public void lose(EngineState state) {
        isLose = true;
        isWin = false;
        message = createCenteredText(state, "GAME OVER");
    }

    private TextWithIcon createCenteredText(EngineState state, String textStr) {
        Dimensions d = state.getDimensions();

        int tileCount = d.windowSize() / d.tileSize();
        int centerTile = tileCount / 2;

        int length = textStr.length();
        int offset = length / 2;

        int x = d.tileToPixel(centerTile - offset);
        int y = d.tileToPixel(centerTile);

        TextWithIcon t = new TextWithIcon(
                SpriteGallery.power.getSprite("icon"),
                x,
                y,
                d.tileSize()
        );

        t.update(textStr);

        return t;
    }

    /**
     * Returns all HUD renderables for the current frame. Includes the power icon, power bar segments, and heart icons
     * (rebuilt each tick by {@link GuiManager#tick(toxiccleanup.engine.EngineState, toxiccleanup.builder.GameState)}, plus the countdown timer text.
     * If a win or lose condition has been triggered, the overlay message is also included.
     *
     * @return a list of all Renderable HUD elements to display this frame.
     *
     * @stage2
     */
    @Override
    public List<Renderable> render() {
        List<Renderable> result = new ArrayList<>(renderables);

        if (isWin || isLose) {
            result.addAll(message.render());
        }

        return result;
    }
}
