package ru.nsu.gsafonkin.lab5_client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class FileManager implements IFileManager {
    private Map<String, Integer> files;
    private Path path;
    private String name;
    public FileManager() {
        files = new HashMap<>();
    }
    @Override
    public Map<String, Integer> getFiles() {
        return files;
    }
    @Override
    public int getIDbyName(String name) {
        return files.get(name);
    }
    @Override
    public Path getPath() {
        return path;
    }
    @Override
    public void setPath(Path path) {
        this.path = path;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public void createFile(String name, String mimeType, String encoding, String content) {
//        if (!this.name.equals(name)) {
//        }
        try {
            Files.createFile(path);
            System.out.println("CREATED");
            Files.write(path, Base64.getDecoder().decode(content.getBytes()));
            System.out.println("WRITED");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
