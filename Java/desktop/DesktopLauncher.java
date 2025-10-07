package desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import core.MainGame;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("Poke Clone");
        cfg.setWindowedMode(960, 540); // 3x scale of 320x180
        cfg.useVsync(true);
        cfg.setForegroundFPS(60);
        new Lwjgl3Application(new MainGame(), cfg);
    }
}
