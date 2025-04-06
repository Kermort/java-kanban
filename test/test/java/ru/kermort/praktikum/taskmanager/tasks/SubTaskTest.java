package ru.kermort.praktikum.taskmanager.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubTaskTest {
    @Test
    void constructorTest() {
        EpicTask initialEpicTask = new EpicTask("title", "description");
        SubTask initialSubTask = new SubTask("title", "description", initialEpicTask);

        SubTask copiedSubTask = new SubTask(initialSubTask);

        assertNotSame(initialSubTask.getParentTask(), copiedSubTask.getParentTask(),
                "в начальной и скопированной подзадаче содержится ссылка на один и тот же объект эпика");
    }

    @Test
    void testEquals() {
        EpicTask epicTask = new EpicTask("title", "description");
        SubTask subTask1 = new SubTask("title", "description", epicTask);
        SubTask subTask2 = new SubTask("заголовок", "описание", epicTask);
        subTask1.setId(1);
        subTask2.setId(1);
        assertEquals(subTask1, subTask2, "подзадачи с одинаковым id не равны");
    }
}
