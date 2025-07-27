package gdd;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class Global {
    private Global() {} // Prevent instantiation

    // Game constants
    public static final int SCALE_FACTOR = 3;
    public static final int BOARD_WIDTH = 716;
    public static final int BOARD_HEIGHT = 700;
    public static final int BORDER_RIGHT = 60;
    public static final int BORDER_LEFT = 10;
    public static final int GROUND = 580;
    public static final int ALIEN_HEIGHT = 14;
    public static final int ALIEN_WIDTH = 14;
    public static final int PLAYER_WIDTH = 15;
    public static final int PLAYER_HEIGHT = 10;
    public static final int SHOT_WIDTH = 1;  
    public static final int SHOT_HEIGHT = 10; 
    public static final int BOMB_WIDTH = 1;  
    public static final int BOMB_HEIGHT = 6; 
    public static final int BOSS_DOWNWARD_SPEED = 1;
    public static final int BOSS_BOMB_COUNT = 3;   
    public static final int BOSS_BOMB_INTERVAL = 120; 
    public static final int BOSS_BOMB_SPREAD = 120;   

    // Image paths (using classpath-relative paths)
    public static final String IMG_ENEMY = "/images/alien.png";
    public static final String IMG_ENEMY2 = "/images/alien2.png";
    public static final String IMG_PLAYER = "/images/player.png";
    public static final String IMG_SHOT = "/images/shot.png";
    public static final String IMG_EXPLOSION = "/images/explosion.png";
    public static final String IMG_TITLE = "/images/title.png";
    public static final String IMG_POWERUP_SPEEDUP = "/images/powerup-s.png";
    public static final String IMG_POWERUP_MULTISHOT = "/images/powerup-m.png";
    public static final String IMG_BOSS = "/images/boss.png";
    public static final String IMG_BOMB = "/images/bomb.png";

    // Image loading utilities with debug logging
    public static Image loadImage(String path) {
        System.out.println("[DEBUG] Loading image: " + path);
        try {
            URL url = Global.class.getResource(path);
            if (url == null) {
                System.err.println("[ERROR] Image not found at: " + path);
                return null;
            }
            System.out.println("[DEBUG] Image found at: " + url);
            ImageIcon icon = new ImageIcon(url);
            if (icon.getImage() == null) {
                System.err.println("[ERROR] Failed to load image: " + path);
                return null;
            }
            return icon.getImage();
        } catch (Exception e) {
            System.err.println("[ERROR] Exception loading image: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public static Image loadAndScaleImage(String path, int width, int height) {
        Image image = loadImage(path);
        if (image == null) {
            System.err.println("[ERROR] Could not scale null image: " + path);
            return null;
        }
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}