package gdd.sprite;

import static gdd.Global.*;

public class Shot extends Sprite {
    private static final int H_SPACE = 10; // Horizontal offset from player
    private static final int V_SPACE = 1;  // Vertical offset from player
    private static final int SHOT_SPEED = 15; // Pixels per frame

    public Shot(int x, int y) {
        initShot(x, y);
    }

    private void initShot(int x, int y) {
        // Load and scale the shot image using the specific shot dimensions
        setImage(loadAndScaleImage(
            IMG_SHOT, 
            SHOT_WIDTH * SCALE_FACTOR, 
            SHOT_HEIGHT * SCALE_FACTOR
        ));
        
        // Position the shot relative to player position
        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }

    @Override
    public void act() {
        // Move the shot upward at constant speed
        setY(getY() - SHOT_SPEED);
        
        // Remove the shot if it goes off-screen
        if (getY() < 0) {
            die();
        }
    }

    // Helper method to center multi-shots
    public static int getMultiShotOffset(int shotIndex, int totalShots) {
        return (shotIndex * 20) - (totalShots * 10);
    }
}