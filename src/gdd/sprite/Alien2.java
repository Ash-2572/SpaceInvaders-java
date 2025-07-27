package gdd.sprite;

import static gdd.Global.*;

public class Alien2 extends Enemy {
    public Alien2(int x, int y) {
        super(x, y);
        setImage(loadAndScaleImage(IMG_ENEMY2, 
                ALIEN_WIDTH * SCALE_FACTOR, 
                ALIEN_HEIGHT * SCALE_FACTOR));
    }

    @Override
    public void act(int direction) {
        this.y += 2;
        this.x += Math.sin(this.y / 20.0) * 2;
        super.act(direction);
    }
}