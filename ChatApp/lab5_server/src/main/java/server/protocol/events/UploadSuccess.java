package server.protocol.events;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="success")
public class UploadSuccess implements IEvent{
    @XmlElement
    private int id;

    public UploadSuccess(int id) {this.id = id;}
    public UploadSuccess() {}
}
