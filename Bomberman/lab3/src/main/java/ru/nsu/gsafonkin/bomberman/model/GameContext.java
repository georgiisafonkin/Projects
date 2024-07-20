package ru.nsu.gsafonkin.bomberman.model;

import ru.nsu.gsafonkin.bomberman.model.objects.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class GameContext {
    private int score;
    private String playerNickname;
    Random randomGenerator = new Random();

    public int getHeight() {
        return height;
    }

    private int height;

    public int getWidth() {
        return width;
    }

    private int width;
    private Player player;
    private ExitBlock exitBlock;
    private List<GameObject> bombs;
    private List<GameObject> explosions;
    private List<Creature> enemies;
    private List<GameObject> weakWalls;
    private List<GameObject> strongWalls;
    private int weakWallsNumber = 12;
    private int enemiesNumber = 12;
    private Point generateRandomCoordinates() {
        return new Point(randomGenerator.nextInt(width/GameObject.getDelta() - 1),
                randomGenerator.nextInt(height/GameObject.getDelta() - 1));
    }
    private <T extends GameObject> void generateGameObjects(List<T> objectsToGenerate, String className, int numberOfObjectsToGenerate) {
        Point randomPoint = generateRandomCoordinates();
        for(int i = 0; i < numberOfObjectsToGenerate; ++i) {
            int overlayFlag = 1;
            while (overlayFlag != 0) {
                overlayFlag = 0;
                randomPoint = generateRandomCoordinates();
                for (GameObject strongWall : strongWalls) {
                    if ((randomPoint.x == strongWall.getX() / GameObject.getDelta() && randomPoint.y == strongWall.getY() / GameObject.getDelta())) {
                        overlayFlag = 1;
                        break;
                    }
                }
                for (GameObject weakWall : weakWalls) {
                    if ((randomPoint.x == weakWall.getX() / GameObject.getDelta() && randomPoint.y == weakWall.getY() / GameObject.getDelta())) {
                        overlayFlag = 1;
                        break;
                    }
                }
                for (GameObject enemy : enemies) {
                    if ((randomPoint.x == enemy.getX() / GameObject.getDelta() && randomPoint.y == enemy.getY() / GameObject.getDelta())) {
                        overlayFlag = 1;
                        break;
                    }
                }
            }
            T newObject;
            try {
                newObject = (T)Class.forName(className)
                        .getConstructor(int.class, int.class, GameContext.class)
                        .newInstance(GameObject.getDelta() * randomPoint.x, GameObject.getDelta() * randomPoint.y, this);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            objectsToGenerate.add(newObject);
        }
    }
    private void generateScene() {
        //Strong walls generation
        for (int i = 0; i < height/GameObject.getDelta(); i+=2) {
            for (int j = 0; j < width/GameObject.getDelta(); j+=2) {
                strongWalls.add(new StrongWall(GameObject.getDelta()*i, GameObject.getDelta()*j, this));
            }
        }
        //weakWallsGeneration
        generateGameObjects(weakWalls, "ru.nsu.gsafonkin.bomberman.model.objects." + "WeakWall", weakWallsNumber);
        //enemies generation
        generateGameObjects(enemies, "ru.nsu.gsafonkin.bomberman.model.objects." + "Enemy", enemiesNumber);
    }
    public GameContext(int width, int height) {
        score = 0;
        playerNickname = null;
        this.width = width  ;
        this.height = height;
        player = new Player(this);
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        enemies = new ArrayList<>();
        weakWalls = new ArrayList<>();
        strongWalls = new ArrayList<>();
        generateScene();
        int index = randomGenerator.nextInt(weakWalls.size());
        exitBlock = new ExitBlock(weakWalls.get(index), weakWalls.get(index).getX(), weakWalls.get(index).getY(), this);
    }
    public Player getPlayer() {return player;}
    public List<GameObject> getBombs() {return bombs;}
    public List<Creature> getEnemies() {return enemies;}
    public List<GameObject> getWeakWalls() {return weakWalls;}
    public List<GameObject> getStrongWalls() {return strongWalls;}
    public Random getRandomGenerator() {return randomGenerator;}
    public ExitBlock getExitBlock() {
        return exitBlock;
    }
    public void removeBombs(List<GameObject> bombsToExplode) {
        for (GameObject bomb : bombsToExplode) {
            explosions.add(new Explosion(bomb));
            bombs.remove(bomb);
            System.out.println("Bomb was detonated. Explosion was created.\n");
        }
    }
    public void removeExplosions(List<GameObject> ToExplode) {
        for (GameObject explosion : ToExplode) {
            explosions.remove(explosion);
            System.out.println("Explosion was exploded.\n");
        }
    }
    public void removeWeakWalls(List<GameObject> weakWallsToRemove) {
        for (GameObject weakWallToRemove : weakWallsToRemove) {
            weakWalls.remove(weakWallToRemove);
        }
    }
    public void killExplodedEnemies(List<GameObject> enemiesToBeKilled) {
        for (GameObject enemyToBeKilled : enemiesToBeKilled) {
            enemies.remove(enemyToBeKilled);
        }
    }
    public List<GameObject> getExplosions() {
        return explosions;
    }
    public void moveEnemies() {
        for (Creature enemy : enemies) {
            enemy.doSomeMovement();
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public void setPlayerNickname(String playerNickname) {
        this.playerNickname = playerNickname;
    }

}
