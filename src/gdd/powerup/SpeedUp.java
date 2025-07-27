package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class SpeedUp extends PowerUp {
    public SpeedUp(int x, int y) {
        super(x, y);
    }

    @Override
    protected void initImage() {
        ImageIcon ii = new ImageIcon(getClass().getResource(IMG_POWERUP_SPEEDUP));
        if (ii.getImage() != null) {
            setImage(ii.getImage().getScaledInstance(
                ii.getIconWidth(),
                ii.getIconHeight(),
                java.awt.Image.SCALE_SMOOTH));
        }
    }

    @Override
    public void upgrade(Player player) {
        player.setSpeed(player.getSpeed() + 4);
        this.die();
    }
}