package ru.nsu.gsafonkin.lab4.model.items;

public class Body extends Item{
    private static int number = 0;
    public Body() {
        super(++number);
        //System.out.println("Body " + id + " was produced");
    }
    public Body(int id) {
        super(id);
    }
}
