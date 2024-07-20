package ru.nsu.gsafonkin.lab5_client.protocol.commands;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "command")
@XmlAccessorType(XmlAccessType.FIELD)
public class Upload implements ICommand{
    @XmlAttribute(name = "name")
    private String cmdName = "upload";
    @XmlElement
    private String name;
    @XmlElement
    private String mimeType;
    @XmlElement
    private String encoding;
    @XmlElement
    private String content;

    public Upload(String name, String mimeType, String encoding, String content) {
        this.name = name;
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.content = content;
    }
    public Upload() {}

}
