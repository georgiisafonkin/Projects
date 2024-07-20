package server;

import server.protocol.events.File;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileManager implements IFileManager {
    private final Map<Integer, String> files = new HashMap<>();
    private final List<File> fileList = new ArrayList<>();
    public FileManager() {
    }

    @Override
    public void createFile(String name, int id, String mimeType, String content) {
        String path = "src/files/" + name;
        try {
            Files.createFile(Paths.get(path));
            Files.write(Paths.get(path), Base64.getDecoder().decode(content.getBytes()));
        } catch (FileAlreadyExistsException e) {
            //TODO SOME MESSAGE MAYBE
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getFile(int id) {
        return files.get(id);
    }

    @Override
    public void addFile(int id, String name) {
        files.put(id, name);
    }
    @Override
    public List<File> getFileList() {
        return fileList;
    }

}
