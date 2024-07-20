package server;

import java.util.Map;
import java.util.Queue;

public interface ILoginManager {
    boolean manage(String newUserNickname, String password);

    Map<String, Integer> getLoginsInfo();

    Queue<String> getMessagesForNewUsers();
}
