package ru.nsu.gsafonkin.bomberman.view.swing;

import ru.nsu.gsafonkin.bomberman.model.GameContext;
import ru.nsu.gsafonkin.bomberman.model.IModel;
import ru.nsu.gsafonkin.bomberman.model.Model;
import ru.nsu.gsafonkin.bomberman.model.objects.Enemy;
import ru.nsu.gsafonkin.bomberman.model.objects.GameObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameScene extends JPanel{
    private final int spriteSize = 32;
    private Map<String, Image> sprites;
    public GameScene() {
        sprites = new HashMap<>();
        try {
            sprites.put("Player", ImageIO.read(new File("player.jpg")));
            sprites.put("WeakWall", ImageIO.read(new File("weak.jpg")));
            sprites.put("StrongWall", ImageIO.read(new File("strong.jpg")));
            sprites.put("Bomb", ImageIO.read(new File("bomb.jpg")));
            sprites.put("Enemy", ImageIO.read(new File("enemy.jpg")));
            sprites.put("Explosion", ImageIO.read(new File("explosion.jpg")));
            sprites.put("ExitBlock", ImageIO.read(new File("exit.jpg")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setSize(512, 517);
        setVisible(false);
    }
    public void drawScene(GameContext gameContext) {
        super.paintComponent(getGraphics());
        //drawing Player
        getGraphics().drawImage(sprites.get("Player"), gameContext.getPlayer().getX(), gameContext.getPlayer().getY(), this);
        //drawing weak walls
        for (GameObject weakWall : gameContext.getWeakWalls()) {
            getGraphics().drawImage(sprites.get("WeakWall"), weakWall.getX(), weakWall.getY(), this);
        }
        //drawing strong walls
        for (GameObject strongWall : gameContext.getStrongWalls()) {
            getGraphics().drawImage(sprites.get("StrongWall"), strongWall.getX(), strongWall.getY(), this);
        }
        //drawing bombs
        for (GameObject bomb : gameContext.getBombs()) {
            getGraphics().drawImage(sprites.get("Bomb"), bomb.getX(), bomb.getY(), this);
        }
        //drawing explosions
        for (GameObject explosion : gameContext.getExplosions()) {
            getGraphics().drawImage(sprites.get("Explosion"), explosion.getX(), explosion.getY(), this);
            getGraphics().drawImage(sprites.get("Explosion"), explosion.getX() + spriteSize, explosion.getY(), this);
            getGraphics().drawImage(sprites.get("Explosion"), explosion.getX() + 2*spriteSize, explosion.getY(), this);
            getGraphics().drawImage(sprites.get("Explosion"), explosion.getX() - spriteSize, explosion.getY(), this);
            getGraphics().drawImage(sprites.get("Explosion"), explosion.getX() - 2*spriteSize, explosion.getY(), this);
            getGraphics().drawImage(sprites.get("Explosion"), explosion.getX(), explosion.getY() + spriteSize, this);
            getGraphics().drawImage(sprites.get("Explosion"), explosion.getX(), explosion.getY() + 2*spriteSize, this);
            getGraphics().drawImage(sprites.get("Explosion"), explosion.getX(), explosion.getY() - spriteSize, this);
            getGraphics().drawImage(sprites.get("Explosion"), explosion.getX(), explosion.getY() - 2*spriteSize, this);
        }
        //drawing enemies
        for (GameObject enemy : gameContext.getEnemies()) {
            getGraphics().drawImage(sprites.get("Enemy"), enemy.getX(), enemy.getY(), this);
        }
        //drawing exitBlock
        if (!gameContext.getWeakWalls().contains(gameContext.getExitBlock().getOverlayedWall())) {
            getGraphics().drawImage(sprites.get("ExitBlock"), gameContext.getExitBlock().getX(), gameContext.getExitBlock().getY(), this);
        }
    }
}