package server.protocol;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import server.ClientHandler;
import server.IClientHandler;
import server.protocol.auxiliary.User;
import server.protocol.events.*;
import server.protocol.events.Error;
import server.protocol.events.File;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class XMLHandler {
    private final IClientHandler clientHandler;
    private JAXBContext jaxbContext;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private XMLInputFactory xmlInputFactory;
    public XMLHandler(IClientHandler clientHandler) throws JAXBException {
        this.clientHandler = clientHandler;
        this.jaxbContext = JAXBContext.newInstance(Success.class, Error.class, Message.class,
                UserLogin.class, UserLogout.class, ListSuccess.class,
                File.class, UploadSuccess.class, DownloadSuccess.class);
        this.marshaller = jaxbContext.createMarshaller();
        this.unmarshaller = jaxbContext.createUnmarshaller();
        this.xmlInputFactory = XMLInputFactory.newInstance();
    }
    public String createXML(IEvent event) {
        StringWriter stringWriter = new StringWriter();
        try {
            marshaller.marshal(event, stringWriter);
        } catch (JAXBException e) {
            System.out.println(e.getCause());
        }
        return stringWriter.toString();
    }
    public IEvent createObject(String xmlString) {
        StringReader stringReader = new StringReader(xmlString);

        IEvent returnEvent;

        String cmdName = null;
        XMLStreamReader xmlStreamReader;
        try {
            xmlStreamReader = xmlInputFactory.createXMLStreamReader("base64", stringReader);

            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if (xmlStreamReader.getLocalName().equals("command")) {
                        // Access the attribute values
                        cmdName = xmlStreamReader.getAttributeValue(null, "name");
                        break;
                    }
                }
                else if (next == XMLStreamReader.END_ELEMENT) {
                    if (xmlStreamReader.getLocalName().equals("command")) {
                        cmdName = xmlStreamReader.getAttributeValue(null, "name");
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            return error("Received message doesn't match with protocol");
        }

        switch (cmdName){
            case("login"):
                return login(xmlStreamReader);
            case("message"):
                return message(xmlStreamReader);
            case("logout"):
                return logout(xmlStreamReader);
            case("list"):
                return list(xmlStreamReader);
            case("upload"):
                return upload(xmlStreamReader);
            case("download"):
                return download(xmlStreamReader);
            default:
                return error("Unknown command");
        }
    }
    private IEvent login(XMLStreamReader xmlStreamReader) {
        //read necessary fields
        String userNickname = null;
        String password = null;
        try {
            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if (xmlStreamReader.getLocalName().equals("name")) {
                        userNickname = xmlStreamReader.getElementText();
                    }
                    else if(xmlStreamReader.getLocalName().equals("password")) {
                        password = xmlStreamReader.getElementText();
                    }
                }
            }
        } catch (XMLStreamException e) {
            return new Error("Incorrect nickname. Use latin letters");
        }

        //process new login and create an IEvent object
        System.out.println(userNickname + ": " + String.valueOf(password));
        if (clientHandler.checkLogin(userNickname, password)) {
            clientHandler.setClientNickname(userNickname);
            clientHandler.broadcast(createXML(new UserLogin(userNickname)));
            clientHandler.getBroadcaster().getClientHandlerList().add(clientHandler);
            clientHandler.sendLastMessages();
            clientHandler.sendFiles();
            return new Success();
        }
        else {
            return new Error("Incorrect username or password");
        }
    }
    private IEvent message(XMLStreamReader xmlStreamReader) {
        //read necessary fields
        String senderNickname = clientHandler.getClientNickname();
        String message = null;
        try {
            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if (xmlStreamReader.getLocalName().equals("message")) {
                        message = xmlStreamReader.getElementText();
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        if (message != null && !message.isBlank()) {
            String xml = this.createXML(new Message(senderNickname, message));
            clientHandler.getLoginManager().getMessagesForNewUsers().offer(xml);
            clientHandler.broadcast(xml);
            return new Success();
        } else {
            return new Error("blank or illegal message");
        }
    }

    private IEvent logout(XMLStreamReader xmlStreamReader) {
        clientHandler.broadcast(this.createXML(new UserLogout(clientHandler.getClientNickname())));
        clientHandler.getBroadcaster().getClientHandlerList().remove(clientHandler);
        return new Success();
    }

    private IEvent list(XMLStreamReader xmlStreamReader) {
        List<User> users = new ArrayList<>();
        for (IClientHandler ch : clientHandler.getBroadcaster().getClientHandlerList()) {
            users.add(new User(ch.getClientNickname()));
        }
        return new ListSuccess(users);
    }

    private IEvent upload(XMLStreamReader xmlStreamReader) {
        int id;
        String from = clientHandler.getClientNickname();
        String name = null;
        int size = 0;
        String encoding = null;
        String mimeType = null;
        String content = null;
        try {
            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    switch (xmlStreamReader.getLocalName()) {
                        case ("name"):
                            name = xmlStreamReader.getElementText();
                            break;
                        case ("mimeType"):
                            mimeType = xmlStreamReader.getElementText();
                            break;
                        case("encoding"):
                            encoding = xmlStreamReader.getElementText();
                            break;
                        case("content"):
                            content = xmlStreamReader.getElementText();
                            break;
                    }

                }
            }
        } catch (XMLStreamException e) {
            System.out.println(e.getMessage());
            return new Error("some error in xml handler");
        }
        id = name.hashCode();
        clientHandler.getFileManager().addFile(id, name);
        clientHandler.getFileManager().createFile(name, id, mimeType, content);
        File f = new File(id, from, name, size, mimeType);
        clientHandler.getFileManager().getFileList().add(f);
        clientHandler.broadcast(this.createXML(f));
        return new UploadSuccess(id);
    }

    private IEvent download(XMLStreamReader xmlStreamReader) {
        int id = 0;
        try {
            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if (xmlStreamReader.getLocalName().equals("id")) {
                        id = Integer.valueOf(xmlStreamReader.getElementText());
                    }
                }
            }
        } catch (XMLStreamException e) {
            System.out.println("download: " + e.getMessage());
        }
        String name = clientHandler.getFileManager().getFile(id);
        String mimeType = null;
        String encoding = null;
        String content = null;
        try {
            java.io.File file = new java.io.File("src/files/" + name);
            mimeType = Files.probeContentType(Paths.get("src/files/" + name));
            InputStream isr = new FileInputStream(file);
            encoding = "base64";
            byte[] byteContent = new byte[(int) file.length()];
            isr.read(byteContent, 0, (int) file.length());
            content = Base64.getEncoder().encodeToString(byteContent);
        } catch (FileNotFoundException e) {
          return new Error("download: file not found");
        } catch (IOException e) {
            return new Error("download: " + e.getMessage());
        }
        return new DownloadSuccess(id, name, mimeType, encoding, content);
    }

    private IEvent error(String reason) {
        return new Error(reason);
    }
}

