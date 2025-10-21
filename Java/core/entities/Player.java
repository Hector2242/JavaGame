package core.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * The Player class represents a controllable character.
 * It handles input, movement, animation, and rendering.
 * Each frame, the player’s position and animation state are updated,
 * and then the correct frame is drawn to the screen.
 */
public class Player {

    // ------------------ CONSTANTS ------------------

    /** Time each animation frame lasts (seconds per frame). */
    private static final float FRAME_DURATION = 0.12f;

    /** Default walking speed, measured in world units per second. */
    private static final float DEFAULT_SPEED = 60f;

    /** Desired height of the sprite in world units (controls on-screen size). */
    private static final float TARGET_HEIGHT_WORLD = 32f;

    // ------------------ POSITION / STATE ------------------

    /** Current X and Y position of the player in world coordinates. */
    private float x, y;

    /** How fast the player moves per second. */
    private float speed = DEFAULT_SPEED;

    /** Keeps track of how much time has passed since the start of the animation. */
    private float stateTime = 0f;

    /** True when the player is moving, false when idle. */
    private boolean moving = false;

    /** Enum for four cardinal directions. */
    private enum Dir { DOWN, UP, LEFT, RIGHT }

    /** Direction the player is currently facing. */
    private Dir facing = Dir.DOWN;

    // ------------------ GRAPHICS ------------------

    /** Idle frames for each facing direction. */
    private final TextureRegion idleDown, idleUp, idleLeft, idleRight;

    /** Walking animations for each direction (each has 2 frames looping). */
    private final Animation<TextureRegion> walkDown, walkUp, walkLeft, walkRight;

    /** All textures loaded by this object (so we can properly dispose them later). */
    private final Texture[] ownedTextures;

    // ------------------ CONSTRUCTOR ------------------

    /**
     * Creates a new Player at the given starting coordinates.
     * Loads all textures and builds idle/walk animations.
     */
    public Player(float startX, float startY, float speed) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;

        // Load idle textures (still images for each direction).
        Texture idleFrontTex  = load("assets/player/idleFront.PNG");
        Texture idleBackTex   = load("assets/player/idleBack.PNG");
        Texture idleLeftTex   = load("assets/player/idleLeftView.PNG");
        Texture idleRightTex  = load("assets/player/IdleRightView.PNG");

        // Load walking animation frames (two per direction).
        Texture f1 = load("assets/player/animationFront1.PNG");
        Texture f2 = load("assets/player/animationFront2.PNG");
        Texture b1 = load("assets/player/animationBackWalk1.PNG");
        Texture b2 = load("assets/player/animationBackWalk2.PNG");
        Texture l1 = load("assets/player/animationLeftWalk1.PNG");
        Texture l2 = load("assets/player/animationLeftWalk2.PNG");
        Texture r1 = load("assets/player/animationRightWalk1.PNG");
        Texture r2 = load("assets/player/animationRightWalk2.PNG");

        // Store all textures so they can be disposed later.
        ownedTextures = new Texture[] {
                idleFrontTex, idleBackTex, idleLeftTex, idleRightTex,
                f1, f2, b1, b2, l1, l2, r1, r2
        };

        // Wrap single images into TextureRegions (used by SpriteBatch).
        idleDown  = new TextureRegion(idleFrontTex);
        idleUp    = new TextureRegion(idleBackTex);
        idleLeft  = new TextureRegion(idleLeftTex);
        idleRight = new TextureRegion(idleRightTex);

