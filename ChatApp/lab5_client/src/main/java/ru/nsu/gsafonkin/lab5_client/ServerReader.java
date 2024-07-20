package ru.nsu.gsafonkin.lab5_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class ServerReader extends Thread {
    private IClient client;
    private InputStream input;
    public ServerReader(IClient client, InputStream in) {
        super();
        this.client = client;
        this.input = in;
    }
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            byte[] msgByteSize = new byte[4];
            byte[] bytes;
            String inputMsg;
            int bytesRead = 0;
            int tmp = 0;
            try {
                if (client.isClientSocketClosed()) {
                    this.interrupt();
                    break;
                }
                input.read(msgByteSize, 0, 4);
                int msgSize = ByteBuffer.wrap(msgByteSize).getInt();
                if (msgSize == -1) {
                    continue;
                }
                bytes = new byte[msgSize];
                while(bytesRead != msgSize) {
                    tmp = input.read(bytes, bytesRead, msgSize - bytesRead);
                    if (tmp == -1) {
                        break;
                    }
                    bytesRead += tmp;
                }
                if (tmp == -1) {
                    client.recieveMessage(null);
                }
//                input.read(bytes, 0, bytes.length);
                inputMsg = new String(bytes, 0, msgSize);
                client.setResponseReceived(true);
                client.recieveMessage(inputMsg);
            }
            catch (SocketException e) {
                this.interrupt();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
