package com.terminaliq.commands;

import java.io.IOException;
import java.util.*;

public class CommandRegistry {
    private final CommandStorage storage;
    private final Map<String, CustomCommand> registry = new HashMap<>();

    public CommandRegistry(CommandStorage storage) {
        this.storage = storage;
    }

    public void loadFromStorage() throws IOException {
        registry.clear();
        List<CustomCommand> loaded = storage.load();
        for (CustomCommand cmd : loaded) {
            registry.put(cmd.getName().toLowerCase(), cmd);
        }
    }

    public boolean register(CustomCommand cmd) throws IOException {
        String key = cmd.getName().toLowerCase();
        if (registry.containsKey(key)) {
            return false;
        }
        registry.put(key, cmd);
        storage.save(registry.values());
        return true;
    }

    public boolean remove(String name) throws IOException {
        String key = name.toLowerCase();
        if (!registry.containsKey(key)) {
            return false;
        }
        registry.remove(key);
        storage.save(registry.values());
        return true;
    }

    public CustomCommand findByName(String name) {
        return registry.get(name.toLowerCase());
    }

    public List<CustomCommand> list() {
        List<CustomCommand> sorted = new ArrayList<>(registry.values());
        sorted.sort(Comparator.comparing(CustomCommand::getName));
        return sorted;
    }

    public void reload() throws IOException {
        loadFromStorage();
    }
}
