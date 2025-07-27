package gdd.sprite;

import static gdd.Global.*;

public class Explosion extends Sprite {
    public Explosion(int x, int y) {
        initExplosion(x, y);
    }

    private void initExplosion(int x, int y) {
        this.x = x;
        this.y = y;
        setImage(loadAndScaleImage(IMG_EXPLOSION, 
                ALIEN_WIDTH * SCALE_FACTOR * 2, 
                ALIEN_HEIGHT * SCALE_FACTOR * 2));
    }

    public void visibleCountDown() {
        if (--visibleFrames <= 0) {
            visible = false;
        }
    }
}