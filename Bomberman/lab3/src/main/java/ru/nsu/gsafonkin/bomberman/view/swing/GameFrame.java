package ru.nsu.gsafonkin.bomberman.view.swing;

import ru.nsu.gsafonkin.bomberman.controller.GameController;
import ru.nsu.gsafonkin.bomberman.model.IModelListener;
import ru.nsu.gsafonkin.bomberman.model.Model;
import ru.nsu.gsafonkin.bomberman.model.State;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

public class GameFrame extends JFrame implements IModelListener {
    private Model model;
    private GameScene scene;
    private GameMenu menu;
    private GameoverScreen gameoverScreen;
    private VictoryScreen victoryScreen;
    public GameFrame(Model model) {
        super("Bomberman");
        this.model = model;
        scene = new GameScene();
        menu = new GameMenu(this);
        gameoverScreen = new GameoverScreen();
        victoryScreen = new VictoryScreen();
        add(menu);
        add(scene);
        addKeyListener(new GameController(model));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(model.getWidth(), model.getHeight());
        setVisible(true);
        setResizable(false);
    }

    public void addKeyListener(KeyListener keyListener) {
        super.addKeyListener(keyListener);
        scene.addKeyListener(keyListener);
    }

    @Override
    public void onModelChanged() {
        SwingUtilities.invokeLater(()-> {
            if (model.getGameState() == State.GAMEPLAY) {
                scene.drawScene(model.getGameContext());
            }
            if (model.getGameState() == State.GAMEOVER) {
                remove(scene);
                add(gameoverScreen);
                gameoverScreen.setVisible(true);
            }
            if (model.getGameState() == State.VICTORY) {
                remove(scene);
                add(victoryScreen);
                victoryScreen.setVisible(true);
            }
        });
    }
    @Override
    public void paintComponents(Graphics g) {
        if (model.getGameState() == State.GAMEPLAY) {
            scene.drawScene(model.getGameContext());
        }
//        scene.drawScene(model.getGameContext());
    }
    public Model getModel() {
        return model;
    }
    public void setModel(Model model) {
        this.model = model;
    }
    public GameScene getScene() {
        return scene;
    }
    public GameMenu getMenu() {
        return menu;
    }
    public void startGame() {
        model.start();
    }
}