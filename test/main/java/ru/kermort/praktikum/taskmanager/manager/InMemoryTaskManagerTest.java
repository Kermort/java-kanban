package ru.kermort.praktikum.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void init() {
        tm = new InMemoryTaskManager();
    }
}
