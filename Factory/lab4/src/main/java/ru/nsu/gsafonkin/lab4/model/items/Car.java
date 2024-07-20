package ru.nsu.gsafonkin.lab4.model.items;

import ru.nsu.gsafonkin.lab4.model.items.Accessory;
import ru.nsu.gsafonkin.lab4.model.items.Body;
import ru.nsu.gsafonkin.lab4.model.items.Engine;
import ru.nsu.gsafonkin.lab4.model.items.Item;

public class Car extends Item {
    private static int number = 0;
    private Accessory accessory;
    private Body body;
    private Engine engine;
    public Car(Accessory accessory, Body body, Engine engine) {
        super(++number);
        this.accessory = accessory;
        this.body = body;
        this.engine = engine;
    }
    public Car(int id) {
        super(id);
    }
    public Accessory getAccessory() {
        return accessory;
    }
    public Body getBody() {
        return body;
    }
    public Engine getEngine() {
        return engine;
    }
}
