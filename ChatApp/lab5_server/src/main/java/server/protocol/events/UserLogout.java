package server.protocol.events;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="event")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserLogout implements IEvent{
    @XmlAttribute(name="name")
    private String eventName = "userlogout";
    @XmlElement(name="name")
    private String name;
    public UserLogout(String name) {
        this.name = name;
    }
    public UserLogout() {}
}
