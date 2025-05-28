package ru.kermort.praktikum.taskmanager.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.enums.TaskStatus;
import ru.kermort.praktikum.taskmanager.exceptions.ManagerSaveException;
import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @BeforeEach
    void init() {
        try {
            tm = new FileBackedTaskManager(File.createTempFile("test", "tmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void deleteTempFile() {
        try {
            if (Files.exists(tm.getFile().toPath())) {
                Files.delete(tm.getFile().toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void saveAndLoadEmptyFileTest() {
        try {
            tm.save();
            String fileText = Files.readString(tm.getFile().toPath());
            assertTrue(fileText.isEmpty(), "после сохранения менеджера без задач файл должен быть пустой");
        } catch (IOException e) {
            e.printStackTrace();
        }
        tm = FileBackedTaskManager.loadFromFile(tm.getFile());

        assertTrue(tm.getAllTasks().isEmpty() && tm.getAllEpicTasks().isEmpty() && tm.getAllSubTasks().isEmpty(),
                "при загрузке из пустого файла в менеджере не должно быть задач");
    }

    @Test
    void saveAndLoadFromFileWithOneTaskTest() {
        Task task = new Task("Test addTask", "Test addTask description");
        int taskId = tm.addTask(task);
        tm = FileBackedTaskManager.loadFromFile(tm.getFile());
        Task savedTask = tm.getTask(taskId);
        List<Task> tasks = tm.getAllTasks();

        assertNotNull(savedTask, "После восстановления из файла задача не найдена по id.");
        assertEquals(task, savedTask, "После восстановления из файла задачи не совпадают.");
        assertNotNull(tasks, "После восстановления из файла список задач не возвращается.");
        assertEquals(1, tasks.size(), "После восстановления из файла в менеджере неверное количество задач.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "После восстановления из файла статус новой задачи не NEW");
    }

    @Test
    void saveAndLoadFromFileWithOneEpicTaskTest() {
        EpicTask epicTask = new EpicTask("Test addTask", "Test addTask description");
        int epicTaskId = tm.addEpicTask(epicTask);
        tm = FileBackedTaskManager.loadFromFile(tm.getFile());
        EpicTask savedTask = tm.getEpicTask(epicTaskId);
        List<EpicTask> tasks = tm.getAllEpicTasks();

        assertNotNull(savedTask, "После восстановления из файла эпик не найден по id.");
        assertEquals(epicTask, savedTask, "После восстановления из файла эпики не совпадают.");
        assertNotNull(tasks, "После восстановления из файла список эпиков не возвращается.");
        assertEquals(1, tasks.size(), "После восстановления из файла в менеджере неверное количество эпиков.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "После восстановления из файла статус нового эпика не NEW");
    }

    @Test
    void saveAndLoadFromFileWithOneEpicTaskAndOneSubTaskTest() {
        EpicTask epicTask = new EpicTask("Test addEpicTask", "Test addEpicTask description");
        int epicTaskId = tm.addEpicTask(epicTask);
        SubTask subTask = new SubTask("Test addSubTask", "Test addSubTask description", epicTask.getId());
        int subTaskId = tm.addSubTask(subTask);
        tm = FileBackedTaskManager.loadFromFile(tm.getFile());
        SubTask savedSubTask = tm.getSubTask(subTaskId);
        EpicTask savedEpicTask = tm.getEpicTask(epicTaskId);
        List<SubTask> tasks = tm.getAllSubTasks();

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
        int taskId = tm.addTask(task);
        tm = FileBackedTaskManager.loadFromFile(tm.getFile());
        Task taskFromManager = tm.getTask(taskId);

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
        tm = FileBackedTaskManager.loadFromFile(tm.getFile());
        int epicTaskId = tm.addEpicTask(task);
        EpicTask epicTaskFromManager = tm.getEpicTask(epicTaskId);

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
        tm.addEpicTask(epicTask);
        SubTask subTask = new SubTask("Заголовок подзадачи 1", "Описание подзадачи 1", epicTask.getId());
        SubTask subTaskInitialState = new SubTask(subTask);
        subTaskInitialState.setId(2);
        int subTaskId = tm.addSubTask(subTask);
        tm = FileBackedTaskManager.loadFromFile(tm.getFile());
        SubTask subTaskFromManager = tm.getSubTask(subTaskId);

        assertTrue(subTaskInitialState.getId() == subTaskFromManager.getId()
                        && subTaskInitialState.getTitle().equals(subTaskFromManager.getTitle())
                        && subTaskInitialState.getDescription().equals(subTaskFromManager.getDescription())
                        && subTaskInitialState.getParentTaskId() == subTaskFromManager.getParentTaskId()
                        && subTaskInitialState.getStatus() == subTaskFromManager.getStatus(),
                "некоторые поля подзадачи после восстановления из файла отличаются");
    }

    @Test
    void saveAndLoadFromFileWithSomeTaskAndStatusChangesTest() {
        Task t1 = new Task("Заголовок задачи 1", "Описание задачи 1", TEST_TIME, 10);
        int t1Id = tm.addTask(t1);
        Task t2 = new Task("Заголовок задачи 2", "Описание задачи 2");
        int t2Id = tm.addTask(t2);
        EpicTask ep1 = new EpicTask("Заголовок эпика 1", "Описание эпика 1");
        int ep1Id = tm.addEpicTask(ep1);
        SubTask st11 = new SubTask("Заголовок подзадачи 1 эпика 1", "Описание подзадачи 1 эпика 1", ep1Id);
        int st11Id = tm.addSubTask(st11);
        EpicTask ep2 = new EpicTask("Заголовок эпика 2", "Описание эпика 2");
        int ep2Id = tm.addEpicTask(ep2);
        SubTask st21 = new SubTask("Заголовок подзадачи 1 эпика 2", "Описание подзадачи 1 эпика 2", ep2Id, TEST_TIME.plusHours(1), 10);
        SubTask st22 = new SubTask("Заголовок подзадачи 2 эпика 2", "Описание подзадачи 2 эпика 2", ep2Id, TEST_TIME.plusHours(2), 10);
        int st21Id = tm.addSubTask(st21);
        int st22Id = tm.addSubTask(st22);
        List<Task> savedPrioritizedTasks = tm.getPrioritizedTasks();
        tm = FileBackedTaskManager.loadFromFile(tm.getFile());

        assertEquals(savedPrioritizedTasks, tm.getPrioritizedTasks(),
                "После восстановления из файла отсортированный список отличается от начального");
        assertEquals(2, tm.getAllTasks().size(),
                "После восстановления из файла в менеджере неверное количество задач");
        assertTrue(t1.getTitle().equals(tm.getTask(t1Id).getTitle())
                        && t1.getDescription().equals(tm.getTask(t1Id).getDescription())
                        && t1.getStatus() == tm.getTask(t1Id).getStatus()
                        && t1.getStartTime().equals(tm.getTask(t1Id).getStartTime())
                        && t1.getDuration().equals(tm.getTask(t1Id).getDuration()),
                "некоторые поля задачи 1 после восстановления из файла отличаются");
        assertTrue(t2.getTitle().equals(tm.getTask(t2Id).getTitle())
                        && t2.getDescription().equals(tm.getTask(t2Id).getDescription())
                        && t2.getStatus() == tm.getTask(t2Id).getStatus(),
                "некоторые поля задачи 2 после восстановления из файла отличаются");

        assertEquals(2, tm.getAllEpicTasks().size(),
                "После восстановления из файла в менеджере неверное количество эпиков");
        assertTrue(ep1.getTitle().equals(tm.getEpicTask(ep1Id).getTitle())
                        && ep1.getDescription().equals(tm.getEpicTask(ep1Id).getDescription())
                        && ep1.getSubTasksIds().equals(tm.getEpicTask(ep1Id).getSubTasksIds())
                        && ep1.getStatus() == tm.getEpicTask(ep1Id).getStatus(),
                "некоторые поля эпика 1 после восстановления из файла отличаются");
        assertTrue(ep2.getTitle().equals(tm.getEpicTask(ep2Id).getTitle())
                        && ep2.getDescription().equals(tm.getEpicTask(ep2Id).getDescription())
                        && ep2.getSubTasksIds().equals(tm.getEpicTask(ep2Id).getSubTasksIds())
                        && ep2.getStatus() == tm.getEpicTask(ep2Id).getStatus(),
                "некоторые поля эпика после восстановления из файла отличаются");

        assertEquals(3, tm.getAllSubTasks().size(),
                "После восстановления из файла в менеджере неверное количество подзадач");
        assertTrue(st11.getTitle().equals(tm.getSubTask(st11Id).getTitle())
                        && st11.getDescription().equals(tm.getSubTask(st11Id).getDescription())
                        && st11.getParentTaskId() == tm.getSubTask(st11Id).getParentTaskId()
                        && st11.getStatus() == tm.getSubTask(st11Id).getStatus(),
                "некоторые поля подзадачи 1 эпика 1 после восстановления из файла отличаются");
        assertTrue(st21.getTitle().equals(tm.getSubTask(st21Id).getTitle())
                        && st21.getDescription().equals(tm.getSubTask(st21Id).getDescription())
                        && st21.getParentTaskId() == tm.getSubTask(st21Id).getParentTaskId()
                        && st21.getStatus() == tm.getSubTask(st21Id).getStatus()
                        && st21.getStartTime().equals(tm.getSubTask(st21Id).getStartTime())
                        && st21.getDuration().equals(tm.getSubTask(st21Id).getDuration()),
                "некоторые поля подзадачи 1 эпика 2 после восстановления из файла отличаются");
        assertTrue(st22.getTitle().equals(tm.getSubTask(st22Id).getTitle())
                        && st22.getDescription().equals(tm.getSubTask(st22Id).getDescription())
                        && st22.getParentTaskId() == tm.getSubTask(st22Id).getParentTaskId()
                        && st22.getStatus() == tm.getSubTask(st22Id).getStatus()
                        && st22.getStartTime().equals(tm.getSubTask(st22Id).getStartTime())
                        && st22.getDuration().equals(tm.getSubTask(st22Id).getDuration()),
                "некоторые поля подзадачи 2 эпика 2 после восстановления из файла отличаются");

        st11.setStatus(TaskStatus.DONE);
        st21.setStatus(TaskStatus.DONE);
        t1.setStatus(TaskStatus.IN_PROGRESS);
        tm.updateSubTask(st11);
        tm.updateSubTask(st21);
        tm.updateTask(t1);

        tm = FileBackedTaskManager.loadFromFile(tm.getFile());

        assertEquals(TaskStatus.DONE, tm.getEpicTask(ep1Id).getStatus(),
                "После восстановления из файла у эпика 1 неверный статус");
        assertEquals(TaskStatus.DONE, tm.getSubTask(st11Id).getStatus(),
                "После восстановления из файла у эпика 1 неверный статус");
        assertEquals(TaskStatus.IN_PROGRESS, tm.getEpicTask(ep2Id).getStatus(),
                "После восстановления из файла у эпика 2 неверный статус");
        assertEquals(TaskStatus.DONE, tm.getSubTask(st21Id).getStatus(),
                "После восстановления из файла у эпика 1 неверный статус");
        assertEquals(TaskStatus.DONE, tm.getSubTask(st11Id).getStatus(),
                "После восстановления из файла у подзадачи 1 эпика 1 неверный статус");
        assertEquals(TaskStatus.IN_PROGRESS, tm.getTask(t1Id).getStatus(),
                "После восстановления из файла у задачи 1 неверный статус");
    }

    @Test
    public void ioExceptionTest() {
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(new File("//incorrect path//incorrect path")),
                "попытка загрузить состояние менеджера из несуществующего файла должна приводить к исключению");
    }

    @Test
    public void managerSaveExceptionTest() {
        Task t = new Task("t", "d t");
        tm = new FileBackedTaskManager(new File("//incorrect path//incorrect path"));
        assertThrows(ManagerSaveException.class, () -> tm.addTask(t),
                "попытка сохранить состояние менеджера в несуществующий файл должна приводить к исключению");
    }

}
