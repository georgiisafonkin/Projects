package server;

import java.util.List;

public interface IBroadcaster {
    List<IClientHandler> getClientHandlerList();
    void broadcast(String broadcastedMsg);
    void checkInactive(long timeout);
}
