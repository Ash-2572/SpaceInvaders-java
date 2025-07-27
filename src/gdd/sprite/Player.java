package gdd.sprite;

import static gdd.Global.*;
import java.awt.event.KeyEvent;

public class Player extends Sprite {
    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int width;
    private int currentSpeed = 2;

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        setImage(loadAndScaleImage(IMG_PLAYER, 
                PLAYER_WIDTH * SCALE_FACTOR, 
                PLAYER_HEIGHT * SCALE_FACTOR));
        this.width = getImage().getWidth(null);
        setX(START_X);
        setY(START_Y);
    }

    public int getSpeed() { return currentSpeed; }

    public void setSpeed(int speed) {
        this.currentSpeed = Math.max(1, speed);
    }

    public void act() {
        x += dx;
        x = Math.max(2, Math.min(x, BOARD_WIDTH - 2 * width));
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT: dx = -currentSpeed; break;
            case KeyEvent.VK_RIGHT: dx = currentSpeed; break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT: dx = 0; break;
        }
    }
}