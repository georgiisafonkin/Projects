package ru.nsu.gsafonkin.lab5_client.protocol.commands;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "command")
@XmlAccessorType(XmlAccessType.FIELD)
public class List implements ICommand {
    @XmlAttribute(name="name")
    private String cmdName = "list";

    public List() {}
}
