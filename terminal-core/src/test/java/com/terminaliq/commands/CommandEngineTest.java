package com.terminaliq.commands;

import com.terminaliq.context.ShellContext;
import com.terminaliq.execution.CommandExecutor;
import com.terminaliq.execution.ExecutionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommandEngineTest {

    @TempDir
    Path tempDir;

    private CommandStorage storage;
    private CommandRegistry registry;
    private CommandExecutor executor;
    private CustomCommandEngine engine;

    @BeforeEach
    public void setUp() throws Exception {
        Path yamlFile = tempDir.resolve("commands.yaml");
        storage = new CommandStorage(yamlFile);
        registry = new CommandRegistry(storage);
        registry.loadFromStorage();
        
        ShellContext context = new ShellContext();
        executor = new CommandExecutor(context);
        engine = new CustomCommandEngine(executor);
    }

    @Test
    public void testSaveAndLoad() throws Exception {
        CustomCommand cmd = new CustomCommand(
            "dev",
            "Start development environment",
            Arrays.asList("echo starting", "echo done"),
            Collections.singletonList("env")
        );

        assertTrue(registry.register(cmd));
        
        CommandRegistry otherRegistry = new CommandRegistry(storage);
        otherRegistry.loadFromStorage();
        
        CustomCommand loaded = otherRegistry.findByName("dev");
        assertNotNull(loaded);
        assertEquals("dev", loaded.getName());
        assertEquals("Start development environment", loaded.getDescription());
        assertEquals(2, loaded.getSteps().size());
        assertEquals("echo starting", loaded.getSteps().get(0));
        assertEquals("echo done", loaded.getSteps().get(1));
        assertEquals(1, loaded.getArguments().size());
        assertEquals("env", loaded.getArguments().get(0));
    }

    @Test
    public void testDuplicateNameRejection() throws Exception {
        CustomCommand cmd1 = new CustomCommand(
            "test",
            "first",
            Collections.singletonList("echo 1"),
            Collections.emptyList()
        );
        CustomCommand cmd2 = new CustomCommand(
            "Test",
            "second",
            Collections.singletonList("echo 2"),
            Collections.emptyList()
        );

        assertTrue(registry.register(cmd1));
        assertFalse(registry.register(cmd2));
    }

    @Test
    public void testRemoveCommand() throws Exception {
        CustomCommand cmd = new CustomCommand(
            "temp",
            "desc",
            Collections.singletonList("echo temp"),
            Collections.emptyList()
        );

        assertTrue(registry.register(cmd));
        assertNotNull(registry.findByName("temp"));
        
        assertTrue(registry.remove("temp"));
        assertNull(registry.findByName("temp"));
    }

    @Test
    public void testArgumentParsing() {
        List<String> args1 = CustomCommandEngine.parseArguments("pushall \"fix bug\"");
        assertEquals(2, args1.size());
        assertEquals("pushall", args1.get(0));
        assertEquals("fix bug", args1.get(1));

        List<String> args2 = CustomCommandEngine.parseArguments("run foo bar");
        assertEquals(3, args2.size());
        assertEquals("run", args2.get(0));
        assertEquals("foo", args2.get(1));
        assertEquals("bar", args2.get(2));
    }

    @Test
    public void testArgumentSubstitution() {
        CustomCommand cmd = new CustomCommand(
            "hello",
            "Greets a user",
            Collections.singletonList("echo Hello {{user}}"),
            Collections.singletonList("user")
        );

        ExecutionResult result = engine.execute(cmd, "hello Alice");
        assertEquals(0, result.getExitCode());
    }

    @Test
    public void testWorkflowStopOnFailure() {
        CustomCommand cmd = new CustomCommand(
            "failworkflow",
            "fails at step 2",
            Arrays.asList("echo step1", "exit 1", "echo step3"),
            Collections.emptyList()
        );

        ExecutionResult result = engine.execute(cmd);
        assertNotEquals(0, result.getExitCode());
    }
}
