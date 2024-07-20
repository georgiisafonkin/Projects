package server.protocol.events;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="event")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserLogin implements IEvent{
    @XmlAttribute(name="name")
    private String eventName = "userlogin";
    @XmlElement(name="name")
    private String name;
    public UserLogin(String name) {
        this.name = name;
    }
    public UserLogin(){}
}
