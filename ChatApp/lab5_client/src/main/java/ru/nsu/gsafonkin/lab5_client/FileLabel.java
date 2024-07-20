package ru.nsu.gsafonkin.lab5_client;

import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;

public class FileLabel extends Label {
    private String name;
    private int id;
    public FileLabel(String name, int id) {
        super(name);
        this.name = name;
        this.id = id;
//        this.setOnMouseClicked();
    }
}
