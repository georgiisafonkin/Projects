package ru.nsu.gsafonkin.lab4.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Storage<E> implements IStorage { //TODO композиция вместо наследования
    private Model model;
    private IStorageListener storageListener;
    private IStorageRegulator storageRegulator;
    private int capacity;
    private int productProduced = 0;
    private BlockingQueue<E> blockingQueue;
    public Storage(int capacity, Model model, IStorageListener storageRegulator) {
        this(capacity, model);
        this.storageListener = storageRegulator;
    }
    public Storage(int capacity, Model model) {
        this.capacity = capacity;
        this.model = model;
        blockingQueue = new LinkedBlockingQueue<>(capacity);
        this.storageListener = null;
        this.storageRegulator = null;
    }
    @Override
    public int getCapacity() {
        return capacity;
    }
    @Override
    public int getSize() {
        return blockingQueue.size();
    }
    @Override
    public int getProductProduced() {
        return productProduced;
    }
    public E take() throws InterruptedException {
        E returnable = blockingQueue.take();
        if (null != storageListener) {
            storageListener.onStorageChanges();
        }
        if (null != storageRegulator) {
            storageRegulator.orderProductsIfNecessary();
        }
        return returnable;
    }
    public void put(E item) throws InterruptedException {
        blockingQueue.put(item);
        if (null != storageListener) {
            storageListener.onStorageChanges();
        }
        ++productProduced;
    }
    public BlockingQueue<E> getBlockingQueue() {
        return blockingQueue;
    }
    public void setStorageListener(IStorageListener storageListener) {
        this.storageListener = storageListener;
    }
    public void setStorageRegulator(IStorageRegulator storageRegulator) {
        this.storageRegulator = storageRegulator;
    }
}
