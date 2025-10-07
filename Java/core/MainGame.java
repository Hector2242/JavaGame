package core;

import com.badlogic.gdx.ApplicationAdapter;            // Game lifecycle
import com.badlogic.gdx.Gdx;                           // Delta time
import com.badlogic.gdx.graphics.GL20;                 // Clearing buffers
import com.badlogic.gdx.graphics.OrthographicCamera;   // 2D camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch;      // Sprite renderer
import com.badlogic.gdx.utils.viewport.FitViewport;    // Aspect-correct scaling
import core.entities.Player;                           // Our Player class

public class MainGame extends ApplicationAdapter {

    public static final int VIRTUAL_WIDTH = 1000;      // Logical width
    public static final int VIRTUAL_HEIGHT = 1000;     // Logical height

    private OrthographicCamera camera;                 // What we see
    private FitViewport viewport;                      // Keeps aspect ratio
    private SpriteBatch batch;                         // Draws sprites
    private Player player;                             // The player entity

    @Override
    public void create() {                             // Called once on start
        camera = new OrthographicCamera();             // New camera
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera); // Viewport
        camera.position.set(VIRTUAL_WIDTH / 2f, VIRTUAL_HEIGHT / 2f, 0);   // Center cam

        batch = new SpriteBatch();                     // Create batch
        player = new Player("assets/player.png", 100, 100, 200f); // Init player
    }

    @Override
    public void render() {                             // Called every frame
        Gdx.gl.glClearColor(0.11f, 0.13f, 0.17f, 1f);  // Clear color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);      // Clear screen

        player.update(Gdx.graphics.getDeltaTime());    // Update player

        camera.update();                               // Update camera matrices
        batch.setProjectionMatrix(camera.combined);    // Use camera for drawing

        batch.begin();                                 // Begin drawing
        player.render(batch);                          // Draw player
        batch.end();                                   // Flush to GPU
    }

    @Override
    public void resize(int width, int height) {        // On window resize
        viewport.update(width, height, true);          // Recenter camera
    }

    @Override
    public void dispose() {                            // On exit
        player.dispose();                               // Free player texture
        batch.dispose();                                // Free batch resources
    }
}
