package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.sprite.BossEnemy;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene2 extends JPanel {
    private int frame = 0;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private Player player;
    private boolean inGame = true;
    private String message = "Game Over";
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;
    private final Game game;
    private AudioPlayer audioPlayer;
    private int score = 0;
    private BossEnemy boss;
    private int speedLevel;
    private int shotLevel;

    public Scene2(Game game, int speedLevel, int shotLevel) {
        this.game = game;
        this.speedLevel = speedLevel;
        this.shotLevel = shotLevel;
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        gameInit();
        initAudio();
        
        boss = new BossEnemy(BOARD_WIDTH / 2 - ALIEN_WIDTH, 50);
        enemies.add(boss);
    }

    private void gameInit() {
        enemies = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        player = new Player();
        player.setSpeed(2 + (speedLevel - 1) * 2);
    }

    private void initAudio() {
        try {
            audioPlayer = new AudioPlayer("src/audio/scene2.wav");
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Audio error: " + e.getMessage());
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        if (inGame) {
            drawExplosions(g);
            drawEnemies(g);
            drawPlayer(g);
            drawShot(g);
            drawDashboard(g);
        } else {
            gameOver(g);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawDashboard(Graphics g) {
        g.setColor(Color.white);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Speed: " + speedLevel, 10, 40);
        g.drawString("Shots: " + shotLevel, 10, 60);
        if (boss != null) {
            g.drawString("Boss Health: " + boss.getHealth(), 10, 80);
        }
    }

    private void drawEnemies(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
                
                if (enemy instanceof BossEnemy) {
                    BossEnemy boss = (BossEnemy)enemy;
                    for (BossEnemy.Bomb bomb : boss.getActiveBombs()) {
                        if (!bomb.isDestroyed()) {
                            g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), this);
                        }
                    }
                }
            }
            if (enemy.isDying()) {
                enemy.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }
        if (player.isDying()) {
            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawExplosions(Graphics g) {
        List<Explosion> toRemove = new ArrayList<>();
        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }
        explosions.removeAll(toRemove);
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_WIDTH / 2);
    }

    private void update() {
        frame++;

        player.act();

        if (boss != null) {
            // Player vs boss collision
            if (player.isVisible() && boss.isVisible() && player.collidesWith(boss)) {
                handlePlayerDeath();
            }
            
            // Player vs bombs collision
            for (BossEnemy.Bomb bomb : boss.getActiveBombs()) {
                if (!bomb.isDestroyed() && player.isVisible() && player.collidesWith(bomb)) {
                    handlePlayerDeath();
                    bomb.setDestroyed(true);
                }
            }
        }

        List<Shot> shotsToRemove = new ArrayList<>();
        List<Enemy> enemiesToRemove = new ArrayList<>();

        for (Shot shot : shots) {
            if (shot.isVisible()) {
                Rectangle shotRect = new Rectangle(
                    shot.getX(), 
                    shot.getY(),
                    shot.getImage().getWidth(null),
                    shot.getImage().getHeight(null)
                );

                for (Enemy enemy : enemies) {
                    if (enemy.isVisible()) {
                        if (enemy instanceof BossEnemy) {
                            BossEnemy boss = (BossEnemy)enemy;
                            // Expanded hitbox for boss (40 pixels wider/taller)
                            Rectangle bossHitbox = new Rectangle(
                                boss.getX() - 20,
                                boss.getY() - 20,
                                boss.getImage().getWidth(null) + 20,
                                boss.getImage().getHeight(null) + 20
                            );
                            
                            if (bossHitbox.intersects(shotRect)) {
                                boss.takeDamage();
                                score += 200;
                                shot.die();
                                explosions.add(new Explosion(shot.getX(), shot.getY()));
                                
                                if (boss.getHealth() <= 0) {
                                    message = "You Win!";
                                    inGame = false;
                                }
                                shotsToRemove.add(shot);
                            }
                        } 
                        // Regular enemies
                        else if (shot.collidesWith(enemy)) {
                            explosions.add(new Explosion(enemy.getX(), enemy.getY()));
                            enemy.setDying(true);
                            score += 100;
                            shot.die();
                            shotsToRemove.add(shot);
                            enemiesToRemove.add(enemy);
                        }
                    }
                }

                shot.setY(shot.getY() - 20);
                if (shot.getY() < 0) {
                    shot.die();
                    shotsToRemove.add(shot);
                }
            }
        }

        shots.removeAll(shotsToRemove);
        enemies.removeAll(enemiesToRemove);

        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(1);
                if (enemy.getY() > BOARD_HEIGHT) {
                    enemy.die();
                }
            }
            if (enemy.isDying()) {
                enemiesToRemove.add(enemy);
            }
        }
        enemies.removeAll(enemiesToRemove);
    }

    private void handlePlayerDeath() {
        player.setDying(true);
        explosions.add(new Explosion(player.getX(), player.getY()));
        inGame = false;
        message = "Game Over";
    }

    private void doGameCycle() {
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                if (shotLevel == 1) {
                    shots.add(new Shot(x, y));
                } else {
                    for (int i = 0; i < shotLevel; i++) {
                        shots.add(new Shot(x + (i * 20) - (shotLevel * 10), y));
                    }
                }
            }
        }
    }
}