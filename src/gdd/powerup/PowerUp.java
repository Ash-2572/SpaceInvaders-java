package gdd.powerup;

import gdd.sprite.Player;
import gdd.sprite.Sprite;

public abstract class PowerUp extends Sprite {
    public PowerUp(int x, int y) {
        this.x = x;
        this.y = y;
        initImage();
    }

    protected abstract void initImage();
    public abstract void upgrade(Player player);
    
    public void act() {
        // Movement handled in Scene1
    }
}