package ru.nsu.gsafonkin.lab4.model.suppliers;

import ru.nsu.gsafonkin.lab4.model.Model;
import ru.nsu.gsafonkin.lab4.model.Storage;
import ru.nsu.gsafonkin.lab4.model.items.Accessory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class AccessorySupplier implements ISupplier{
    private final Model model;
    private Storage<Accessory> accessoryStorage;
    public AccessorySupplier(Storage<Accessory> accessoryStorage, Model model) {
        this.model = model;
        this.accessoryStorage = accessoryStorage;
    }

    @Override
    public void run() {
        while(true) {
            if (Thread.currentThread().isInterrupted() || null == accessoryStorage) {
                break;
            }
            try {
                accessoryStorage.put(new Accessory());
                sleep(model.getAccessorySupplyTimeout() * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
