package ru.nsu.gsafonkin.bomberman.controller;

import ru.nsu.gsafonkin.bomberman.model.Command;
import ru.nsu.gsafonkin.bomberman.model.Model;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController extends KeyAdapter {
    private Model model;

    public GameController(Model model) {
        this.model = model;
    }
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case (KeyEvent.VK_UP):
                model.setChangesByUser(Command.UP);
                break;
            case (KeyEvent.VK_DOWN):
                model.setChangesByUser(Command.DOWN);
                break;
            case (KeyEvent.VK_LEFT):
                model.setChangesByUser(Command.LEFT);
                break;
            case (KeyEvent.VK_RIGHT):
                model.setChangesByUser(Command.RIGHT);
                break;
            case (KeyEvent.VK_SPACE):
                model.setChangesByUser(Command.BOMB);
                break;
            case (KeyEvent.VK_S):
                model.setChangesByUser(Command.STATISTICS);
                break;
        }
    }
}
