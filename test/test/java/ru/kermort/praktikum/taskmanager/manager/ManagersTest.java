package ru.kermort.praktikum.taskmanager.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {
    @Test
    void testManagers() {
        assertNotNull(Managers.getDefault(), "Менеджер вернул null вместо InMemoryTaskManager");
        assertNotNull(Managers.getDefaultHistory(), "Менеджер вернул null вместо InMemoryHistoryManager");
    }
}
