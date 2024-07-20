package server;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.*;

public class LoginManager implements ILoginManager{
    private final Map<String, Integer> loginsInfo;
    private final CircularFifoQueue<String> messagesForNewUsers;
    public LoginManager() {
        this.loginsInfo = new HashMap<>();
        this.messagesForNewUsers = new CircularFifoQueue<>(3);
    }
    @Override
    public boolean manage(String newUserNickname, String password) {
        if (loginsInfo.containsKey(newUserNickname)) {
            return password.hashCode() == loginsInfo.get(newUserNickname);
        }
        else if (!newUserNickname.isBlank()) {
            loginsInfo.put(newUserNickname, password.hashCode());
            return true;
        }
        else { return false; }
    }
    @Override
    public Map<String, Integer> getLoginsInfo() {
        return loginsInfo;
    }
    @Override
    public CircularFifoQueue<String> getMessagesForNewUsers() {
        return messagesForNewUsers;
    }
}
