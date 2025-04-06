package ru.kermort.praktikum.taskmanager.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.manager.InMemoryTaskManager;
import ru.kermort.praktikum.taskmanager.tasks.Task;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class InMemoryHistoryManagerTest {
    private InMemoryTaskManager tm;

    @BeforeEach
    void init() {
         tm = new InMemoryTaskManager();
    }

    @Test
    void historyTest() {
        Task t1 = new Task("Заголовок задачи 1", "Описание задачи 1");
        int t1Id = tm.addTask(t1);
        Task taskFromManager = tm.getTask(t1Id);
        Task taskFromHistory = tm.getHistory().get(0);
        taskFromHistory.setTitle("Измененный заголовок задачи 1");
        taskFromHistory.setDescription("Измененное описание задачи 1");
        tm.updateTask(taskFromManager);
        Task updatedTaskFromManager = tm.getTask(t1Id);

        assertNotEquals(taskFromHistory.getTitle(), updatedTaskFromManager.getTitle(),
                "измененная задача в истории тоже изменена");
        assertNotEquals(taskFromHistory.getDescription(), updatedTaskFromManager.getDescription(),
                "измененная задача в истории тоже изменена");
        System.out.println(updatedTaskFromManager);
        System.out.println(taskFromHistory);
    }

    @Test
    void testHistoryOverflow() {
        Task task = new Task("Заголовок задачи 1", "Описание задачи 1");
        int taskId = tm.addTask(task);
        for (int i = 1; i <= InMemoryHistoryManager.HISTORY_LIMIT + 1; i++) {
            tm.getTask(taskId);
        }
        assertEquals(InMemoryHistoryManager.HISTORY_LIMIT, tm.getHistory().size());
    }
}
