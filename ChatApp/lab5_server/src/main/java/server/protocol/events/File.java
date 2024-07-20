package server.protocol.events;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="event")
@XmlAccessorType(XmlAccessType.FIELD)
public class File implements IEvent{
    @XmlAttribute(name="name")
    private String eventName = "file";
    @XmlElement
    private int id;
    @XmlElement
    private String from;
    @XmlElement
    private String name;
    @XmlElement
    private int size;
    @XmlElement
    private String mimeType;

    public File(int id, String from, String name, int size, String mimeType) {
        this.id = id;
        this.from = from;
        this.name = name;
        this.size = size;
        this.mimeType = mimeType;
    }
    public File() {}
}
