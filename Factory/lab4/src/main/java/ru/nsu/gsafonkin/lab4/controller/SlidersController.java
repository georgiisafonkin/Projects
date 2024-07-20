package ru.nsu.gsafonkin.lab4.controller;

import javafx.scene.control.Slider;
import ru.nsu.gsafonkin.lab4.model.Model;
import ru.nsu.gsafonkin.lab4.model.suppliers.ISupplier;

import java.util.List;
import java.util.function.Supplier;

public class SlidersController {
    private Model model;
    public SlidersController(Model model) {
        this.model = model;
    }
    public void notifyNewTimeout(String supplier, long newTimeout) {
        switch (supplier) {
            case ("Accessory supply") :
                model.setAccessorySupplyTimeout(newTimeout);
                break;
            case ("Body supply") :
                model.setBodySupplyTimeout(newTimeout);
                break;
            case ("Engine supply"):
                model.setEngineSupplyTimeout(newTimeout);
                break;
            case ("Car requesting"):
                model.setCarRequestingTimeout(newTimeout);
                break;
            default:break;
        }
    }
}
