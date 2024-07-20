package ru.nsu.gsafonkin.bomberman.model.objects;

public interface IGameObject {
    int getX();
    int getY();
    int getFrame_width();
    int getFrame_height();
    void move_right();
    void move_left();
    void move_up();
    void move_down();
    long getBornTime();
    boolean isDestroyed (GameObject reason);
    void doSomeMovement();
    GameObject getSource();
    void setSource(GameObject source);
}
