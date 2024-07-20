package ru.nsu.gsafonkin.lab4.model.items;

public class Engine extends Item{
    private static int number = 0;
    public Engine() {
        super(++number);
        //System.out.println("Engine " + id + " was produced");
    }
    public Engine(int id) {
        super(id);
    }
}
