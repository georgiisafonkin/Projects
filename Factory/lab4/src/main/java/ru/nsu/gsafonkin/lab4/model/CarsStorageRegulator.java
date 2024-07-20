package ru.nsu.gsafonkin.lab4.model;

import ru.nsu.gsafonkin.lab4.model.items.Car;

import java.util.concurrent.ExecutorService;

public class CarsStorageRegulator implements IStorageRegulator {
    private Model model;
    private Storage<Car> carStorage;
    private ExecutorService workers;
    public CarsStorageRegulator(Model model) {
        this(model, model.getCarsStorage(), model.getWorkersPool());
    }
    public CarsStorageRegulator(Model model, Storage<Car> carStorage, ExecutorService workers) {
        this.model = model;
        this.carStorage = carStorage;
        this.workers = workers;
    }

    @Override
    public void orderProductsIfNecessary() {
        if (workers.isShutdown())
            return;
        for (int i = carStorage.getSize(); i < carStorage.getCapacity(); ++i) {
            workers.submit(new Worker(model));
        }
    }
}
