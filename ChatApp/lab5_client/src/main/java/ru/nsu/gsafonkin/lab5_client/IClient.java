package ru.nsu.gsafonkin.lab5_client;

import ru.nsu.gsafonkin.lab5_client.protocol.commands.CommandType;

public interface IClient {
    IFileManager getFileManager();

    void connect(String host, int port);
    void stopHandle();
    void sendMessage(String msg);
    void recieveMessage(String msg);
    String processMessage(String bytes);
    void setNickname(String nickname);

    boolean isResponseReceived();

    void setResponseReceived(boolean responseReceived);

    CommandType getCmdType();

    void setCmdType(CommandType cmdType);
    boolean isClientSocketConnected();
    boolean isClientSocketClosed();
    void receiveFile(String name, int id);
}
