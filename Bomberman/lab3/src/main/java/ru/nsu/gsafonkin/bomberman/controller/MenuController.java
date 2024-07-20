package ru.nsu.gsafonkin.bomberman.controller;

import ru.nsu.gsafonkin.bomberman.view.swing.GameMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuController implements ActionListener {
    private GameMenu menu;
    public MenuController(GameMenu menu) {
        super();
        this.menu = menu;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case ("Start"):
                menu.getView().startGame();
                menu.getView().getScene().setVisible(true);
                menu.getView().getScene().grabFocus();
                menu.setVisible(false);
                menu.getView().remove(menu);
                break;
            case ("Score"):
                break;

        }
    }
}
