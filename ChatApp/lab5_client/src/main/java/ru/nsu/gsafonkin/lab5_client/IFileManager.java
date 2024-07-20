package ru.nsu.gsafonkin.lab5_client;

import java.nio.file.Path;
import java.util.Map;

public interface IFileManager {
    Map<String, Integer> getFiles();

    int getIDbyName(String name);

    Path getPath();

    void setPath(Path path);

    String getName();

    void setName(String name);

    void createFile(String name, String mimeType, String encoding, String content);
}
