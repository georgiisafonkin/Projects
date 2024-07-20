package ru.nsu.gsafonkin.bomberman.model;

import ru.nsu.gsafonkin.bomberman.model.objects.Bomb;
import ru.nsu.gsafonkin.bomberman.model.objects.GameObject;

import java.util.List;

public class Model implements IModel{
    private State gameState = State.MENU;
    private int height;
    private int width;
    private final long bombsDropTimeout = 5000;
    private final long enemiesMovementTimeout = 1000;
    private long lastDropTime = 0;
    private long lastEnemiesMovementTime = System.currentTimeMillis();
    private IModelListener listener;
    private GameContext gameContext;
    private Thread thread;
    public Model() {
        width = 512;
        height = 512;
        gameContext = new GameContext(width, height);
        thread = new Ticker(this);
        //thread.start();
    }
    public Model(IModelListener listener) {
        thread = new Ticker(this);
        gameContext = new GameContext(width, height);
        this.listener = listener;
    }

    private void notifyModelChanges() {
        if (null != listener) {
            listener.onModelChanged();
        }
    }
    public long getLastEnemiesMovementTime() {
        return lastEnemiesMovementTime;
    }
    @Override
    public void setChangesByUser(Command cmd) {
        switch (cmd) {
            case UP:
                gameContext.getPlayer().move_up();
                break;
            case DOWN:
                gameContext.getPlayer().move_down();
                break;
            case LEFT:
                gameContext.getPlayer().move_left();
                break;
            case RIGHT:
                gameContext.getPlayer().move_right();
                break;
            case BOMB:
                if (System.currentTimeMillis() - lastDropTime >= bombsDropTimeout) {
                    gameContext.getBombs().add(
                            new Bomb(gameContext.getPlayer()));
                    System.out.println("bomb was created");
                    lastDropTime = System.currentTimeMillis();
                }
                break;
            case STATISTICS:
                System.out.println(gameState);
                System.out.println(gameContext.getScore());
//                System.out.println("exit x = " + gameContext.getExitBlock().getX() + "\nexit y = " + gameContext.getExitBlock().getY() + "\n");
//                System.out.println(gameContext.getWeakWalls().size());
//                System.out.println(gameContext.getEnemies().size());
//                System.out.println(gameContext.getBombs().size());
//                System.out.println(listener.getBombsViews().size());
//                System.out.println(dropState);
                break;
        }
        notifyModelChanges();
    }

    @Override
    public void setGameState(State gameState) {
        this.gameState = gameState;
    }
    @Override
    public State getGameState() {
        return gameState;
    }
    @Override
    public long getBombsDropTimeout() {
        return bombsDropTimeout;
    }
    @Override
    public long getLastDropTime() {
        return lastDropTime;
    }
    @Override
    public void setLastDropTime(long lastDropTime) {
        this.lastDropTime = lastDropTime;
    }
    @Override
    public GameContext getGameContext() {return gameContext;}
    public void setListener(IModelListener listener) { //TODO SYNCHRONIZED???
        this.listener = listener;
    }
    @Override
    public IModelListener getListener() { return listener; }
    @Override
    public void removeBombs(List<GameObject> bombsToExplode) {
        gameContext.removeBombs(bombsToExplode);
        notifyModelChanges();
    }
    @Override
    public void removeExplosions(List<GameObject> ToExplode) {
        gameContext.removeExplosions(ToExplode);
        notifyModelChanges();
    }

    @Override
    public void checkCollisions() {

    }
    @Override
    public void moveEnemies() {
        gameContext.moveEnemies();
        lastEnemiesMovementTime = System.currentTimeMillis();
        notifyModelChanges();
    }
    @Override
    public long getEnemiesMovementTimeout() {
        return enemiesMovementTimeout;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public boolean checkVictory() {
        if (gameContext.getEnemies().size() == 0 &&
                gameContext.getPlayer().getX() == gameContext.getExitBlock().getX() &&
                gameContext.getPlayer().getY() == gameContext.getExitBlock().getY()) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void start() {
        setGameState(State.GAMEPLAY);
        thread.start();
    }
}