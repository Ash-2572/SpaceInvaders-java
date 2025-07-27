package gdd.sprite;

import static gdd.Global.*;

public class Alien1 extends Enemy {
    public Alien1(int x, int y) {
        super(x, y);
        setImage(loadAndScaleImage(IMG_ENEMY, 
                ALIEN_WIDTH * SCALE_FACTOR, 
                ALIEN_HEIGHT * SCALE_FACTOR));
    }

    @Override
    public void act(int direction) {
        this.y += 1;
        super.act(direction);
    }
}