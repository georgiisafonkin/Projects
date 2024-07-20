package ru.nsu.gsafonkin.bomberman.model.objects;

import ru.nsu.gsafonkin.bomberman.model.GameContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Creature extends GameObject {
    protected int hp;
    protected int collisionFlag = 0;
    public Creature(GameContext gameContext) {
        super(0,0,gameContext);
    }
    public Creature(int x, int y, GameContext gameContext) {
        super(x,y,gameContext);
        hp = 100;
    }
    public Creature() {
        x = 0;
        y = 0;
        gameContext = null;
    }
    public int getHP() {
        return hp;
    }

//    @Override
//    public boolean isDestroyed(GameObject reason) {
//        if (System.currentTimeMillis() - reason.bornTime >= 1000) {
//            if (
//                    (reason.getX() + this.getDelta() == this.getX() && reason.getY() == this.getY()) ||
//                            (reason.getX() == this.getX() && reason.getY() + this.getDelta() == this.getY()) ||
//                            (reason.getX() - this.getDelta() == this.getX() && reason.getY() == this.getY()) ||
//                            (reason.getX() == this.getX() && reason.getY() - this.getDelta() == this.getY())){
//                return true;
//            }
//            List<Boolean> isThereNeighbours = new ArrayList<Boolean>(); //0 = right, 1 = left, 2 = up, 3 = down
//            for (int i = 0; i < 4; ++i) {
//                isThereNeighbours.add(false);
//            }
//            for (GameObject weakWall : gameContext.getWeakWalls()) {
//                if (this != weakWall) {
//                    if (this.x + this.getDelta() == weakWall.x && this.y == weakWall.y) {
//                        isThereNeighbours.set(0, true);
//                    }
//                    else if (this.x - this.getDelta() == weakWall.x && this.y == weakWall.y) {
//                        isThereNeighbours.set(1, true);
//                    }
//                    else if (this.y - this.getDelta() == weakWall.y && this.x == weakWall.x) {
//                        isThereNeighbours.set(2, true);
//                    }
//                    else if (this.y + this.getDelta() == weakWall.y && this.x == weakWall.x) {
//                        isThereNeighbours.set(3, true);
//                    }
//                }
//            }
//            for (GameObject strongWall : gameContext.getStrongWalls()) {
//                if (this.x + this.getDelta() == strongWall.x && this.y == strongWall.y) {
//                    isThereNeighbours.set(0, true);
//                }
//                else if (this.x - this.getDelta() == strongWall.x && this.y == strongWall.y) {
//                    isThereNeighbours.set(1, true);
//                }
//                else if (this.y - this.getDelta() == strongWall.y && this.x == strongWall.x) {
//                    isThereNeighbours.set(2, true);
//                }
//                else if (this.y + this.getDelta() == strongWall.y && this.x == strongWall.x) {
//                    isThereNeighbours.set(3, true);
//                }
//            }
//            //0 = right, 1 = left, 2 = up, 3 = down
//            if ((reason.getX() == this.getX() && reason.getY() + 2*this.getDelta() == this.getY() && isThereNeighbours.get(2) == false) ||
//                    (reason.getX() == this.getX() && reason.getY() - 2*this.getDelta() == this.getY() && isThereNeighbours.get(3) == false) ||
//                    (reason.getY() == this.getY() && reason.getX() + 2*this.getDelta() == this.getX() && isThereNeighbours.get(1) == false) ||
//                    (reason.getY() == this.getY() && reason.getX() - 2*this.getDelta() == this.getX() && isThereNeighbours.get(0) == false))
//            {
//                return true;
//            }
//            else {
//                return false;
//            }
//        }
//        else {
//            return false;
//        }
//    }
}
