package server.protocol.events;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="error")
@XmlAccessorType(XmlAccessType.FIELD)
public class Error implements IEvent {
    @XmlElement(name="message")
    private String message;
    public Error(String message) {
        this.message = message;
    }
    public Error() {
        this("REASON");
    }
}
