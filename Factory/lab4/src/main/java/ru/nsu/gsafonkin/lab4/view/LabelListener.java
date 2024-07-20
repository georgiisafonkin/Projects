package ru.nsu.gsafonkin.lab4.view;

import javafx.application.Platform;
import javafx.scene.control.Label;
import ru.nsu.gsafonkin.lab4.model.IStorageListener;
import ru.nsu.gsafonkin.lab4.model.Storage;

public class LabelListener extends Label implements IStorageListener {
    private Storage storage;
    private String initialString;
    public LabelListener(Storage storage, String initialString) {
        super(initialString);
        this.storage = storage;
        this.initialString = initialString;
    }

    @Override
    public void onStorageChanges() {
        Platform.runLater(() -> {
            this.setText(initialString + "CUR - " + storage.getSize() + "/" + storage.getCapacity() + " TOTAL - " + storage.getProductProduced());
        });
    }
}
