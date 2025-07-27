package gdd.sprite;

import static gdd.Global.*;
import java.util.Random;

public class Enemy extends Sprite {
    protected Bomb bomb;
    protected int bombChance = 100;
    protected boolean canDropBombs = true;
    protected Random random = new Random();

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.bomb = new Bomb(x, y);
        setImage(loadAndScaleImage(IMG_ENEMY, 
                ALIEN_WIDTH * SCALE_FACTOR, 
                ALIEN_HEIGHT * SCALE_FACTOR));
    }

    public void act(int direction) {
        this.x += direction;
        
        if (canDropBombs && random.nextInt(bombChance) == 0 && bomb.isDestroyed()) {
            bomb.setDestroyed(false);
            bomb.setX(this.x + this.getImage().getWidth(null)/2 - bomb.getImage().getWidth(null)/2);
            bomb.setY(this.y + this.getImage().getHeight(null));
        }
        
        if (!bomb.isDestroyed()) {
            bomb.setY(bomb.getY() + 3);
            if (bomb.getY() > GROUND) {
                bomb.setDestroyed(true);
            }
        }
    }

    public Bomb getBomb() { return bomb; }

    public class Bomb extends Sprite {
        private boolean destroyed;

        public Bomb(int x, int y) {
            initBomb(x, y);
        }

        private void initBomb(int x, int y) {
            setDestroyed(true);
            this.x = x;
            this.y = y;
            setImage(loadAndScaleImage(IMG_BOMB, 
                    BOMB_WIDTH * SCALE_FACTOR, 
                    BOMB_HEIGHT * SCALE_FACTOR));
        }

        public void setDestroyed(boolean destroyed) { this.destroyed = destroyed; }
        public boolean isDestroyed() { return destroyed; }
    }
}