package gdd.sprite;

import static gdd.Global.*;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;

public class BossEnemy extends Enemy {
    private int health = 100;
    private int bombTimer = 0;
    private List<Bomb> activeBombs = new ArrayList<>();
     private float preciseY = 0;
    private int movementCounter=0;
    public BossEnemy(int x, int y) {
        super(x, y);
        this.preciseY=y;
        initBoss();
    }

    private void initBoss() {
        Image original = loadImage(IMG_BOSS);
        if (original != null) {
            setImage(original.getScaledInstance(
                original.getWidth(null) * SCALE_FACTOR,
                original.getHeight(null) * SCALE_FACTOR,
                Image.SCALE_SMOOTH));
        }
    }

    // In the act() method of BossEnemy.java
    @Override
    public void act(int direction) {
         movementCounter++;
        if (movementCounter % 3 == 0) { 
            this.y += BOSS_DOWNWARD_SPEED;
        }
    
        // Smoother side-to-side movement
        this.x += Math.cos(this.y / 60.0) * 1; // Reduced from 3 to 2 and increased divisor
    
        // Bomb firing logic (less frequent)
        bombTimer++;
        if (bombTimer % BOSS_BOMB_INTERVAL == 0) {
            fireBombVolley();
        }
    
        updateBombs();
    }

    private void fireBombVolley() {
        int centerX = this.x + this.getImage().getWidth(null)/2;
        int baseY = this.y + this.getImage().getHeight(null);
        
        // Create spread-out bombs
        for (int i = 0; i < BOSS_BOMB_COUNT; i++) {
            int offsetX = (i * BOSS_BOMB_SPREAD) - (BOSS_BOMB_SPREAD * (BOSS_BOMB_COUNT-1)/2);
            Bomb bomb = new Bomb(centerX + offsetX, baseY);
            bomb.setDestroyed(false);
            activeBombs.add(bomb);
        }
    }

    private void updateBombs() {
        Iterator<Bomb> iterator = activeBombs.iterator();
        while (iterator.hasNext()) {
            Bomb bomb = iterator.next();
            if (!bomb.isDestroyed()) {
                int BOMB_SPEED=5;
                bomb.setY(bomb.getY() + BOMB_SPEED);
                if (bomb.getY() > GROUND) {
                    bomb.setDestroyed(true);
                    iterator.remove();
                }
            } else {
                iterator.remove();
            }
        }
    }

    public List<Bomb> getActiveBombs() {
        return new ArrayList<>(activeBombs); // Return copy for safety
    }

    public void takeDamage() {
        health--;
        if (health <= 0) {
            setDying(true);
        }
    }

    public int getHealth() {
        return health;
    }

    public class Bomb extends Sprite {
        private boolean destroyed;

        public Bomb(int x, int y) {
            initBomb(x, y);
        }

        private void initBomb(int x, int y) {
            setDestroyed(true);
            this.x = x;
            this.y = y;
            ImageIcon ii = new ImageIcon(getClass().getResource(IMG_BOMB));
            if (ii.getImage() != null) {
                setImage(ii.getImage().getScaledInstance(
                    BOMB_WIDTH * SCALE_FACTOR,
                    BOMB_HEIGHT * SCALE_FACTOR,
                    Image.SCALE_SMOOTH));
            }
        }

        public void setDestroyed(boolean destroyed) {
            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }
}