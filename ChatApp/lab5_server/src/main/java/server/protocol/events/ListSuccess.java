package server.protocol.events;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import server.protocol.auxiliary.User;

import java.util.List;

@XmlRootElement(name="success")
public class ListSuccess implements IEvent{
    @XmlElementWrapper(name="users")
    @XmlElement(name="user")
    private List<User> users;

    public ListSuccess(List<User> users) {
        this.users = users;
    }
    public ListSuccess() {}
}


