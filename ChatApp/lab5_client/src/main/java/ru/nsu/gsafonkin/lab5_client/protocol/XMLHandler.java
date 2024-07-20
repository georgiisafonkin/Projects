package ru.nsu.gsafonkin.lab5_client.protocol;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import ru.nsu.gsafonkin.lab5_client.IClient;
import ru.nsu.gsafonkin.lab5_client.protocol.commands.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

public class XMLHandler {
    private IClient client;
    private JAXBContext jaxbContext;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private XMLInputFactory xmlInputFactory;
    public XMLHandler(IClient client) throws JAXBException {
        this.client = client;
        jaxbContext = JAXBContext.newInstance(Login.class, Message.class, List.class, Logout.class,
                Upload.class, Download.class);
        marshaller = jaxbContext.createMarshaller();
        unmarshaller = jaxbContext.createUnmarshaller();
        xmlInputFactory = XMLInputFactory.newInstance();
    }
    public String createXML(ICommand event) {
        StringWriter stringWriter = new StringWriter();
        try {
            marshaller.marshal(event, stringWriter);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return stringWriter.toString();
    }
    public String parseXml(String xmlString) {
        StringReader stringReader = new StringReader(xmlString);

        String cmdName = null;
        XMLStreamReader xmlStreamReader;
        try {
            xmlStreamReader = xmlInputFactory.createXMLStreamReader(stringReader);

            while (xmlStreamReader.hasNext()) {
                int event = xmlStreamReader.next();
                if (event == XMLStreamReader.START_ELEMENT) {
                    if (xmlStreamReader.getLocalName().equals("event")) {
                        // Access the attribute values
                        cmdName = xmlStreamReader.getAttributeValue(null, "name");
                        break;
                    }
                    else if (xmlStreamReader.getLocalName().equals("error")) {
                        cmdName = "error";
                        break;
                    }
                    else if (xmlStreamReader.getLocalName().equals("userlogin")) {
                        cmdName = "userlogin";
                        break;
                    }
                    else if (xmlStreamReader.getLocalName().equals("userlogout")) {
                        cmdName = "userlogout";
                        break;
                    }
                    else if (xmlStreamReader.getLocalName().equals("success")) {
                        cmdName = "success";
                        break;
                    }
                }
                else if (event == XMLStreamReader.END_ELEMENT) {
                    if (xmlStreamReader.getLocalName().equals("success")) {
                        cmdName = "success";
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
                throw new RuntimeException(e);
        }

        switch (cmdName){
            case("message"):
                return message(xmlStreamReader);
            case("success"):
                return success(xmlStreamReader);
            case("error"):
                return error(xmlStreamReader);
            case("userlogin"):
                return userlogin(xmlStreamReader);
            case("userlogout"):
                return userlogout(xmlStreamReader);
            case("file"):
                return file(xmlStreamReader);
            default: break;
        }
        return "";
    }

    private String message(XMLStreamReader xmlStreamReader) {
        //read necessary fields
        String senderNickname = null;
        String message = null;
        try {
            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if (xmlStreamReader.getLocalName().equals("from")) {
                        senderNickname = xmlStreamReader.getElementText();
                    }
                    else if (xmlStreamReader.getLocalName().equals("message")) {
                        message = xmlStreamReader.getElementText();
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }

        return senderNickname + ": " + message;
    }

    private String success(XMLStreamReader xmlStreamReader) {
        try {
            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if (xmlStreamReader.getLocalName().equals("id")) {
                        return uploaddownloadsuccess(xmlStreamReader);
                    }
                    if (xmlStreamReader.getLocalName().equals("users")) {
                        return listsuccess(xmlStreamReader);
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return "SUCCESS";
    }

    private String listsuccess(XMLStreamReader xmlStreamReader) {
        StringBuilder builder = new StringBuilder("<chat members: ");
        try {
            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if (xmlStreamReader.getLocalName().equals("name")) {
                        builder.append(xmlStreamReader.getElementText());
                        builder.append(", ");
                    }
                }
            }
            builder.delete(builder.length() - 2, builder.length());
            builder.append(">");
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

    private String error(XMLStreamReader xmlStreamReader) {
        String msg = null;
        try {
            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if (xmlStreamReader.getLocalName().equals("message")) {
                        msg = xmlStreamReader.getElementText();
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return "Server: " + msg;
    }
    private String userlogin(XMLStreamReader xmlStreamReader) {
        String name = null;
        try {
            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if (xmlStreamReader.getLocalName().equals("name")) {
                        name = xmlStreamReader.getElementText();
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return name + " connected to the server.";
    }

    private String uploaddownloadsuccess(XMLStreamReader xmlStreamReader) {
        String name = null;
        String mimeType = null;
        String encoding = null;
        String content = null;
        try {
            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    switch (xmlStreamReader.getLocalName()) {
                        case("name"):
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
            throw new RuntimeException(e);
        }
        if (name != null) {
            client.getFileManager().createFile(name, mimeType, encoding, content);
        }
        return "SUCCESS";
    }

    private String userlogout(XMLStreamReader xmlStreamReader) {
        String name = null;
        try {
            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if (xmlStreamReader.getLocalName().equals("name")) {
                        name = xmlStreamReader.getElementText();
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return name + " disconnected from the server.";
    }

    private String file(XMLStreamReader xmlStreamReader) {
        int id = 0;
        String from = null;
        String name = null;
        int size;
        try {
            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    switch (xmlStreamReader.getLocalName()) {
                        case("id"):
                            id = Integer.valueOf(xmlStreamReader.getElementText());
                            break;
                        case ("from"):
                            from = xmlStreamReader.getElementText();
                            break;
                        case ("name"):
                            name = xmlStreamReader.getElementText();
                            break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            System.out.println("Something gone wrong: " + e.getMessage());
        }
        client.receiveFile(name, id);
        return from + " uploaded " + name + " to the server";
    }
}
