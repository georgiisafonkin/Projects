package server;

import java.io.IOException;

public interface IClientHandler {
    void sendMessage(String msg);
    void recieveMessage(String msg);
    String processMessage(String bytes);
    void closeClientSocket() throws IOException;
    boolean isClientSocketConnected();
    boolean isClientSocketClosed();
    String getClientNickname();
    void setClientNickname(String clientNickname);
    boolean checkLogin(String newUserNickname, String password);
    void broadcast(String broadcastedMsg);
    void closeConnection();

    IBroadcaster getBroadcaster();

    ILoginManager getLoginManager();

    long getLastReceivedMsgTime();

    void sendLastMessages();

    void sendFiles();

    IFileManager getFileManager();
}
