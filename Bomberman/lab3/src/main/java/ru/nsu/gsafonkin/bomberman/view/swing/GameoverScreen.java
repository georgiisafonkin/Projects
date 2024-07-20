package ru.nsu.gsafonkin.bomberman.view.swing;

import javax.swing.*;
import java.awt.*;

public class GameoverScreen extends JPanel {
    private JLabel deathLabel;
    public GameoverScreen() {
        deathLabel = new JLabel("You are dead! Game is over.");
        add(deathLabel);
        setVisible(false);
    }
}
