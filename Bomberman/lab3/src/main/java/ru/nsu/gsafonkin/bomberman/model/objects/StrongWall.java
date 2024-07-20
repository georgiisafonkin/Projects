package ru.nsu.gsafonkin.bomberman.model.objects;

import ru.nsu.gsafonkin.bomberman.model.GameContext;

public class StrongWall extends GameObject{
    public StrongWall(int x, int y, GameContext gameContext) {
        super(x,y,gameContext);
    }

    @Override
    public boolean isDestroyed(GameObject reason) {
        return false;
    }
}
