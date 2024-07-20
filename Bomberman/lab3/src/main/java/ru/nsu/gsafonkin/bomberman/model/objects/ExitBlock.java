package ru.nsu.gsafonkin.bomberman.model.objects;

import ru.nsu.gsafonkin.bomberman.model.GameContext;

public class ExitBlock extends GameObject{
    GameObject overlayedWall;
    public ExitBlock(GameObject overlayedWall, int x, int y, GameContext gameContext) {
        super(x,y,gameContext);
        this.overlayedWall = overlayedWall;
    }
    public GameObject getOverlayedWall() {
        return overlayedWall;
    }
}
