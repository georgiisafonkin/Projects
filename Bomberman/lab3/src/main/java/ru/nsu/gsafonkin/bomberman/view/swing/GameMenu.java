package ru.nsu.gsafonkin.bomberman.view.swing;

import ru.nsu.gsafonkin.bomberman.controller.MenuController;

import javax.swing.*;
import java.awt.*;

public class GameMenu extends JMenuBar {
    private GameFrame view;
    private JButton scoreTableButton;
    private JButton startGameButton;
    private JTextField nicknameField;

    public GameMenu(GameFrame view) {
        this.view = view;
        setLayout(new BorderLayout(0,0));
//        scoreTableButton = new JButton("Score");
        startGameButton = new JButton("Start");
//        setBounds(view.getWidth()/2, view.getHeight()/2, 128,64);
//        startGameButton.setPreferredSize(new Dimension());
//        startGameButton.setPreferredSize(new Dimension(200, 100));
//        startGameButton.setMinimumSize(new Dimension(200, 100));
//        startGameButton.setMaximumSize(new Dimension(200, 100));
//        nicknameField = new JTextField();
//        nicknameField.setPreferredSize(new Dimension(200, 20));
//        nicknameField.setMinimumSize(new Dimension(200, 20));
//        nicknameField.setMaximumSize(new Dimension(200, 20));
//        this.add(scoreTableButton);
        this.add(startGameButton);
//        this.add(nicknameField);
        setSize(512, 517);
        setVisible(true);
//        scoreTableButton.addActionListener(new MenuController(this));
        startGameButton.addActionListener(new MenuController(this));
    }
    public GameFrame getView() {
        return view;
    }
    //TODO add some Labels, buttons, text
}