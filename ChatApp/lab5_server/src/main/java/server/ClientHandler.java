package server;

import jakarta.xml.bind.JAXBException;
import server.protocol.XMLHandler;
import server.protocol.events.File;
import server.protocol.events.IEvent;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements IClientHandler {
    private final ILoginManager loginManager;
    private final IFileManager fileManager;
    private final IBroadcaster broadcaster;
    private Socket clientSocket;
    private String clientNickname;
    private final ClientReader reader;
    private final ClientWriter writer;
    private final XMLHandler xmlHandler;
    private long lastReceivedMsgTime;
    public ClientHandler(ILoginManager loginManager, IFileManager fileManager, IBroadcaster broadcaster, Socket clientSocket) throws IOException {
        this.loginManager = loginManager;
        this.fileManager = fileManager;
        this.broadcaster = broadcaster;
        this.clientSocket = clientSocket;
        this.reader = new ClientReader(this, clientSocket.getInputStream());
        this.writer = new ClientWriter(this, clientSocket.getOutputStream());
        try {
            this.xmlHandler = new XMLHandler(this);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        reader.start();
        writer.start();
        System.out.println("Client handled...\n");
    }

    @Override
    public void sendMessage(String outputMsg) {
        writer.getMessagesToWrite().add(outputMsg);
    }

    @Override
    public void recieveMessage(String inputMsg) {
        if (inputMsg != null && !inputMsg.isBlank()) {
            lastReceivedMsgTime = System.currentTimeMillis();
            System.out.println("Received Message: " + inputMsg);
            String outputMsg = processMessage(inputMsg);
            sendMessage(outputMsg);
        }
    }

    @Override
    public String processMessage(String processedMsg) {
        IEvent e = xmlHandler.createObject(processedMsg);
        System.out.println("CLASS: " + e.toString());
        return xmlHandler.createXML(e);
    }

    @Override
    public void closeClientSocket() throws IOException {
        clientSocket.close();
    }
    @Override
    public boolean isClientSocketConnected() {
        return clientSocket.isConnected();
    }

    @Override
    public boolean isClientSocketClosed() {
        return clientSocket.isClosed();
    }

    @Override
    public String getClientNickname() {
        return clientNickname;
    }
    @Override
    public void setClientNickname(String clientNickname) {
        this.clientNickname = clientNickname;
    }
    @Override
    public boolean checkLogin(String newUserNickname, String password) {
        return loginManager.manage(newUserNickname, password);
    }
    @Override
    public void broadcast(String broadcastedMsg) {
        broadcaster.broadcast(broadcastedMsg);
    }

    @Override
    public void closeConnection() {
        try {
            closeClientSocket();
            writer.interrupt();
            reader.interrupt();
            broadcaster.getClientHandlerList().remove(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public IBroadcaster getBroadcaster() {
        return broadcaster;
    }
    @Override
    public ILoginManager getLoginManager() {
        return loginManager;
    }
    @Override
    public long getLastReceivedMsgTime() {
        return lastReceivedMsgTime;
    }

    @Override
    public void sendLastMessages() {
        for (String msg : loginManager.getMessagesForNewUsers()) {
            this.sendMessage(msg);
        }
    }

    @Override
    public void sendFiles() {
        for (File f : fileManager.getFileList()) {
            this.sendMessage(xmlHandler.createXML(f));
        }
    }

    @Override
    public IFileManager getFileManager() {
        return fileManager;
    }
}
