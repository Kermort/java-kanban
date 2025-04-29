package ru.kermort.praktikum.taskmanager.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTaskTest {

    @Test
    void constructorTest() {
        EpicTask initialEpicTask = new EpicTask("title", "description");
        SubTask initialsubTask = new SubTask("title", "description", initialEpicTask.getId());
        EpicTask copiedEpicTask = new EpicTask(initialEpicTask);

        assertNotSame(initialEpicTask, copiedEpicTask,
                "начальный эпик и его копия не должны быть одним и тем же объектом");

        assertNotSame(initialEpicTask.getSubTasksIds(), copiedEpicTask.getSubTasksIds(),
                "начальный и скопированнй эпик ссылается на один и тот эе список подзадач");
    }

    @Test
    void testEquals() {
        EpicTask epicTask1 = new EpicTask("title", "description");
        EpicTask epicTask2 = new EpicTask("заголовок", "описание");
        epicTask1.setId(1);
        epicTask2.setId(1);

        assertEquals(epicTask1, epicTask2, "эпики с одинаковым id не равны");
    }

}
