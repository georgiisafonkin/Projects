package ru.nsu.gsafonkin.bomberman.model.objects;

import ru.nsu.gsafonkin.bomberman.model.GameContext;

import java.util.List;

public class Enemy extends Creature{
    public Enemy(int x, int y, GameContext gameContext) {
        super(x, y, gameContext);
    }

    @Override
    public int getHP() {
        return super.getHP();
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
        for (GameObject weakWall: gameContext.getWeakWalls()) {
            if (this.x + delta == weakWall.getX() && y == weakWall.getY()) {
                collisionFlag = 1;
            }
        }
        for (GameObject strongWall : gameContext.getStrongWalls()) {
            if (this.x + delta == strongWall.getX() && y == strongWall.getY()) {
                collisionFlag = 1;
            }
        }

        if (this.x + delta >= gameContext.getWidth()) {
            collisionFlag = 1;
        }

        if (collisionFlag == 0) {
            x += delta;
            System.out.println("enemy move_right()" + x);
        } else {
            System.out.println("enemy can't move right because of collision");
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
        for (GameObject weakWall: gameContext.getWeakWalls()) {
            if (this.x - delta == weakWall.getX() && y == weakWall.getY()) {
                collisionFlag = 1;
            }
        }
        for (GameObject strongWall : gameContext.getStrongWalls()) {
            if (this.x - delta == strongWall.getX() && y == strongWall.getY()) {
                collisionFlag = 1;
            }
        }

        if (this.x - delta < 0) {
            collisionFlag = 1;
        }

        if (collisionFlag == 0) {
            x -= delta;
            System.out.println("enemy move_left()" + x);
        } else {
            System.out.println("enemy can't move left because of collision");
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
        for (GameObject weakWall: gameContext.getWeakWalls()) {
            if (this.y - delta == weakWall.getY() && x == weakWall.getX()) {
                collisionFlag = 1;
            }
        }
        for (GameObject strongWall : gameContext.getStrongWalls()) {
            if (this.y - delta == strongWall.getY() && x == strongWall.getX()) {
                collisionFlag = 1;
            }
        }
        if (this.y - delta < 0) {
            collisionFlag = 1;
        }

        if (collisionFlag == 0) {
            y -= delta;
            System.out.println("enemy move_up()" + y);
        } else {
            System.out.println("enemy can't move up because of collision");
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
        for (GameObject weakWall: gameContext.getWeakWalls()) {
            if (this.y + delta == weakWall.getY() && x == weakWall.getX()) {
                collisionFlag = 1;
            }
        }
        for (GameObject strongWall : gameContext.getStrongWalls()) {
            if (this.y + delta == strongWall.getY() && x == strongWall.getX()) {
                collisionFlag = 1;
            }
        }
        if (this.y + delta >= gameContext.getHeight() - delta) {
            collisionFlag = 1;
        }
        if (collisionFlag == 0) {
            y += delta;
            System.out.println("enemy move_down()" + y);
        } else {
            System.out.println("enemy can't move down because of collision");
        }
    }

    private int getDirection(List<GameObject> gameObjectList) {
        for (GameObject gameObject : gameObjectList) {
            if (y == gameObject.getY() && (x - delta == gameObject.getX() || x - 2*delta == gameObject.getX() || x - 3*delta == gameObject.getX())) {
                return 0; //move_right
            }
            else if (y == gameObject.getY() && (x + delta == gameObject.getX() || x + 2*delta == gameObject.getX() || x + 3*delta == gameObject.getX())) {
                return 1; //move_left
            }
            else if (x == gameObject.getX() && (y + delta == gameObject.getY() || y + 2*delta == gameObject.getY() || y + 3*delta == gameObject.getY())) {
                return 2; //move_up
            }
            else if (x == gameObject.getX() && (y - delta == gameObject.getY() || y - 2*delta == gameObject.getY() || y - 3*delta == gameObject.getY())) {
                return 3; //move_down
            }
        }
        return gameContext.getRandomGenerator().nextInt(4);
    }
    @Override
    public void doSomeMovement() {
        int direction = getDirection(gameContext.getBombs());
        switch(direction) {
            case 0: //move_right case
                move_right();
                break;
            case 1: //move_left case
                move_left();
                break;
            case 2: //move_up case
                move_up();
                break;
            case 3: //move_down case
                move_down();
                break;
        }
    }
}