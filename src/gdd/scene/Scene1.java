package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.powerup.MultiShot;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Alien2;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene1 extends JPanel {
    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private Player player;
    private boolean inGame = true;
    private String message = "Game Over";
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random random = new Random();
    private Timer timer;
    private final Game game;
    private AudioPlayer audioPlayer;
    private int score = 0;
    private int speedLevel = 1;
    private int shotLevel = 1;
    private int enemiesDefeated = 0;
    private boolean showBossWarning = false;
    private int warningFrames = 0;
    private final int ENEMIES_PER_STAGE = 70;
    private int spawnRate = 80;
    private final int spawnRateIncreaseInterval = 600;
    private final int minSpawnRate = 30;
    private final int spawnGroupSize = 8;
    private int currentStage = 1;
    private boolean showStageMessage = false;
    private int stageMessageFrames = 0;

    public Scene1(Game game) {
        this.game = game;
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
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error stopping audio player: " + e.getMessage());
        }
    }

    private void gameInit() {
        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        player = new Player();
        enemiesDefeated = 0;
        showBossWarning = false;
        currentStage = 1;
        showStageMessage = true;
        stageMessageFrames = 120; // Show stage message for 2 seconds
    }

    private void initAudio() {
        try {
            // Play different music for stage 2
            String audioFile = currentStage == 1 ? "src/audio/scene1.wav" : "src/audio/scene12.wav";
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
            audioPlayer = new AudioPlayer(audioFile);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio: " + e.getMessage());
        }
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2, BOARD_WIDTH / 2);
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
            drawPowerUps(g);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawDashboard(g);
            drawBossWarning(g);
            drawStageMessage(g);
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawDashboard(Graphics g) {
        g.setColor(Color.white);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Speed: " + speedLevel, 10, 40);
        g.drawString("Shots: " + shotLevel, 10, 60);
        g.drawString("Stage: " + currentStage, 10, 80);
        g.drawString("Enemies: " + enemiesDefeated + "/" + (currentStage * ENEMIES_PER_STAGE), 10, 100);
    }

    private void drawStageMessage(Graphics g) {
        if (showStageMessage) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            String message = "STAGE " + currentStage;
            int stringWidth = g.getFontMetrics().stringWidth(message);
            g.drawString(message, (BOARD_WIDTH - stringWidth) / 2, BOARD_HEIGHT / 2);
        }
    }

    private void drawAliens(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
                if (!enemy.getBomb().isDestroyed()) {
                    g.drawImage(enemy.getBomb().getImage(), enemy.getBomb().getX(), enemy.getBomb().getY(), this);
                }
            }
            if (enemy.isDying()) {
                enemy.die();
            }
        }
    }

    private void drawPowerUps(Graphics g) {
        List<PowerUp> toRemove = new ArrayList<>();
        for (PowerUp p : powerups) {
            if (p.isVisible()) {
                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            } else {
                toRemove.add(p);
            }
        }
        powerups.removeAll(toRemove);
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

    private void drawBossWarning(Graphics g) {
        if (showBossWarning) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            String warning = "BOSS FIGHT INCOMING!";
            int stringWidth = g.getFontMetrics().stringWidth(warning);
            g.drawString(warning, (BOARD_WIDTH - stringWidth) / 2, BOARD_HEIGHT / 2);
        }
    }

    private void update() {
        frame++;
        
        if (frame % spawnRateIncreaseInterval == 0 && spawnRate > minSpawnRate) {
            spawnRate -= 5;
        }
        
        if (frame % spawnRate == 0) {
            for (int i = 0; i < spawnGroupSize; i++) {
                if (random.nextInt(3) == 0) {
                    spawnRandomEnemy();
                }
            }
        }
        
        if (frame % 180 == 0 && random.nextInt(5) == 0) {
            spawnRandomPowerUp();
        }

        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.setY(powerup.getY() + 2);
                if (powerup.getY() > BOARD_HEIGHT) {
                    powerup.die();
                }
            }
        }

        player.act();

        checkCollisions();

        if (showStageMessage) {
            stageMessageFrames--;
            if (stageMessageFrames <= 0) {
                showStageMessage = false;
            }
        }

        if (enemiesDefeated >= currentStage * ENEMIES_PER_STAGE) {
            if (currentStage < 2) {
                currentStage++;
                showStageMessage = true;
                stageMessageFrames = 120;
                enemies.clear();
                initAudio(); // Change music for stage 2
            } else if (currentStage == 2 && !showBossWarning) {
                showBossWarning = true;
                warningFrames = 180;
            }
        }
        
        if (showBossWarning) {
            warningFrames--;
            if (warningFrames <= 0) {
                game.loadScene2(speedLevel, shotLevel);
            }
        }
    }

    private void spawnRandomEnemy() {
        int x = random.nextInt(BOARD_WIDTH - ALIEN_WIDTH);
        int y = -ALIEN_HEIGHT - random.nextInt(100);
        
        // In stage 1, only spawn Alien1
        if (currentStage == 1) {
            enemies.add(new Alien1(x, y));
        } 
        // In stage 2, spawn both types
        else if (currentStage == 2) {
            enemies.add(random.nextBoolean() ? new Alien1(x, y) : new Alien2(x, y));
        }
    }

    private void spawnRandomPowerUp() {
        int x = random.nextInt(BOARD_WIDTH - 30);
        int y = -30;
        powerups.add(random.nextBoolean() ? new SpeedUp(x, y) : new MultiShot(x, y));
    }

    private void checkCollisions() {
        for (Enemy enemy : enemies) {
            if (player.isVisible() && enemy.isVisible() && player.collidesWith(enemy)) {
                handlePlayerDeath();
                break;
            }
            
            if (!enemy.getBomb().isDestroyed() && player.isVisible() && player.collidesWith(enemy.getBomb())) {
                handlePlayerDeath();
                enemy.getBomb().setDestroyed(true);
                break;
            }
        }

        for (PowerUp powerup : powerups) {
            if (powerup.isVisible() && powerup.collidesWith(player)) {
                powerup.upgrade(player);
                if (powerup instanceof SpeedUp) {
                    speedLevel = Math.min(speedLevel + 1, 4);
                } else if (powerup instanceof MultiShot) {
                    shotLevel = Math.min(shotLevel + 1, 4);
                }
                powerup.die();
            }
        }

        List<Shot> shotsToRemove = new ArrayList<>();
        List<Enemy> enemiesToRemove = new ArrayList<>();

        for (Shot shot : shots) {
            if (shot.isVisible()) {
                for (Enemy enemy : enemies) {
                    if (enemy.isVisible() && shot.collidesWith(enemy)) {
                        explosions.add(new Explosion(enemy.getX(), enemy.getY()));
                        enemy.setDying(true);
                        enemiesDefeated++;
                        score += 100;
                        shot.die();
                        shotsToRemove.add(shot);
                        enemiesToRemove.add(enemy);
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
        }
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