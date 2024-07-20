package ru.nsu.gsafonkin.bomberman.model;

import ru.nsu.gsafonkin.bomberman.model.objects.GameObject;

import java.util.List;

public interface IModel {
    void setGameState(State gameState);
    State getGameState();
    long getBombsDropTimeout();
    long getLastDropTime();
    void setLastDropTime(long lastDropTime);
    void setChangesByUser(Command cmd);
    GameContext getGameContext();
    IModelListener getListener();
    void removeBombs(List<GameObject> bombsToExplode);
    void removeExplosions(List<GameObject> ToExplode);
    void checkCollisions();
    long getEnemiesMovementTimeout();
    long getLastEnemiesMovementTime();
    void moveEnemies();
    int getHeight();
    int getWidth();
    boolean checkVictory();
    void start();
}