        // Create looping animations for walking (each uses 2 frames).
        walkDown  = loop(frames(f1, f2));
        walkUp    = loop(frames(b1, b2));
        walkLeft  = loop(frames(l1, l2));
        walkRight = loop(frames(r1, r2));
    }

    // ------------------ TEXTURE HELPERS ------------------

    /**
     * Loads a texture from disk and sets "nearest neighbor" filtering.
     * This keeps pixel art sharp instead of blurry when scaled.
     */
    private static Texture load(String path) {
        Texture t = new Texture(path);
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return t;
    }

    /**
     * Creates a looping animation from an array of frames.
     * Each frame lasts FRAME_DURATION seconds.
     */
    private static Animation<TextureRegion> loop(TextureRegion[] fr) {
        Animation<TextureRegion> a = new Animation<>(FRAME_DURATION, fr);
        a.setPlayMode(Animation.PlayMode.LOOP);
        return a;
    }

    /** Converts two Texture objects into a TextureRegion array (for animations). */
    private static TextureRegion[] frames(Texture a, Texture b) {
        return new TextureRegion[] { new TextureRegion(a), new TextureRegion(b) };
    }

    // ------------------ GAME LOOP: UPDATE ------------------

    /**
     * Updates player state each frame.
     * Handles input, movement, direction facing, and animation time.
     */
    public void update(float delta) {
        float vx = 0, vy = 0; // Velocity components for movement direction.

        // ----------- HANDLE INPUT -----------

        // Check if any movement keys are held down.
        boolean up    = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down  = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean left  = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        //Running Velocity
        boolean upRun    = (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
        boolean downRun  = (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
        boolean leftRun  = (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
        boolean rightRun = (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));

        // Convert key presses into a direction vector.
        if (up)    vy += 1;
        if (down)  vy -= 1;
        if (left)  vx -= 1;
        if (right) vx += 1;

        if (upRun)    vy += 10;
        if (downRun)  vy -= 10;
        if (leftRun)  vx -= 10;
        if (rightRun) vx += 10;

        // Determine the direction the player is facing (used for selecting animation).
        if      (vy > 0) facing = Dir.UP;
        else if (vy < 0) facing = Dir.DOWN;
        else if (vx > 0) facing = Dir.RIGHT;
        else if (vx < 0) facing = Dir.LEFT;

        // ----------- NORMALIZE DIAGONAL SPEED -----------

        // Normalize diagonal movement so diagonal speed isn't faster.
        float len = (float)Math.hypot(vx, vy); // Compute vector length.
        if (len > 0f) { vx /= len; vy /= len; }

        // ----------- UPDATE POSITION -----------

        // Move player based on velocity, speed, and frame delta time.
        x += vx * speed * delta;
        y += vy * speed * delta;

        // Record whether player is moving or idle.
        moving = (len > 0f);

        // Advance animation timer (used for selecting animation frames).
        stateTime += delta;
    }

    // ------------------ GAME LOOP: RENDER ------------------

    /**
     * Draws the player’s current frame to the screen using the SpriteBatch.
     * Picks either a walking or idle frame based on movement state.
     */
    public void render(SpriteBatch batch) {
        TextureRegion frame;

        // Select correct animation frame based on facing direction and movement state.
        if (moving) {
            switch (facing) {
                case UP:    frame = walkUp.getKeyFrame(stateTime, true);    break;
                case DOWN:  frame = walkDown.getKeyFrame(stateTime, true);  break;
                case LEFT:  frame = walkLeft.getKeyFrame(stateTime, true);  break;
                case RIGHT: frame = walkRight.getKeyFrame(stateTime, true); break;
                default:    frame = idleDown;
            }
        } else {
            switch (facing) {
                case UP:    frame = idleUp;    break;
                case DOWN:  frame = idleDown;  break;
                case LEFT:  frame = idleLeft;  break;
                case RIGHT: frame = idleRight; break;
                default:    frame = idleDown;
            }
        }

        // --- SCALE DRAW: adjust sprite size to match desired on-screen height ---

        float srcW = frame.getRegionWidth();   // width in pixels of source image
        float srcH = frame.getRegionHeight();  // height in pixels of source image

        // Compute how much to scale so the sprite’s height = TARGET_HEIGHT_WORLD
        float scale = TARGET_HEIGHT_WORLD / srcH;
        float drawW = Math.round(srcW * scale);
        float drawH = Math.round(srcH * scale);

        // Draw at integer pixel positions to avoid blurry rendering.
        batch.draw(frame, Math.round(x), Math.round(y), drawW, drawH);
    }

    // ------------------ CLEANUP ------------------

    /**
     * Frees all textures used by the player to prevent memory leaks.
     * Always call this when shutting down the game.
     */
    public void dispose() {
        for (Texture t : ownedTextures)
            if (t != null) t.dispose();
    }

    // ------------------ GETTERS ------------------

    /** Returns current X position in world space. */
    public float getX() {
        return x;
    }

    /** Returns current Y position in world space. */
    public float getY() {
        return y;
    }
}
