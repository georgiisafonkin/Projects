package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

public class ClientWriter extends Thread {
    private IClientHandler clientHandler;
    private OutputStream output;
    private Queue<String> messagesToWrite = new ArrayDeque<>();
    public ClientWriter(IClientHandler clientHandler, OutputStream out) {
        this.clientHandler = clientHandler;
        this.output = out;
    }
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (clientHandler.isClientSocketConnected() == false) {
                this.interrupt();
                continue;
            }
            byte[] msgSize = new byte[4];
            String outputMsg;
            if (!messagesToWrite.isEmpty()) {
                outputMsg = messagesToWrite.remove();
                if (outputMsg == null) {continue;}
                ByteBuffer.wrap(msgSize).putInt(outputMsg.length());
                try {
                    if (clientHandler.isClientSocketClosed() == true) {
                        this.interrupt();
                        continue;
                    }
                    output.write(msgSize, 0, 4);
                    output.write(outputMsg.getBytes(), 0, outputMsg.length());
                    System.out.println("Sent Message: " + outputMsg);
                }
                catch (SocketException e) {
                    this.interrupt();//TODO HAVE TO REMOVE ITS CLIENT HANDLER FROM BROADCASTER LIST
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public Queue<String> getMessagesToWrite() {
        return messagesToWrite;
    }
}
