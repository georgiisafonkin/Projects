package server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ClientReader extends Thread{
    IClientHandler clientHandler;
    private InputStream input;
    public ClientReader(IClientHandler clientHandler, InputStream in) {
        super();
        this.clientHandler = clientHandler;
        this.input = in;
    }
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (clientHandler.isClientSocketClosed() == true) {
                this.interrupt();
                continue;
            }
            byte[] msgByteSize = new byte[4];
            byte[] bytes;
            String inputMsg;
            int bytesRead = 0;
            int tmp = 0;
            try {
                input.read(msgByteSize, 0, 4);
                int msgSize = ByteBuffer.wrap(msgByteSize).getInt();
                if (msgSize == -1) {
                    continue;
                }
                bytes = new byte[msgSize];
                while (bytesRead != msgSize) {
                    tmp = input.read(bytes, bytesRead, msgSize - bytesRead);
                    if (tmp == -1) {
                        break;
                    }
                    bytesRead += tmp;
                    System.out.println("BYTES READ: " + tmp);
                }
                if (tmp == -1) {
                    clientHandler.recieveMessage(null);
                }
                if (clientHandler.isClientSocketConnected() == false) {
                    this.interrupt();
                    break;
                }
                inputMsg = new String(bytes, 0, msgSize);
                clientHandler.recieveMessage(inputMsg);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
