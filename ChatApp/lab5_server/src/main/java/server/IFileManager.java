package server;

import server.protocol.events.File;

import java.util.List;

public interface IFileManager {
    void createFile(String name, int id, String mimeType, String content);
    String getFile(int id);
    void addFile(int id, String name);

    List<File> getFileList();
}
