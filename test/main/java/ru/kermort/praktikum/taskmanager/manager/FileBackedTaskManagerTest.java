package ru.kermort.praktikum.taskmanager.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.enums.TaskStatus;
import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager fbtm;

    @BeforeEach
    void init() {
        try {
            fbtm = new FileBackedTaskManager(File.createTempFile("test", "tmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void deleteTempFile() {
        try {
            if (Files.exists(fbtm.getFile().toPath())) {
                Files.delete(fbtm.getFile().toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void saveAndLoadEmptyFileTest() {
        try {
            fbtm.save();
            String fileText = Files.readString(fbtm.getFile().toPath());
            assertTrue(fileText.isEmpty(), "после сохранения менеджера без задач файл должен быть пустой");
        } catch (IOException e) {
            e.printStackTrace();
        }

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());
        assertTrue(fbtm.getAllTasks().isEmpty() && fbtm.getAllEpicTasks().isEmpty() && fbtm.getAllSubTasks().isEmpty(),
                "при загрузке из пустого файла в менеджере не должно быть задач");
    }

    @Test
    void saveAndLoadFromFileWithOneTaskTest() {
        Task task = new Task("Test addTask", "Test addTask description");
        int taskId = fbtm.addTask(task);

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());

        Task savedTask = fbtm.getTask(taskId);
        List<Task> tasks = fbtm.getAllTasks();

        assertNotNull(savedTask, "После восстановления из файла задача не найдена по id.");
        assertEquals(task, savedTask, "После восстановления из файла задачи не совпадают.");
        assertNotNull(tasks, "После восстановления из файла список задач не возвращается.");
        assertEquals(1, tasks.size(), "После восстановления из файла в менеджере неверное количество задач.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "После восстановления из файла статус новой задачи не NEW");
    }

    @Test
    void saveAndLoadFromFileWithOneEpicTaskTest() {
        EpicTask epicTask = new EpicTask("Test addTask", "Test addTask description");
        int epicTaskId = fbtm.addEpicTask(epicTask);

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());

        EpicTask savedTask = fbtm.getEpicTask(epicTaskId);
        List<EpicTask> tasks = fbtm.getAllEpicTasks();

        assertNotNull(savedTask, "После восстановления из файла эпик не найден по id.");
        assertEquals(epicTask, savedTask, "После восстановления из файла эпики не совпадают.");
        assertNotNull(tasks, "После восстановления из файла список эпиков не возвращается.");
        assertEquals(1, tasks.size(), "После восстановления из файла в менеджере неверное количество эпиков.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "После восстановления из файла статус нового эпика не NEW");
    }

    @Test
    void saveAndLoadFromFileWithOneEpicTaskAndOneSubTaskTest() {
        EpicTask epicTask = new EpicTask("Test addEpicTask", "Test addEpicTask description");
        int epicTaskId = fbtm.addEpicTask(epicTask);
        SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", epicTask.getId());
        int subTaskId = fbtm.addSubTask(subTask);

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());

        SubTask savedSubTask = fbtm.getSubTask(subTaskId);
        EpicTask savedEpicTask = fbtm.getEpicTask(epicTaskId);
        List<SubTask> tasks = fbtm.getAllSubTasks();

        assertNotNull(savedSubTask, "После восстановления из файла подзадача не найдена по id.");
        assertEquals(subTask, savedSubTask, "После восстановления из файла подзадачи не совпадают.");
        assertNotNull(tasks, "После восстановления из файла список подзадач не возвращается.");
        assertEquals(1, tasks.size(), "После восстановления из файла в менеджере неверное количество подзадач.");
        assertEquals(TaskStatus.NEW, savedSubTask.getStatus(), "После восстановления из файла статус новой подзадачи не NEW");
        assertEquals(savedSubTask.getParentTaskId(), epicTaskId,
                "После восстановления из файла в подзадаче содержится неверный id родительского эпика");
        assertEquals(savedEpicTask.getSubTasksIds().get(0), subTaskId,
                "После восстановления из файла в эпике содержится неверный id подзадачи");
    }

    @Test
    void taskFieldsShouldNotChangeAfterLoadFromFileTest() {
        Task task = new Task("Заголовок задачи 1", "Описание задачи 1");
        Task taskInitialState = new Task(task);
        taskInitialState.setId(1);
        int taskId = fbtm.addTask(task);

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());

        Task taskFromManager = fbtm.getTask(taskId);

        assertTrue(taskInitialState.getId() == taskFromManager.getId()
                        && taskInitialState.getTitle().equals(taskFromManager.getTitle())
                        && taskInitialState.getDescription().equals(taskFromManager.getDescription())
                        && taskInitialState.getStatus() == taskFromManager.getStatus(),
                "некоторые поля задачи после восстановления из файла отличаются");
    }

    @Test
    void epicTaskFieldsShouldNotChangeAfterLoadFromFileTest() {
        EpicTask task = new EpicTask("Заголовок эпика 1", "Описание эпика 1");
        EpicTask epicTaskInitialState = new EpicTask(task);
        epicTaskInitialState.setId(1);

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());

        int epicTaskId = fbtm.addEpicTask(task);
        EpicTask epicTaskFromManager = fbtm.getEpicTask(epicTaskId);

        assertTrue(epicTaskInitialState.getId() == epicTaskFromManager.getId()
                        && epicTaskInitialState.getTitle().equals(epicTaskFromManager.getTitle())
                        && epicTaskInitialState.getDescription().equals(epicTaskFromManager.getDescription())
                        && epicTaskInitialState.getSubTasksIds().equals(epicTaskFromManager.getSubTasksIds())
                        && epicTaskInitialState.getStatus() == epicTaskFromManager.getStatus(),
                "некоторые поля эпика после восстановления из файла отличаются");
    }

    @Test
    void subTaskFieldsShouldNotChangeAfterLoadFromFileTest() {
        EpicTask epicTask = new EpicTask("Заголовок эпика 1", "Описание эпика 1");
        fbtm.addEpicTask(epicTask);
        SubTask subTask = new SubTask("Заголовок подзадачи 1", "Описание подзадачи 1", epicTask.getId());
        SubTask subTaskInitialState = new SubTask(subTask);
        subTaskInitialState.setId(2);
        int subTaskId = fbtm.addSubTask(subTask);

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());

        SubTask subTaskFromManager = fbtm.getSubTask(subTaskId);

        assertTrue(subTaskInitialState.getId() == subTaskFromManager.getId()
                        && subTaskInitialState.getTitle().equals(subTaskFromManager.getTitle())
                        && subTaskInitialState.getDescription().equals(subTaskFromManager.getDescription())
                        && subTaskInitialState.getParentTaskId() == subTaskFromManager.getParentTaskId()
                        && subTaskInitialState.getStatus() == subTaskFromManager.getStatus(),
                "некоторые поля подзадачи после восстановления из файла отличаются");
    }

    @Test
    void saveAndLoadFromFileWithSomeTaskAndStatusChangesTest() {
        Task t1 = new Task("Заголовок задачи 1", "Описание задачи 1");
        int t1Id = fbtm.addTask(t1);
        Task t2 = new Task("Заголовок задачи 2", "Описание задачи 2");
        int t2Id = fbtm.addTask(t2);

        EpicTask ep1 = new EpicTask("Заголовок эпика 1", "Описание эпика 1");
        int ep1Id = fbtm.addEpicTask(ep1);
        SubTask st11 = new SubTask("Заголовок подзадачи 1 эпика 1", "Описание подзадачи 1 эпика 1", ep1.getId());
        int st11Id = fbtm.addSubTask(st11);

        EpicTask ep2 = new EpicTask("Заголовок эпика 2", "Описание эпика 2");
        int ep2Id = fbtm.addEpicTask(ep2);
        SubTask st21 = new SubTask("Заголовок подзадачи 1 эпика 2", "Описание подзадачи 1 эпика 2", ep2.getId());
        SubTask st22 = new SubTask("Заголовок подзадачи 2 эпика 2", "Описание подзадачи 2 эпика 2", ep2.getId());
        int st21Id = fbtm.addSubTask(st21);
        int st22Id = fbtm.addSubTask(st22);

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());

        assertEquals(2, fbtm.getAllTasks().size(),
                "После восстановления из файла в менеджере неверное количество задач");
        assertTrue(t1.getTitle().equals(fbtm.getTask(t1Id).getTitle())
                        && t1.getDescription().equals(fbtm.getTask(t1Id).getDescription())
                        && t1.getStatus() == fbtm.getTask(t1Id).getStatus(),
                "некоторые поля задачи 1 после восстановления из файла отличаются");
        assertTrue(t2.getTitle().equals(fbtm.getTask(t2Id).getTitle())
                        && t2.getDescription().equals(fbtm.getTask(t2Id).getDescription())
                        && t2.getStatus() == fbtm.getTask(t2Id).getStatus(),
                "некоторые поля задачи 2 после восстановления из файла отличаются");

        assertEquals(2, fbtm.getAllEpicTasks().size(),
                "После восстановления из файла в менеджере неверное количество эпиков");
        assertTrue(ep1.getTitle().equals(fbtm.getEpicTask(ep1Id).getTitle())
                        && ep1.getDescription().equals(fbtm.getEpicTask(ep1Id).getDescription())
                        && ep1.getSubTasksIds().equals(fbtm.getEpicTask(ep1Id).getSubTasksIds())
                        && ep1.getStatus() == fbtm.getEpicTask(ep1Id).getStatus(),
                "некоторые поля эпика 1 после восстановления из файла отличаются");
        assertTrue(ep2.getTitle().equals(fbtm.getEpicTask(ep2Id).getTitle())
                        && ep2.getDescription().equals(fbtm.getEpicTask(ep2Id).getDescription())
                        && ep2.getSubTasksIds().equals(fbtm.getEpicTask(ep2Id).getSubTasksIds())
                        && ep2.getStatus() == fbtm.getEpicTask(ep2Id).getStatus(),
                "некоторые поля эпика после восстановления из файла отличаются");

        assertEquals(3, fbtm.getAllSubTasks().size(),
                "После восстановления из файла в менеджере неверное количество подзадач");
        assertTrue(st11.getTitle().equals(fbtm.getSubTask(st11Id).getTitle())
                        && st11.getDescription().equals(fbtm.getSubTask(st11Id).getDescription())
                        && st11.getParentTaskId() == fbtm.getSubTask(st11Id).getParentTaskId()
                        && st11.getStatus() == fbtm.getSubTask(st11Id).getStatus(),
                "некоторые поля подзадачи 1 эпика 1 после восстановления из файла отличаются");
        assertTrue(st21.getTitle().equals(fbtm.getSubTask(st21Id).getTitle())
                        && st21.getDescription().equals(fbtm.getSubTask(st21Id).getDescription())
                        && st21.getParentTaskId() == fbtm.getSubTask(st21Id).getParentTaskId()
                        && st21.getStatus() == fbtm.getSubTask(st21Id).getStatus(),
                "некоторые поля подзадачи 1 эпика 2 после восстановления из файла отличаются");
        assertTrue(st22.getTitle().equals(fbtm.getSubTask(st22Id).getTitle())
                        && st22.getDescription().equals(fbtm.getSubTask(st22Id).getDescription())
                        && st22.getParentTaskId() == fbtm.getSubTask(st22Id).getParentTaskId()
                        && st22.getStatus() == fbtm.getSubTask(st22Id).getStatus(),
                "некоторые поля подзадачи 2 эпика 2 после восстановления из файла отличаются");

        st11.setStatus(TaskStatus.DONE);
        st21.setStatus(TaskStatus.DONE);
        t1.setStatus(TaskStatus.IN_PROGRESS);
        fbtm.updateSubTask(st11);
        fbtm.updateSubTask(st21);
        fbtm.updateTask(t1);

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());

        assertEquals(TaskStatus.DONE, fbtm.getEpicTask(ep1Id).getStatus(),
                "После восстановления из файла у эпика 1 неверный статус");
        assertEquals(TaskStatus.DONE, fbtm.getSubTask(st11Id).getStatus(),
                "После восстановления из файла у эпика 1 неверный статус");
        assertEquals(TaskStatus.IN_PROGRESS, fbtm.getEpicTask(ep2Id).getStatus(),
                "После восстановления из файла у эпика 2 неверный статус");
        assertEquals(TaskStatus.DONE, fbtm.getSubTask(st21Id).getStatus(),
                "После восстановления из файла у эпика 1 неверный статус");
        assertEquals(TaskStatus.DONE, fbtm.getSubTask(st11Id).getStatus(),
                "После восстановления из файла у подзадачи 1 эпика 1 неверный статус");
        assertEquals(TaskStatus.IN_PROGRESS, fbtm.getTask(t1Id).getStatus(),
                "После восстановления из файла у задачи 1 неверный статус");
    }

}
