package ru.nsu.gsafonkin.bomberman.model;

import ru.nsu.gsafonkin.bomberman.model.objects.Explosion;
import ru.nsu.gsafonkin.bomberman.model.objects.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Ticker extends Thread{

    private List<GameObject> bombsToDetonate;
    private List<GameObject> explosionsToExplode;
    private List<GameObject> weakWallsToExplode;
    private List<GameObject> enemiesToBeKilled;
    private IModel model;

    public Ticker(IModel model) {
        bombsToDetonate = new ArrayList<>();
        explosionsToExplode = new ArrayList<>();
        enemiesToBeKilled = new ArrayList<>();
        weakWallsToExplode = new ArrayList<>();
        this.model = model;
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            bombsToDetonate.clear();
            explosionsToExplode.clear();
            weakWallsToExplode.clear();
            enemiesToBeKilled.clear();
            for (GameObject bomb : model.getGameContext().getBombs()) {
                if (bomb.isDestroyed(bomb)) {
                    bombsToDetonate.add(bomb);
                }
            }
            if (bombsToDetonate.size() > 0) {
                model.removeBombs(bombsToDetonate);
            }

            for (GameObject weakWall : model.getGameContext().getWeakWalls()) {
                for (GameObject explosion : model.getGameContext().getExplosions()) {
                    if (weakWall.isDestroyed(explosion)) {
                        weakWallsToExplode.add(weakWall);
                    }
                }
            }
            if (weakWallsToExplode.size() > 0) {
                model.getGameContext().removeWeakWalls(weakWallsToExplode);
            }

            for (GameObject explosion: model.getGameContext().getExplosions()) {
                if (explosion.isDestroyed(explosion)) {
                    explosionsToExplode.add(explosion);
                }
                for (GameObject enemy : model.getGameContext().getEnemies()) {
                    if (enemy.isDestroyed(explosion)) {
                        enemiesToBeKilled.add(enemy);
                        model.getGameContext().setScore(model.getGameContext().getScore() + 1);
                    }
                }
            }
            if (explosionsToExplode.size() > 0) {
                model.removeExplosions(explosionsToExplode);
            }
            if (enemiesToBeKilled.size() > 0) {
                model.getGameContext().killExplodedEnemies(enemiesToBeKilled);
            }

            if (System.currentTimeMillis() - model.getLastEnemiesMovementTime() >= model.getEnemiesMovementTimeout()) {
                model.moveEnemies();
            }
            for (GameObject explosion : model.getGameContext().getExplosions()) {
                if (model.getGameContext().getPlayer().isDestroyed(explosion)) {
                    //player.hp -= 100;
                    model.setGameState(ru.nsu.gsafonkin.bomberman.model.State.GAMEOVER);
                    break;
                }
            }
            if (model.getGameContext().getPlayer().isHurtedByEnemy()) {
                //player.hp -= 100;
                model.setGameState(ru.nsu.gsafonkin.bomberman.model.State.GAMEOVER);
            }
            if (model.checkVictory() && model.getGameState() == ru.nsu.gsafonkin.bomberman.model.State.GAMEPLAY) {
                model.setGameState(ru.nsu.gsafonkin.bomberman.model.State.VICTORY);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (model.getGameState() == ru.nsu.gsafonkin.bomberman.model.State.VICTORY ||
                    model.getGameState() == ru.nsu.gsafonkin.bomberman.model.State.GAMEOVER) {
                this.interrupt();
            }
        }
    }
}