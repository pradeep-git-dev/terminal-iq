package com.terminaliq.commands;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CommandStorage {
    private final Path storagePath;

    public CommandStorage() {
        String userHome = System.getProperty("user.home");
        this.storagePath = Paths.get(userHome, ".terminal-iq", "commands.yaml");
    }

    public CommandStorage(Path storagePath) {
        this.storagePath = storagePath;
    }

    public List<CustomCommand> load() throws IOException {
        if (!Files.exists(storagePath)) {
            return new ArrayList<>();
        }

        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(storagePath)) {
            Object obj = yaml.load(in);
            if (obj == null) {
                return new ArrayList<>();
            }
            if (!(obj instanceof List)) {
                throw new IOException("Invalid YAML structure: Root element must be a list of commands.");
            }

            List<?> list = (List<?>) obj;
            List<CustomCommand> commands = new ArrayList<>();
            Set<String> names = new HashSet<>();
            for (Object item : list) {
                if (!(item instanceof Map)) {
                    throw new IOException("Invalid YAML structure: Command item must be a key-value map.");
                }
                Map<?, ?> map = (Map<?, ?>) item;
                String name = getRequiredString(map, "name");
                
                String lowerName = name.toLowerCase();
                if (names.contains(lowerName)) {
                    throw new IOException("Invalid YAML structure: Duplicate command name '" + name + "' found.");
                }
                names.add(lowerName);

                String description = getOptionalString(map, "description");
                List<String> arguments = getStringList(map, "arguments");
                List<String> steps = getStringList(map, "steps");

                commands.add(new CustomCommand(name, description, steps, arguments));
            }
            return commands;
        } catch (YAMLException e) {
            throw new IOException("YAML syntax error: " + e.getMessage(), e);
        }
    }

    public void save(Collection<CustomCommand> commands) throws IOException {
        Path parent = storagePath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        List<Map<String, Object>> yamlData = new ArrayList<>();
        for (CustomCommand cmd : commands) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", cmd.getName());
            map.put("description", cmd.getDescription());
            map.put("arguments", cmd.getArguments());
            map.put("steps", cmd.getSteps());
            yamlData.add(map);
        }

        Yaml yaml = new Yaml();
        try (Writer writer = Files.newBufferedWriter(storagePath)) {
            yaml.dump(yamlData, writer);
        }
    }

    private String getRequiredString(Map<?, ?> map, String key) throws IOException {
        Object val = map.get(key);
        if (val == null) {
            throw new IOException("Missing required field: '" + key + "' in custom command.");
        }
        return val.toString().trim();
    }

    private String getOptionalString(Map<?, ?> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString().trim() : "";
    }

    private List<String> getStringList(Map<?, ?> map, String key) throws IOException {
        Object val = map.get(key);
        if (val == null) {
            return new ArrayList<>();
        }
        if (!(val instanceof List)) {
            throw new IOException("Field '" + key + "' must be a list.");
        }
        List<?> rawList = (List<?>) val;
        List<String> stringList = new ArrayList<>();
        for (Object item : rawList) {
            if (item != null) {
                stringList.add(item.toString());
            }
        }
        return stringList;
    }
}
