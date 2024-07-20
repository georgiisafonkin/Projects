package ru.nsu.gsafonkin.bomberman.model.objects;

import ru.nsu.gsafonkin.bomberman.model.GameContext;

import java.util.ArrayList;
import java.util.List;

public class GameObject implements IGameObject {
    protected GameObject source;
    protected long bornTime;
    protected int x;
    protected int y;
    protected int frame_width;
    protected int frame_height;
    static protected final int delta = 32;
    protected GameContext gameContext;
    public GameObject() {}
    public GameObject(int x, int y, GameContext gameContext) {
        bornTime = System.currentTimeMillis();
        this.x = x;
        this.y = y;
        this.gameContext = gameContext;
        source = null;
    }
    public static int getDelta() {
        return delta;
    }
    @Override
    public int getX() {
        return x;
    }
    @Override
    public int getY() {
        return y;
    }
    @Override
    public int getFrame_height() {
        return frame_height;
    }

    @Override
    public void move_right() {

    }

    @Override
    public void move_left() {

    }

    @Override
    public void move_up() {

    }

    @Override
    public void move_down() {

    }

    @Override
    public int getFrame_width() {
        return frame_width;
    }

    @Override
    public boolean isDestroyed(GameObject reason) {
        if (System.currentTimeMillis() - reason.bornTime >= 1000) {
            if (
                    (reason.getX() + this.getDelta() == this.getX() && reason.getY() == this.getY()) ||
                            (reason.getX() == this.getX() && reason.getY() + this.getDelta() == this.getY()) ||
                            (reason.getX() - this.getDelta() == this.getX() && reason.getY() == this.getY()) ||
                            (reason.getX() == this.getX() && reason.getY() - this.getDelta() == this.getY())){
                return true;
            }
            List<Boolean> isThereNeighbours = new ArrayList<Boolean>(); //0 = right, 1 = left, 2 = up, 3 = down
            for (int i = 0; i < 4; ++i) {
                isThereNeighbours.add(false);
            }
            for (GameObject weakWall : gameContext.getWeakWalls()) {
                if (this != weakWall) {
                    if (this.x + this.getDelta() == weakWall.x && this.y == weakWall.y) {
                        isThereNeighbours.set(0, true);
                    }
                    else if (this.x - this.getDelta() == weakWall.x && this.y == weakWall.y) {
                        isThereNeighbours.set(1, true);
                    }
                    else if (this.y - this.getDelta() == weakWall.y && this.x == weakWall.x) {
                        isThereNeighbours.set(2, true);
                    }
                    else if (this.y + this.getDelta() == weakWall.y && this.x == weakWall.x) {
                        isThereNeighbours.set(3, true);
                    }
                }
            }
            for (GameObject strongWall : gameContext.getStrongWalls()) {
                if (this.x + this.getDelta() == strongWall.x && this.y == strongWall.y) {
                    isThereNeighbours.set(0, true);
                }
                else if (this.x - this.getDelta() == strongWall.x && this.y == strongWall.y) {
                    isThereNeighbours.set(1, true);
                }
                else if (this.y - this.getDelta() == strongWall.y && this.x == strongWall.x) {
                    isThereNeighbours.set(2, true);
                }
                else if (this.y + this.getDelta() == strongWall.y && this.x == strongWall.x) {
                    isThereNeighbours.set(3, true);
                }
            }
            //0 = right, 1 = left, 2 = up, 3 = down
            if ((reason.getX() == this.getX() && reason.getY() + 2*this.getDelta() == this.getY() && isThereNeighbours.get(2) == false) ||
                    (reason.getX() == this.getX() && reason.getY() - 2*this.getDelta() == this.getY() && isThereNeighbours.get(3) == false) ||
                    (reason.getY() == this.getY() && reason.getX() + 2*this.getDelta() == this.getX() && isThereNeighbours.get(1) == false) ||
                    (reason.getY() == this.getY() && reason.getX() - 2*this.getDelta() == this.getX() && isThereNeighbours.get(0) == false))
            {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    @Override
    public void doSomeMovement() {

    }

    @Override
    public long getBornTime() {
        return bornTime;
    }
    @Override
    public GameObject getSource() {
        return source;
    }
    @Override
    public void setSource(GameObject source) {
        this.source = source;
    }
}
