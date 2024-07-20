package ru.nsu.gsafonkin.lab4.model.suppliers;

import ru.nsu.gsafonkin.lab4.model.Model;
import ru.nsu.gsafonkin.lab4.model.Storage;
import ru.nsu.gsafonkin.lab4.model.items.Body;

import java.util.concurrent.BlockingQueue;

import static java.lang.Thread.sleep;

public class BodySupplier implements ISupplier{
    private final Model model;
    private Storage<Body> bodiesStorage;
    public BodySupplier(Storage<Body> bodiesStorage, Model model) {
        this.model = model;
        this.bodiesStorage = bodiesStorage;
    }

    @Override
    public void run() {
        while (true) {
            if (Thread.currentThread().isInterrupted() || null == bodiesStorage) {
                break;
            }
            try {
                bodiesStorage.put(new Body());
                sleep(model.getBodySupplyTimeout() * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
