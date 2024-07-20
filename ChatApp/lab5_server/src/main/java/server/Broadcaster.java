package server;

import jakarta.xml.bind.JAXBException;
import server.protocol.XMLHandler;
import server.protocol.events.UserLogout;

import java.util.ArrayList;
import java.util.List;

public class Broadcaster implements IBroadcaster {
    private final List<IClientHandler> clientHandlerList = new ArrayList<>();
    private final XMLHandler xmlHandler;

    public Broadcaster() {
        {
            try {
                xmlHandler = new XMLHandler(null);
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void broadcast(String broadcastedMsg) {
        for (IClientHandler clientHandler : clientHandlerList) {
            clientHandler.sendMessage(broadcastedMsg);
        }
    }

    @Override
    public void checkInactive(long timeout) {
        List<IClientHandler> inactive = new ArrayList<>();
        for (IClientHandler clientHandler : clientHandlerList) {
            if (System.currentTimeMillis() - clientHandler.getLastReceivedMsgTime() > timeout) {
                clientHandler.sendMessage(xmlHandler.createXML(new UserLogout(clientHandler.getClientNickname())));
                inactive.add(clientHandler);
            }
        }
        clientHandlerList.remove(inactive);
    }

    @Override
    public List<IClientHandler> getClientHandlerList() {
        return clientHandlerList;
    }
}
