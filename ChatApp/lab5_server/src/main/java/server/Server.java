package server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server{
    private static int port = 8080;
    private static final ILoginManager loginManager = new LoginManager();
    private static final IFileManager fileManager = new FileManager();
    private static final IBroadcaster broadcaster = new Broadcaster();
    private static final Ticker ticker = new Ticker(broadcaster);
    public static void main(String[] args) throws IOException {
        port = Integer.valueOf(args[0]);
        try (java.net.ServerSocket server = new java.net.ServerSocket(port)) {
            ticker.start();
            while (true) {
                Socket clientSocket = server.accept();
                IClientHandler clientHandler = new ClientHandler(loginManager, fileManager, broadcaster, clientSocket);
                System.out.println("CLIENTS: " + broadcaster.getClientHandlerList().size());
            }
        }
    }
}

//TODO FIX КИРИЛЛИЦА
//TODO НОРМАЛЬНОЕ ПОВЕДЕНИЕ КЛИЕНТА ПРИ ПОЛОЖЕННОМ СЕРВЕРЕ
//TODO РАСШИРЕНИЕ ПРОТОКОЛА ДЛЯ FILELIST
