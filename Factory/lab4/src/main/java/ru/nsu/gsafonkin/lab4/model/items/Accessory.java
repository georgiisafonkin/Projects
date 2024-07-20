package ru.nsu.gsafonkin.lab4.model.items;

public class Accessory extends Item {
    private static int number = 0;
    public Accessory() {
        super(++number);
        //System.out.println("Accessory " + id + " was produced");
    }
    public Accessory(int id) {
        super(id);
    }
}
