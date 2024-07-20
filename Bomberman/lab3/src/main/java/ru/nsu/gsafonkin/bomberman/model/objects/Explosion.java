package ru.nsu.gsafonkin.bomberman.model.objects;

public class Explosion extends GameObject{
    public Explosion(GameObject source) {
        super(source.getX(), source.getY(), source.gameContext);
        this.source = source;
        x = source.getX();
        y = source.getY();
    }
    @Override
    public boolean isDestroyed(GameObject reason) {
        if (System.currentTimeMillis() - reason.bornTime < 1000) {
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public int getX() {
        return super.getX();
    }

    @Override
    public int getY() {
        return super.getY();
    }
    public GameObject getSource() {
        return source;
    }
}
