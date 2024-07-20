package ru.nsu.gsafonkin.lab4.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConfigParser {
    private Map<String, Integer> data;
    private String configPath;
    public ConfigParser(String configPath) {
        this.data = new HashMap<>();
        this.configPath = configPath;
        try (Scanner scanner = new Scanner(ConfigParser.class.getResource(configPath).openStream())) {
            String[] strs;
            while (scanner.hasNext()) {
                strs = scanner.nextLine().split("=");
                data.put(strs[0], Integer.valueOf(strs[1]));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<String, Integer> getData() {return data;}
}
