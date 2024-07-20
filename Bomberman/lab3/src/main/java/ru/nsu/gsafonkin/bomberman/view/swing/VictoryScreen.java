package ru.nsu.gsafonkin.bomberman.view.swing;

import javax.swing.*;

public class VictoryScreen extends JPanel {
    private JLabel victoryLabel;
    public VictoryScreen() {
        victoryLabel = new JLabel("You won. Congratulations!");
        add(victoryLabel);
        setVisible(false);
    }
}
