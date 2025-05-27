package ru.kermort.praktikum.taskmanager.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.manager.InMemoryTaskManager;
import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;

import java.util.List;

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
    void shouldReturnTaskInCorrectOrder() {
        int t1Id = tm.addTask(new Task("title1", "description1"));
        int t2Id = tm.addTask(new Task("title2", "description2"));
        int t3Id = tm.addTask(new Task("title3", "description3"));
        int t4Id = tm.addTask(new Task("title4", "description4"));
        int t5Id = tm.addTask(new Task("title5", "description5"));

        tm.getTask(t5Id);
        tm.getTask(t3Id);
        tm.getTask(t1Id);
        tm.getTask(t4Id);
        tm.getTask(t2Id);
        tm.getTask(t3Id);
        List<Task> history = tm.getHistory();

        assertTrue(history.get(0).getId() == 5
                && history.get(1).getId() == 1
                && history.get(2).getId() == 4
                && history.get(3).getId() == 2
                && history.get(4).getId() == 3, "история возвращается не в правильном порядке");
    }

    @Test
    void subTasksShouldBeRemovedFromHistoryIfTheirEpicDeleted() {
        int ep1Id = tm.addEpicTask(new EpicTask("Epic 1 title", "Epic 1 description"));
        int st1Id = tm.addSubTask(new SubTask("SubTask 1 title", "SubTask 1 description", ep1Id));
        int st2Id = tm.addSubTask(new SubTask("SubTask 2 title", "SubTask 2 description", ep1Id));

        tm.getEpicTask(ep1Id);
        tm.getSubTask(st1Id);
        tm.getSubTask(st2Id);
        tm.deleteEpicTask(ep1Id);

        assertEquals(tm.getHistory().size(), 0, "подзадачи удаленного эпика остались в истории");
    }

    @Test
    void addAndRemoveElementTest() {
        int t1Id = tm.addTask(new Task("t1", "d"));
        int ep1Id = tm.addEpicTask(new EpicTask("ep1", "d"));
        int st1Id = tm.addSubTask(new SubTask("st1", "d1", ep1Id));

        tm.getTask(t1Id);
        assertEquals(tm.getHistory().size(), 1, "в истории должен быть один элемент");
        tm.getEpicTask(ep1Id);
        assertEquals(tm.getHistory().size(), 2, "в истории должно быть два элемента");
        tm.getSubTask(st1Id);
        assertEquals(tm.getHistory().size(), 3, "в истории должно быть три элемента");
        tm.getTask(t1Id);
        assertEquals(tm.getHistory().size(), 3, "в истории должно быть три элемента");
        tm.deleteSubTask(st1Id);
        assertEquals(tm.getHistory().size(), 2, "в истории должно быть два элемента");
        tm.deleteEpicTask(ep1Id);
        assertEquals(tm.getHistory().size(), 1, "в истории должен быть один элемент");
        tm.deleteTask(t1Id);
        assertEquals(tm.getHistory().size(), 0, "история должна быть пуста");
    }

    @Test
    void tasksShouldBeRemovedFromHistoryIfDeleteAllMethodInvoked() {
        int t1Id = tm.addTask(new Task("t1","d"));
        int t2Id = tm.addTask(new Task("t2","d"));
        int ep1Id = tm.addEpicTask(new EpicTask("ep1", "d"));
        int ep2Id = tm.addEpicTask(new EpicTask("ep2", "d"));
        int st1Id = tm.addSubTask(new SubTask("st1", "d", ep1Id));
        int st2Id = tm.addSubTask(new SubTask("st2", "d", ep1Id));

        tm.getTask(t1Id);
        tm.getTask(t2Id);
        tm.deleteAllTasks();
        assertEquals(tm.getHistory().size(), 0, "история должна быть пустой");

        tm.getSubTask(st1Id);
        tm.getSubTask(st2Id);
        tm.deleteAllSubTasks();
        assertEquals(tm.getHistory().size(), 0, "история должна быть пустой");

        tm.getEpicTask(ep1Id);
        tm.getEpicTask(ep2Id);
        tm.deleteAllEpicTasks();
        assertEquals(tm.getHistory().size(), 0, "история должна быть пустой");
    }
}