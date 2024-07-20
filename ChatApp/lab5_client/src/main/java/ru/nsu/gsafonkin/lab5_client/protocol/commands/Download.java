package ru.nsu.gsafonkin.lab5_client.protocol.commands;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "command")
@XmlAccessorType(XmlAccessType.FIELD)
public class Download implements ICommand{
    @XmlAttribute(name = "name")
    private String cmdName = "download";
    @XmlElement
    private int id;
    public Download(int id){this.id = id;}
    public Download(){}
}
