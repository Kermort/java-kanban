package ru.kermort.praktikum.taskmanager.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubTaskTest {
    @Test
    void constructorTest() {
        EpicTask initialEpicTask = new EpicTask("title", "description");
        SubTask initialSubTask = new SubTask("title", "description", initialEpicTask.getId());
        SubTask copiedSubTask = new SubTask(initialSubTask);

        assertNotSame(initialSubTask, copiedSubTask,
                "начальная подзадача и ее копия не должны быть одним и тем же объектом");
    }

    @Test
    void testEquals() {
        EpicTask epicTask = new EpicTask("title", "description");
        SubTask subTask1 = new SubTask("title", "description", epicTask.getId());
        SubTask subTask2 = new SubTask("заголовок", "описание", epicTask.getId());
        subTask1.setId(1);
        subTask2.setId(1);

        assertEquals(subTask1, subTask2, "подзадачи с одинаковым id не равны");
    }
}
