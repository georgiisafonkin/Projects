package ru.nsu.gsafonkin.lab4.model.suppliers;

import ru.nsu.gsafonkin.lab4.model.Model;
import ru.nsu.gsafonkin.lab4.model.Storage;
import ru.nsu.gsafonkin.lab4.model.items.Engine;

import java.util.concurrent.BlockingQueue;

import static java.lang.Thread.sleep;

public class EngineSupplier implements ISupplier{
    private final Model model;
    private Storage<Engine> engineStorage;
    public EngineSupplier(Storage<Engine> engineStorage, Model model) {
        this.model = model;
        this.engineStorage = engineStorage;
    }
    @Override
    public void run() {
        while(true) {
            if (Thread.currentThread().isInterrupted() || null == engineStorage) {
                break;
            }
            try {
                engineStorage.put(new Engine());
                sleep(model.getEngineSupplyTimeout() * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
