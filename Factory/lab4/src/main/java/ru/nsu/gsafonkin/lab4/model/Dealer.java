package ru.nsu.gsafonkin.lab4.model;

import ru.nsu.gsafonkin.lab4.model.items.Car;

import java.time.LocalTime;
import java.util.concurrent.BlockingQueue;

public class Dealer extends Thread {
    private final Model model;
    private Storage<Car> carStorage;
    private static int number = 0;
    private int id;
    public Dealer(Model model, Storage<Car> carStorage) {
        this.model = model;
        this.carStorage = carStorage;
        this.id = ++number;
    }

    @Override
    public void run() {
        while(true) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            try {
                Car taken = carStorage.take();
                System.out.println(LocalTime.now() +
                        " Dealer " + id +
                        ": Car " + taken.getId() +
                        "(Accessory - " + taken.getAccessory().getId() +
                        " Body - " + taken.getBody().getId() +
                        " Engine - " + taken.getEngine().getId() +
                        ") was produced");
                sleep(model.getCarRequestingTimeout() * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
