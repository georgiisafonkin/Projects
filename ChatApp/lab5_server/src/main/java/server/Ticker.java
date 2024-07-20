package server;

import server.IBroadcaster;

public class Ticker extends Thread {
    private final IBroadcaster broadcaster;
    private final long timeout = 3600*1000;
    public Ticker(IBroadcaster broadcaster) {this.broadcaster = broadcaster;}
    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                sleep(timeout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            broadcaster.checkInactive(timeout);
        }
    }
}
