package gdd;

import gdd.scene.Scene1;
import gdd.scene.Scene2;
import gdd.scene.TitleScene;
import javax.swing.JFrame;

public class Game extends JFrame {
    private TitleScene titleScene;
    private Scene1 scene1;
    private Scene2 scene2;

    public Game() {
        setTitle("Space Invaders");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        titleScene = new TitleScene(this);
        loadTitle();
    }

    public void loadTitle() {
        getContentPane().removeAll();
        add(titleScene);
        titleScene.start();
        revalidate();
        repaint();
    }

    public void loadScene1() {
        getContentPane().removeAll();
        scene1 = new Scene1(this);
        add(scene1);
        titleScene.stop();
        scene1.start();
        revalidate();
        repaint();
    }

    public void loadScene2(int speedLevel, int shotLevel) {
        getContentPane().removeAll();
        scene2 = new Scene2(this, speedLevel, shotLevel);
        add(scene2);
        scene1.stop();
        scene2.start();
        revalidate();
        repaint();
    }
}