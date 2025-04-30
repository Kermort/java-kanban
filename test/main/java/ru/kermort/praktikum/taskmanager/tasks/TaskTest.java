package ru.kermort.praktikum.taskmanager.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    @Test
    void constructorTest() {
        Task initialTask = new Task("title", "description");
        Task copiedTask = new Task(initialTask);

        assertNotSame(initialTask, copiedTask,
                "переменные initialTask и copiedTask ссылаются на один и тот же объект");
    }

    @Test
    void testEquals() {
        Task task1 = new Task("title", "description");
        Task task2 = new Task("заголовок", "описание");
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "задачи с одинаковым id не равны");
    }
}
