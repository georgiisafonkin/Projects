package server.protocol.events;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="success")
@XmlAccessorType(XmlAccessType.FIELD)
public class DownloadSuccess implements IEvent {
    @XmlElement
    private int id;
    @XmlElement
    private String name;
    @XmlElement
    private String mimeType;
    @XmlElement
    private String encoding;
    @XmlElement
    private String content;
    public DownloadSuccess(int id, String name, String mimeType, String encoding, String content) {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.content = content;
    }
    public DownloadSuccess() {}
}
