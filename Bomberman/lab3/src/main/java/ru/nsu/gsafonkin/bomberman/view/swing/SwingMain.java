package ru.nsu.gsafonkin.bomberman.view.swing;

import java.io.IOException;

import ru.nsu.gsafonkin.bomberman.model.Model;

import javax.swing.*;

public class SwingMain {
    public static void main(String[] args) throws IOException {
        Model model = new Model();
        SwingUtilities.invokeLater(() -> {
            GameFrame gf = new GameFrame(model);
            model.setListener(gf);
            gf.setVisible(true);
        });
    }
}