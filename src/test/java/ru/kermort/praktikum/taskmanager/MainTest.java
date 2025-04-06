package ru.kermort.praktikum.taskmanager;

import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.manager.InMemoryTaskManager;
import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;
import ru.kermort.praktikum.taskmanager.enums.TaskStatus;
import ru.kermort.praktikum.taskmanager.manager.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testsFromSprint4() {
        InMemoryTaskManager tm = new InMemoryTaskManager();
        Task t1 = new Task("Заголовок задачи 1", "Описание задачи 1");
        int t1Id = tm.addTask(t1);
        Task t2 = new Task("Заголовок задачи 2", "Описание задачи 2");
        int t2Id = tm.addTask(t2);
        assertNotNull(tm.getTask(t1Id), "в менеджере отсутствует добавленная задача");
        assertEquals(t1, tm.getTask(t1Id), "созданная задача и добавленная в менеджер не совпадают");

        EpicTask ep1 = new EpicTask("Заголовок эпика 1", "Описание эпика 1");
        int ep1Id = tm.addEpicTask(ep1);
        SubTask st11 = new SubTask("Заголовок подзадачи 1 эпика 1", "Описание подзадачи 1 эпика 1", ep1);
        int st11Id = tm.addSubTask(st11);
        assertNotNull(tm.getEpicTask(ep1Id), "в менеджере отсутствует добавленный эпик");
        assertEquals(ep1, tm.getEpicTask(ep1Id), "созданный эпик и добавленный в менеджер не совпадают");

        EpicTask ep2 = new EpicTask("Заголовок эпика 2", "Описание эпика 2");
        int ep2Id = tm.addEpicTask(ep2);
        SubTask st21 = new SubTask("Заголовок подзадачи 1 эпика 2", "Описание подзадачи 1 эпика 2", ep2);
        SubTask st22 = new SubTask("Заголовок подзадачи 2 эпика 2", "Описание подзадачи 2 эпика 2", ep2);
        int st21Id = tm.addSubTask(st21);
        int st22Id = tm.addSubTask(st22);
        assertNotNull(tm.getSubTask(st21Id), "в менеджере отсутствет добавленная подзадача");
        assertEquals(st11, tm.getSubTask(st11Id), "созданная подзадача и добавленная в менеджер не совпадают");

        assertNull(tm.getTask(999), "менеджер возвращает задачу по id, который не существует");
        assertNull(tm.getEpicTask(999), "менеджер возвращает эпик по id, который не существует");
        assertNull(tm.getSubTask(999), "менеджер возвращает подзадачу по id, который не существует");

        assertEquals(2, tm.getAllEpicTasks().size(), "getAllEpicTasks возвращает неверное количество эпиков");
        assertEquals(3, tm.getAllSubTasks().size(), "getAllEpicTasks возвращает неверное количество подзадач");
        assertEquals(2, tm.getAllTasks().size(), "getAllTasks возвращает неверное количество задач");

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
    void addNewTask() {
        InMemoryTaskManager tm = new InMemoryTaskManager();
        Task task = new Task("Test addTask", "Test addTask description");
        final int taskId = tm.addTask(task);

        final Task savedTask = tm.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = tm.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
        assertEquals(TaskStatus.NEW, task.getStatus(), "Статус новой задачи не NEW");
    }

    @Test
    void historyTest() {
        InMemoryTaskManager tm = new InMemoryTaskManager();
        Task t1 = new Task("Заголовок задачи 1", "Описание задачи 1");
        int t1Id = tm.addTask(t1);
        Task taskFromManager = tm.getTask(t1Id);
        Task taskFromHistory = tm.getHistory().get(0);
        taskFromHistory.setTitle("Измененный заголовок задачи 1");
        taskFromHistory.setDescription("Измененное описание задачи 1");
        tm.updateTask(taskFromManager);
        Task updatedTaskFromManager = tm.getTask(t1Id);
        assertNotEquals(taskFromHistory.getTitle(), updatedTaskFromManager.getTitle(), "измененная задача в истории тоже изменена");
        assertNotEquals(taskFromHistory.getDescription(), updatedTaskFromManager.getDescription(), "измененная задача в истории тоже изменена");
        System.out.println(updatedTaskFromManager);
        System.out.println(taskFromHistory);
    }

    @Test
    void shouldEqualsIfIdIsEquals() {
        InMemoryTaskManager tm = new InMemoryTaskManager();
        Task task = new Task("Заголовок задачи 1", "Описание задачи 1");
        int taskId = tm.addTask(task);
        Task task1 = tm.getTask(taskId);
        Task task2 = tm.getTask(taskId);
        assertTrue(task1.getId() == task2.getId()
            && task1.getTitle().equals(task2.getTitle())
            && task1.getDescription().equals(task2.getDescription()), "некоторые поля задачи изменились при добавлении ее в менеджер");
    }

    @Test
    void testManagers() {
        assertNotNull(Managers.getDefault(), "Менеджер вернул null вместо InMemoryTaskManager");
        assertNotNull(Managers.getDefaultHistory(), "Менеджер вернул null вместо InMemoryHistoryManager");
    }

    @Test
    void inMemoryTaskManagerTest() {
        InMemoryTaskManager tm = new InMemoryTaskManager();

        Task task = new Task("Заголовок задачи 1", "Описание задачи 1");
        int taskId = tm.addTask(task);
        List<Task> tasks = tm.getAllTasks();
        assertNotNull(tm.getTask(taskId), "InMemoryTaskManager не нашел задачу по id");
        assertEquals(task, tm.getTask(taskId), "задача изменилась при добавлении ее в менеджер");
        assertEquals(1, tasks.size(), "возвращено неверное количество задач");

        EpicTask epicTask = new EpicTask("Заголовок эпика 1", "Описание эпика 1");
        int epicTaskId = tm.addEpicTask(epicTask);
        List<EpicTask> epicTasks = tm.getAllEpicTasks();
        assertNotNull(tm.getEpicTask(epicTaskId), "InMemoryTaskManager не эпик по id");
        assertEquals(epicTask, tm.getEpicTask(epicTaskId), "эпик изменился при добавлении его в менеджер");
        assertEquals(1, epicTasks.size(), "возвращено неверное количество эпиков");

        SubTask subTask = new SubTask("Заголовок подзадачи 1", "Описание подзадачи 1", epicTask);
        int subTaskId = tm.addSubTask(subTask);
        List<SubTask> subTasks = tm.getAllSubTasks();
        assertNotNull(tm.getSubTask(subTaskId), "InMemoryTaskManager не нашел подзадачу по id");
        assertEquals(subTask, tm.getSubTask(subTaskId), "подзадача изменилась при добавлении ее в менеджер");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
    }



}