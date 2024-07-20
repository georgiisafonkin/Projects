package ru.nsu.gsafonkin.lab4.model;

import ru.nsu.gsafonkin.lab4.model.items.Accessory;
import ru.nsu.gsafonkin.lab4.model.items.Body;
import ru.nsu.gsafonkin.lab4.model.items.Car;
import ru.nsu.gsafonkin.lab4.model.items.Engine;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class Worker implements Runnable {
    private final Model model;
    private Storage<Accessory> accessoryStorage;
    private Storage<Body> bodyStorage;
    private Storage<Engine> engineStorage;
    private Storage<Car> carStorage;
    public Worker(Model model) {
        this(model,
                model.getAccessoriesStorage(),
                model.getBodiesStorage(),
                model.getEnginesStorage(),
                model.getCarsStorage());
    }
    public Worker(Model model, Storage<Accessory> accessoryStorage,
                  Storage<Body> bodyStorage, Storage<Engine> engineStorage, Storage<Car> carStorage) {
        this.model = model;
        this.accessoryStorage = accessoryStorage;
        this.bodyStorage = bodyStorage;
        this.engineStorage = engineStorage;
        this.carStorage = carStorage;
    }
    @Override
    public void run() {
        try {
            if (!Thread.currentThread().isInterrupted() && null != carStorage) {
                carStorage.put(new Car(accessoryStorage.take(), bodyStorage.take(), engineStorage.take()));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
