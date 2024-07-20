package ru.nsu.gsafonkin.bomberman.model.objects;

public class Bomb extends GameObject{
    //Player owner;
    public Bomb(Player owner) {
        super(owner.getX(), owner.getY(), owner.gameContext);
        //this.owner = owner;
        this.source = owner;
    }
    @Override
    public boolean isDestroyed(GameObject reason) {
        if (System.currentTimeMillis() - reason.bornTime >= 5000) {
            return true;
        }
        else {
            return false;
        }
    }
//    public Player getOwner() {
//        return owner;
//    }
}
