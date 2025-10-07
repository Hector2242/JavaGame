package core.entities;

import com.badlogic.gdx.graphics.Texture;        // Image for the sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch;// Drawer for textures
import com.badlogic.gdx.Input;                   // Key constants
import com.badlogic.gdx.Gdx;                     // Delta time and input

public class Player {
    private final Texture texture;   // Player image
    private float x, y;              // Position in world units
    private float speed;             // Units per second

    public Player(String texturePath, float startX, float startY, float speed) {
        this.texture = new Texture(texturePath); // Load player texture
        this.x = startX;                         // Initial X
        this.y = startY;                         // Initial Y
        this.speed = speed;                      // Move speed
    }

    public void update(float delta) {            // Handle movement each frame
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))
            y += speed * delta;                  // Move up
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))
            y -= speed * delta;                  // Move down
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
            x -= speed * delta;                  // Move left
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            x += speed * delta;                  // Move right
    }

    public void render(SpriteBatch batch) {      // Draw sprite at current pos
        batch.draw(texture, x, y);
    }

    public void dispose() {                      // Free GPU memory
        texture.dispose();
    }

    // Getters if you want camera follow later
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return texture.getWidth(); }
    public int getHeight() { return texture.getHeight(); }
}
