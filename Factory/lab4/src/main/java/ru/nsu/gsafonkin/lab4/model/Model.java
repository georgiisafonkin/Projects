package ru.nsu.gsafonkin.lab4.model;

import javafx.application.Platform;
import ru.nsu.gsafonkin.lab4.model.items.Accessory;
import ru.nsu.gsafonkin.lab4.model.items.Body;
import ru.nsu.gsafonkin.lab4.model.items.Car;
import ru.nsu.gsafonkin.lab4.model.items.Engine;
import ru.nsu.gsafonkin.lab4.model.suppliers.AccessorySupplier;
import ru.nsu.gsafonkin.lab4.model.suppliers.BodySupplier;
import ru.nsu.gsafonkin.lab4.model.suppliers.EngineSupplier;
import ru.nsu.gsafonkin.lab4.my.concurent.pool.ThreadPool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Model {
    private IModelListener listener;
    private Storage<Engine> enginesStorage;
    private Storage<Body> bodiesStorage;
    private Storage<Accessory> accessoriesStorage;
    private Storage<Car> carsStorage;
    private IStorageRegulator carsStorageRegulator;
    private ExecutorService accessoriesSuppliersPool; //TODO write my own pool for threads
    private ExecutorService enginesSuppliersPool; //TODO write my own pool for threads
    private ExecutorService bodiesSuppliersPool; //TODO write my own pool for threads
    private ExecutorService workersPool;
    private ExecutorService dealersPool;
    int accessoriesSuppliersNumber;
    int bodiesSupplierNumber;
    int enginesSupplierNumber;
    private int dealersNumber;
    private int workersNumber;
    Map<String, Long> timeouts;
    public Model(IModelListener listener) {
        this.listener = listener;
    }
    public Model(IModelListener listener, String configPath) {
        this(listener);

        ConfigParser configParser = new ConfigParser(configPath);
        accessoriesSuppliersNumber = configParser.getData().get("AccessoriesSuppliersNumber");
        bodiesSupplierNumber = configParser.getData().get("BodySuppliersNumber");
        enginesSupplierNumber = configParser.getData().get("EngineSuppliersNumber");
        workersNumber = configParser.getData().get("WorkersNumber");
        dealersNumber = configParser.getData().get("DealersNumber");

        enginesStorage = new Storage<>(configParser.getData().get("EngineStorageCapacity"), this);
        bodiesStorage = new Storage<>(configParser.getData().get("BodyStorageCapacity"), this);
        accessoriesStorage = new Storage<>(configParser.getData().get("AccessoriesStorageCapacity"), this);
        carsStorage = new Storage<>(configParser.getData().get("CarsStorageCapacity"),this);

        timeouts = new HashMap<>();
        timeouts.put("accessorySupplyTimeout", 1L);
        timeouts.put("bodySupplyTimeout", 1L);
        timeouts.put("engineSupplyTimeout", 1L);
        timeouts.put("carRequestingTimeout", 1L);


        accessoriesSuppliersPool = new ThreadPool(accessoriesSuppliersNumber, accessoriesSuppliersNumber);//Executors.newCachedThreadPool();

        enginesSuppliersPool = new ThreadPool(enginesSupplierNumber, enginesSupplierNumber);//Executors.newCachedThreadPool();

        bodiesSuppliersPool = new ThreadPool(bodiesSupplierNumber, bodiesSupplierNumber);//Executors.newCachedThreadPool();

        workersPool = new ThreadPool(workersNumber, workersNumber);//Executors.newCachedThreadPool();

        dealersPool = new ThreadPool(dealersNumber, dealersNumber);//Executors.newCachedThreadPool();

        for (int i = 0; i < enginesSupplierNumber; ++i) {
            enginesSuppliersPool.submit(new EngineSupplier(enginesStorage, this));
        }
        for (int i = 0; i < bodiesSupplierNumber; ++i) {
            bodiesSuppliersPool.submit(new BodySupplier(bodiesStorage, this));
        }
        for (int i = 0; i < accessoriesSuppliersNumber; ++i) {
            accessoriesSuppliersPool.submit(new AccessorySupplier(accessoriesStorage, this));
        }

        for (int i = 0; i < workersNumber; ++i) {
            workersPool.submit(new Worker(this, accessoriesStorage, bodiesStorage, enginesStorage, carsStorage));
        }

        for (int i = 0; i < dealersNumber; ++i) {
            dealersPool.submit(new Dealer(this, carsStorage));
        }

        carsStorageRegulator = new CarsStorageRegulator(this);
        carsStorage.setStorageRegulator(carsStorageRegulator);
    }
    public Storage<Engine> getEnginesStorage() {
        return enginesStorage;
    }
    public Storage<Body> getBodiesStorage() {
        return bodiesStorage;
    }
    public Storage<Accessory> getAccessoriesStorage() {
        return accessoriesStorage;
    }
    public Storage<Car> getCarsStorage() {
        return carsStorage;
    }
    public ExecutorService getAccessoriesSuppliersPool() {
        return accessoriesSuppliersPool;
    }
    public ExecutorService getEnginesSuppliersPool() {
        return enginesSuppliersPool;
    }
    public ExecutorService getBodiesSuppliersPool() {
        return bodiesSuppliersPool;
    }
    public ExecutorService getWorkersPool() {
        return workersPool;
    }
    public ExecutorService getDealersPool() {
        return dealersPool;
    }
    public IModelListener getListener() {
        return listener;
    }
    public long getAccessorySupplyTimeout() {
        return timeouts.get("accessorySupplyTimeout");
    }
    public long getBodySupplyTimeout() {
        return timeouts.get("bodySupplyTimeout");
    }
    public long getEngineSupplyTimeout() {
        return timeouts.get("engineSupplyTimeout");
    }
    public long getCarRequestingTimeout() { return timeouts.get("carRequestingTimeout");}
    public void setAccessorySupplyTimeout(long accessorySupplyTimeout) {
        timeouts.put("accessorySupplyTimeout", accessorySupplyTimeout);
    }
    public void setBodySupplyTimeout(long bodySupplyTimeout) {
        timeouts.put("bodySupplyTimeout", bodySupplyTimeout);
    }
    public void setEngineSupplyTimeout(long engineSupplyTimeout) {
        timeouts.put("engineSupplyTimeout", engineSupplyTimeout);
    }
    public void setCarRequestingTimeout(long carRequestingTimeout) {
        timeouts.put("carRequestingTimeout", carRequestingTimeout);
    }
    public void notifyOnModelChanges() {
        if (null != listener) {
            listener.onModelChanged();
        }
    }
    public int getDealersNumber() {
        return dealersNumber;
    }
    public int getWorkersNumber() {
        return workersNumber;
    }

    public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(1, TimeUnit.MILLISECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
//            Thread.currentThread().interrupt();
        }
    }
    public void safeCompletion() {
//        accessoriesSuppliersPool.shutdownNow();
//        bodiesSuppliersPool.shutdownNow();
//        enginesSuppliersPool.shutdownNow();
//        workersPool.shutdownNow();
//        dealersPool.shutdownNow();
        awaitTerminationAfterShutdown(accessoriesSuppliersPool);
        awaitTerminationAfterShutdown(bodiesSuppliersPool);
        awaitTerminationAfterShutdown(enginesSuppliersPool);
        awaitTerminationAfterShutdown(workersPool);
        awaitTerminationAfterShutdown(dealersPool);
    }
}