package ru.kermort.praktikum.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.enums.TaskStatus;
import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest {
    TaskManager tm;

    @BeforeEach
    void init() {
        tm = new InMemoryTaskManager();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addTask", "Test addTask description");
        int taskId = tm.addTask(task);

        Task savedTask = tm.getTask(taskId);
        List<Task> tasks = tm.getAllTasks();

        assertNotNull(savedTask, "Задача не найдена по id.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertNotNull(tasks, "Список задач не возвращается.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статус новой задачи не NEW");
    }

    @Test
    void addNewEpicTask() {
        EpicTask epicTask = new EpicTask("Test addTask", "Test addTask description");
        int epicTaskId = tm.addEpicTask(epicTask);

        EpicTask savedTask = tm.getEpicTask(epicTaskId);
        List<EpicTask> tasks = tm.getAllEpicTasks();

        assertNotNull(savedTask, "Эпик не найден по id.");
        assertEquals(epicTask, savedTask, "Эпикии не совпадают.");
        assertNotNull(tasks, "Список эпиков не возвращается.");
        assertEquals(1, tasks.size(), "В менеджере неверное количество эпиков.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статус нового эпика не NEW");
    }

    @Test
    void addNewSubTask() {
        EpicTask epicTask = new EpicTask("Test addTask", "Test addTask description");
        tm.addEpicTask(epicTask);
        SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", epicTask.getId());
        int subTaskId = tm.addSubTask(subTask);
        SubTask savedTask = tm.getSubTask(subTaskId);
        List<SubTask> tasks = tm.getAllSubTasks();

        assertNotNull(savedTask, "Подзадача не найдена по id.");
        assertEquals(subTask, savedTask, "Подзадачи не совпадают.");
        assertNotNull(tasks, "Список подзадач не возвращается.");
        assertEquals(1, tasks.size(), "В менеджере неверное количество подзадач.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статус новой подзадачи не NEW");
    }

    @Test
    void taskFieldsDontChangeAfterAddToManagerTest() {
        Task task = new Task("Заголовок задачи 1", "Описание задачи 1");
        Task taskInitialState = new Task(task);
        taskInitialState.setId(1);
        int taskId = tm.addTask(task);
        Task taskFromManager = tm.getTask(taskId);

        assertTrue(taskInitialState.getId() == taskFromManager.getId()
                && taskInitialState.getTitle().equals(taskFromManager.getTitle())
                && taskInitialState.getDescription().equals(taskFromManager.getDescription()),
                "некоторые поля задачи изменились при добавлении ее в менеджер");
    }

    @Test
    void epicTaskFieldsDontChangeAfterAddToManagerTest() {
        EpicTask task = new EpicTask("Заголовок эпика 1", "Описание эпика 1");
        EpicTask epicTaskInitialState = new EpicTask(task);
        epicTaskInitialState.setId(1);
        int epicTaskId = tm.addEpicTask(task);
        EpicTask epicTaskFromManager = tm.getEpicTask(epicTaskId);

        assertTrue(epicTaskInitialState.getId() == epicTaskFromManager.getId()
                && epicTaskInitialState.getTitle().equals(epicTaskFromManager.getTitle())
                && epicTaskInitialState.getDescription().equals(epicTaskFromManager.getDescription())
                && epicTaskInitialState.getSubTasksIds().equals(epicTaskFromManager.getSubTasksIds()),
                "некоторые поля эпика изменились при добавлении ее в менеджер");
    }

    @Test
    void subTaskFieldsDontChangeAfterAddToManagerTest() {
        EpicTask epicTask = new EpicTask("Заголовок эпика 1", "Описание эпика 1");
        tm.addEpicTask(epicTask);
        SubTask subTask = new SubTask("Заголовок подзадачи 1", "Описание подзадачи 1", epicTask.getId());
        SubTask subTaskInitialState = new SubTask(subTask);
        subTaskInitialState.setId(2);
        int subTaskId = tm.addSubTask(subTask);
        SubTask subTaskFromManager = tm.getSubTask(subTaskId);

        assertTrue(subTaskInitialState.getId() == subTaskFromManager.getId()
                && subTaskInitialState.getTitle().equals(subTaskFromManager.getTitle())
                && subTaskInitialState.getDescription().equals(subTaskFromManager.getDescription())
                && subTaskInitialState.getParentTaskId() == subTaskFromManager.getParentTaskId(),
                "некоторые поля подзадачи изменились при добавлении ее в менеджер");
    }

    @Test
    void nonExistentTasksTest() {
        assertNull(tm.getTask(999), "менеджер возвращает задачу по id, который не существует");
        assertNull(tm.getEpicTask(999), "менеджер возвращает эпик по id, который не существует");
        assertNull(tm.getSubTask(999), "менеджер возвращает подзадачу по id, который не существует");
    }

    @Test
    void changeStatusTest() {
        Task t1 = new Task("Заголовок задачи 1", "Описание задачи 1");
        int t1Id = tm.addTask(t1);
        Task t2 = new Task("Заголовок задачи 2", "Описание задачи 2");
        int t2Id = tm.addTask(t2);

        EpicTask ep1 = new EpicTask("Заголовок эпика 1", "Описание эпика 1");
        int ep1Id = tm.addEpicTask(ep1);
        SubTask st11 = new SubTask("Заголовок подзадачи 1 эпика 1", "Описание подзадачи 1 эпика 1", ep1.getId());
        int st11Id = tm.addSubTask(st11);

        EpicTask ep2 = new EpicTask("Заголовок эпика 2", "Описание эпика 2");
        int ep2Id = tm.addEpicTask(ep2);
        SubTask st21 = new SubTask("Заголовок подзадачи 1 эпика 2", "Описание подзадачи 1 эпика 2", ep2.getId());
        SubTask st22 = new SubTask("Заголовок подзадачи 2 эпика 2", "Описание подзадачи 2 эпика 2", ep2.getId());
        int st21Id = tm.addSubTask(st21);
        int st22Id = tm.addSubTask(st22);

        st11.setStatus(TaskStatus.DONE);
        st21.setStatus(TaskStatus.DONE);
        tm.updateSubTask(st11);
        tm.updateSubTask(st21);

        assertEquals(TaskStatus.DONE, ep1.getStatus(), "Статус эпика 1 не изменился на DONE");
        assertEquals(TaskStatus.IN_PROGRESS, ep2.getStatus(), "Статус эпика 2 не изменился на IN_PROGRESS");

        tm.deleteTask(t2.getId());
        assertEquals(1, tm.getAllTasks().size(), "Обычная задача должна остаться одна");

        tm.deleteEpicTask(ep2.getId());
        assertEquals(1, tm.getAllSubTasks().size(), "Должна остаться только одна подзадача");

        tm.deleteAllSubTasks();
        assertEquals(TaskStatus.NEW, ep1.getStatus(), "Эпик 1 должен стать NEW");
    }

    @Test
    void subTaskIdShouldBeRemovedFromEpicIfSubTaskRemoved() {
        int ep1Id = tm.addEpicTask(new EpicTask("epic title 1", "epic description 1"));
        int st1Id = tm.addSubTask(new SubTask("sub task title 1", "sub task description 1", ep1Id));
        tm.deleteSubTask(st1Id);

        assertFalse(tm.getEpicTask(ep1Id).getSubTasksIds().contains(st1Id),
                "в эпике остался id подзадачи после ее удаления");
    }
}
