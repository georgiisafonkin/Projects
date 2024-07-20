package server.protocol.events;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="event")
@XmlAccessorType(XmlAccessType.FIELD)
public class Message implements IEvent {
    @XmlAttribute(name="name")
    private String eventName = "message";
    @XmlElement(name="from")
    private String from;
    @XmlElement(name="message")
    private String message;
    public Message(String from, String message) {
        this.from = from;
        this.message = message;
    }
    public Message() {}
}
