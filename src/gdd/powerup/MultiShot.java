package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class MultiShot extends PowerUp {
    public MultiShot(int x, int y) {
        super(x, y);
    }

    @Override
    protected void initImage() {
        ImageIcon ii = new ImageIcon(getClass().getResource(IMG_POWERUP_MULTISHOT));
        if (ii.getImage() != null) {
            setImage(ii.getImage().getScaledInstance(
                ii.getIconWidth(),
                ii.getIconHeight(),
                java.awt.Image.SCALE_SMOOTH));
        }
    }

    @Override
    public void upgrade(Player player) {
        this.die();
    }
}