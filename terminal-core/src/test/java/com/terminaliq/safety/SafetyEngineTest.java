package com.terminaliq.safety;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SafetyEngineTest {

    private final SafetyEngine safetyEngine = new SafetyEngine();

    @Test
    public void testSafeCommands() {
        assertEquals(SafetyEngine.Level.SAFE, safetyEngine.check("mvn clean package").getLevel());
        assertEquals(SafetyEngine.Level.SAFE, safetyEngine.check("git status").getLevel());
        assertEquals(SafetyEngine.Level.SAFE, safetyEngine.check("dir").getLevel());
    }

    @Test
    public void testBlockedCommands() {
        assertEquals(SafetyEngine.Level.BLOCKED, safetyEngine.check("Format-Volume -DriveLetter D").getLevel());
        assertEquals(SafetyEngine.Level.BLOCKED, safetyEngine.check("Remove-Item -Recurse C:\\Windows\\System32").getLevel());
        assertEquals(SafetyEngine.Level.BLOCKED, safetyEngine.check("del C:\\Windows\\explorer.exe").getLevel());
    }

    @Test
    public void testWarningCommands() {
        assertEquals(SafetyEngine.Level.WARNING, safetyEngine.check("Remove-Item -Recurse -Force ./target").getLevel());
        assertEquals(SafetyEngine.Level.WARNING, safetyEngine.check("git push -f origin main").getLevel());
        assertEquals(SafetyEngine.Level.WARNING, safetyEngine.check("docker prune").getLevel());
    }
}
