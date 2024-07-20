package ru.nsu.gsafonkin.bomberman.model.objects;

import ru.nsu.gsafonkin.bomberman.model.GameContext;

import java.util.ArrayList;
import java.util.List;

public class Player extends Creature{
    public Player(GameContext gameContext) {
        super(gameContext);
    }

    public Player() {
        x = 0;
        y = 0;
        hp = 100;
    }

    @Override
    public void doSomeMovement() {

    }

    @Override
    public void move_right() {
        collisionFlag = 0;
        for (GameObject weakWall : gameContext.getWeakWalls()) {
            if (weakWall.getX() == x + delta && weakWall.getY() == y) {
                collisionFlag = 1;
            }
        }
        for (GameObject bomb : gameContext.getBombs()) {
            if (bomb.getX() == x + delta && bomb.getY() == y) {
                collisionFlag = 1;
            }
        }
        for (GameObject strongWall : gameContext.getStrongWalls()) {
            if (strongWall.getX() == x + delta && strongWall.getY() == y) {
                collisionFlag = 1;
            }
        }
        if (collisionFlag == 0) {
            x += delta;
            System.out.println("move_right()" + x);
        } else {
            System.out.println("can't move because of collision");
        }
    }
    @Override
    public void move_left() {
        collisionFlag = 0;
        for (GameObject weakWall : gameContext.getWeakWalls()) {
            if (weakWall.getX() == x - delta && weakWall.getY() == y) {
                collisionFlag = 1;
            }
        }
        for (GameObject bomb : gameContext.getBombs()) {
            if (bomb.getX() == x - delta && bomb.getY() == y) {
                collisionFlag = 1;
            }
        }
        for (GameObject strongWall : gameContext.getStrongWalls()) {
            if (strongWall.getX() == x - delta && strongWall.getY() == y) {
                collisionFlag = 1;
            }
        }
        if (collisionFlag == 0) {
            x -= delta;
            System.out.println("move_left()" + x);
        } else {
            System.out.println("can't move because of collision");
        }
    }
    @Override
    public void move_up() {
        collisionFlag = 0;
        for (GameObject weakWall : gameContext.getWeakWalls()) {
            if (weakWall.getY() == y - delta && weakWall.getX() == x) {
                collisionFlag = 1;
            }
        }
        for (GameObject bomb : gameContext.getBombs()) {
            if (bomb.getY() == y - delta && bomb.getX() == x) {
                collisionFlag = 1;
            }
        }
        for (GameObject strongWall : gameContext.getStrongWalls()) {
            if (strongWall.getY() == y - delta && strongWall.getX() == x) {
                collisionFlag = 1;
            }
        }
        if (collisionFlag == 0) {
            y -= delta;
            System.out.println("move_up()" + y);
        } else {
            System.out.println("can't move because of collision");
        }
    }
    @Override
    public void move_down() {
        collisionFlag = 0;
        for (GameObject weakWall : gameContext.getWeakWalls()) {
            if (weakWall.getY() == y + delta && weakWall.getX() == x) {
                collisionFlag = 1;
            }
        }
        for (GameObject bomb : gameContext.getBombs()) {
            if (bomb.getY() == y + delta && bomb.getX() == x) {
                collisionFlag = 1;
            }
        }
        for (GameObject strongWall : gameContext.getStrongWalls()) {
            if (strongWall.getY() == this.y + delta && strongWall.getX() == x) {
                collisionFlag = 1;
            }
        }
        if (collisionFlag == 0) {
            y += delta;
            System.out.println("move_down()" + y);
        } else {
            System.out.println("can't move because of collision");
        }
    }

    public boolean isHurtedByEnemy() {
        for (Creature enemy : gameContext.getEnemies()) {
            if (x == enemy.getX() && y == enemy.getY()) {
                return true;
            }
        }
        return false;
    }
}