package ru.nsu.gsafonkin.lab5_client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

public class ServerWriter extends Thread {
    private IClient client;
    private OutputStream output;
    private Queue<String> messagesToWrite = new ArrayDeque<>();
    public ServerWriter(IClient client, OutputStream out) {
        this.client = client;
        this.output = out;
    }
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (!client.isResponseReceived()) {
                continue;
            }
            byte[] msgSize = new byte[4];
            String msg;
            if (!messagesToWrite.isEmpty()) {
                msg = messagesToWrite.remove();
                if (msg == null) {continue;}
                ByteBuffer.wrap(msgSize).putInt(msg.getBytes().length);
                System.out.println("MESSAGE SIZE: " + ByteBuffer.wrap(msgSize).getInt());
                try {
                    output.write(msgSize, 0, 4);
                    output.write(msg.getBytes(), 0, msg.getBytes().length);
                    client.setResponseReceived(false);
                    System.out.println("Sent Message: " + msg);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public Queue<String> getMessagesToWrite() {
        return messagesToWrite;
    }
}
