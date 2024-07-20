package ru.nsu.gsafonkin.lab5_client.protocol.commands;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "command")
@XmlAccessorType(XmlAccessType.FIELD)
public class Message implements ICommand {
    @XmlAttribute(name="name")
    private String cmdName = "message";
    @XmlElement(name = "message")
    private String message;
    public Message(String message) {
        this.message = message;
    }
    public Message() {}
}
