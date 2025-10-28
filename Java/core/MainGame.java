package core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import core.entities.Player;

/**
 * MainGame is the core LibGDX ApplicationAdapter.
 *
 * Lifecycle:
 *  - create(): allocate long-lived resources (camera, viewport, batch, game objects)
 *  - render(): runs every frame; update game logic, then draw
 *  - resize(): handle window size changes; recompute viewport
 *  - dispose(): free resources
 *
 * Coordinate model:
 *  - We use a small "virtual resolution" (VIRTUAL_WIDTH x VIRTUAL_HEIGHT),
 *    where 1 world unit == 1 pixel for crisp pixel art.
 *  - FitViewport scales the game to fit the window while preserving aspect ratio,
 *    adding black bars (letterboxing) as needed.
 */
public class MainGame extends ApplicationAdapter {

    // ------------------ VIRTUAL RESOLUTION ------------------

    /** Logical width of the world, in world units (treated like pixels). */
    public static final int VIRTUAL_WIDTH  = 320;

    /** Logical height of the world, in world units. */
    public static final int VIRTUAL_HEIGHT = 180;

    // ------------------ RENDERING CAMERA / VIEWPORT ------------------

    /**
     * OrthographicCamera projects the 2D world onto the screen with no perspective.
     * Its position is in world units; we move it to "follow" the player.
     */
    private OrthographicCamera camera;

    /**
     * FitViewport keeps aspect ratio by scaling the world to fit within the window.
     * Black bars appear if the window's aspect doesn't match VIRTUAL_WIDTH:VIRTUAL_HEIGHT.
     */
    private FitViewport viewport;

    /** SpriteBatch efficiently draws many sprites (textures/regions) with minimal state changes. */
    private SpriteBatch batch;

    // ------------------ GAME OBJECTS ------------------

    /** The controllable player entity that handles input, movement, and animation. */
    private Player player;

    // ------------------ LIFECYCLE: CREATE ------------------

    /**
     * Called once at startup. Allocate and configure all long-lived objects here.
     */
    @Override
    public void create() {
        // 1) Create the camera with default parameters (y-up world, no perspective).
        camera = new OrthographicCamera();

        // 2) Create a FitViewport that maps the virtual world to the window.
        //    The viewport maintains a camera internally and sets its worldSize.
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        // 3) Start the camera looking at the center of the virtual world.
        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);

        // 4) Create the SpriteBatch used to render textures.
        batch = new SpriteBatch();

        // 5) Create the player roughly in the middle of the world.
        //    Speed is in world units per second; tweak for your desired feel.
        player = new Player(160, 90, 60f);
    }

    // ------------------ LIFECYCLE: RENDER (PER-FRAME LOOP) ------------------

    /**
     * Called continuously (typically ~60 times/second). Do game logic and drawing here.
     * Order matters:
     *  1) Clear screen
     *  2) Update game state with delta time
     *  3) Update camera (if following something)
     *  4) Bind batch to camera projection
     *  5) Draw world
     */
    @Override
    public void render() {
        // ---- 1) CLEAR THE FRAME BUFFER ----
        // Set background color once per frame (RGBA), then clear the color buffer.
        Gdx.gl.glClearColor(0.11f, 0.13f, 0.17f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ---- 2) UPDATE GAME LOGIC ----
        // Delta time is the time (in seconds) since last frame; use it for framerate-independent movement.
        float dt = Gdx.graphics.getDeltaTime();
        player.update(dt);

        // ---- 3) CAMERA FOLLOW (PIXEL-PERFECT) ----
        // Make the camera center on the player and snap to integer coordinates
        // so pixel art stays razor sharp (no subpixel blur).
        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0); // fixed camera
        camera.update();

        // ---- 4) DRAW SPRITES ----
        // Tell the batch to use the camera's projection, then draw the player.
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        batch.end();
    }

    // ------------------ LIFECYCLE: RESIZE ------------------

    /**
     * Called whenever the window changes size (or on mobile, orientation).
     * We enforce integer scaling to keep pixel art crisp:
     *  - Compute the largest integer scale that fits the window
     *  - Set viewport bounds to the scaled size, centered (letterboxed)
     */
    @Override
    public void resize(int width, int height) {
        // Compute the largest integer scale that fits both width and height.
        int scale = Math.max(1, Math.min(width / VIRTUAL_WIDTH, height / VIRTUAL_HEIGHT));

        // Calculate the pixel size of the viewport at that scale.
        int vpW = VIRTUAL_WIDTH * scale;
        int vpH = VIRTUAL_HEIGHT * scale;

        // Center the viewport in the window (letterboxing).
        int vpX = (width  - vpW) / 2;
        int vpY = (height - vpH) / 2;

        // Apply the computed screen bounds to the viewport and update the camera.
        // The boolean "centerCamera" re-centers the camera to the world center.
        viewport.setScreenBounds(vpX, vpY, vpW, vpH);
        viewport.apply(true);
    }

    // ------------------ LIFECYCLE: DISPOSE ------------------

    /**
     * Free GPU/CPU resources you created in create().
     * Not disposing leaks memory or GPU objects.
     */
    @Override
    public void dispose() {
        player.dispose(); // Player owns textures; cleanly free them.
        batch.dispose();  // Batch owns GPU buffers; release them.
        // Note: If you add maps, atlases, or other disposables, dispose them here too.
    }
}
