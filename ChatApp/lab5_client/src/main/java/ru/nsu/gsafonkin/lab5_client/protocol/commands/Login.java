package ru.nsu.gsafonkin.lab5_client.protocol.commands;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "command")
@XmlAccessorType(XmlAccessType.FIELD)
public class Login implements ICommand {
    @XmlAttribute(name="name")
    private String cmdName = "login";
    @XmlElement(name = "name")
    private String userName;
    @XmlElement(name = "password")
    private String password;
    public Login(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    public Login() {}

    @Override
    public String toString() {
        String rv = userName + " logged in.";
        return rv;
    }
}
