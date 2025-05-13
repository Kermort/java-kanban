package ru.kermort.praktikum.taskmanager.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.enums.TaskStatus;
import ru.kermort.praktikum.taskmanager.enums.TaskType;
import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {
    FileBackedTaskManager fbtm;

    @Override
    @BeforeEach
    void init() {
        try {
            fbtm = new FileBackedTaskManager(File.createTempFile("test", "tmp"));
            tm = fbtm;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void taskManagerStateAndFileMustBeEquals() {
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
        assertEquals(fbtm.getAllTasks().size() + fbtm.getAllEpicTasks().size() + fbtm.getAllSubTasks().size(), 0,
                "при загрузке из пустого файла в менеджере не должно быть задач");
    }

    @Test
    @Override
    void addNewTask() {
        super.addNewTask();
        List<String> listFromFile = fileToList(fbtm.getFile());
        List<String> listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера записано в файл неправильно");

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());
        listFromFile = fileToList(fbtm.getFile());
        listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера загружено неправильно");
    }

    @Test
    @Override
    void addNewEpicTask() {
        super.addNewEpicTask();
        List<String> listFromFile = fileToList(fbtm.getFile());
        List<String> listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера записано в файл неправильно");

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());
        listFromFile = fileToList(fbtm.getFile());
        listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера загружено неправильно");
    }

    @Test
    @Override
    void addNewSubTask() {
        super.addNewSubTask();
        List<String> listFromFile = fileToList(fbtm.getFile());
        List<String> listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера записано в файл неправильно");

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());
        listFromFile = fileToList(fbtm.getFile());
        listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера загружено неправильно");
    }

    @Test
    @Override
    void subTaskIdShouldBeRemovedFromEpicIfSubTaskRemoved() {
        super.subTaskIdShouldBeRemovedFromEpicIfSubTaskRemoved();
        List<String> listFromFile = fileToList(fbtm.getFile());
        List<String> listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера записано в файл неправильно");

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());
        listFromFile = fileToList(fbtm.getFile());
        listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера загружено неправильно");
    }

    @Test
    @Override
    void changeStatusTest() {
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

        List<String> listFromFile = fileToList(fbtm.getFile());
        List<String> listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера записано в файл неправильно");

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());
        listFromFile = fileToList(fbtm.getFile());
        listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера загружено неправильно");

        st11.setStatus(TaskStatus.DONE);
        st21.setStatus(TaskStatus.DONE);
        fbtm.updateSubTask(st11);
        fbtm.updateSubTask(st21);

        listFromFile = fileToList(fbtm.getFile());
        listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager,
                "после изменения статуса подзадачи состояние менеджера записано в файл неправильно");

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());
        listFromFile = fileToList(fbtm.getFile());
        listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера загружено неправильно");

        fbtm.deleteTask(t2.getId());
        listFromFile = fileToList(fbtm.getFile());
        listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager,
                "после удаления задачи состояние менеджера записано в файл неправильно");

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());
        listFromFile = fileToList(fbtm.getFile());
        listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера загружено неправильно");

        fbtm.deleteEpicTask(ep2.getId());
        listFromFile = fileToList(fbtm.getFile());
        listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager,
                "после удаления эпика состояние менеджера записано в файл неправильно");

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());
        listFromFile = fileToList(fbtm.getFile());
        listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера загружено неправильно");

        fbtm.deleteAllSubTasks();
        listFromFile = fileToList(fbtm.getFile());
        listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager,
                "после удаления всех подзадач состояние менеджера записано в файл неправильно");

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());
        listFromFile = fileToList(fbtm.getFile());
        listFromManager = managerToList(fbtm);
        assertEquals(listFromFile, listFromManager, "состояние менеджера загружено неправильно");
    }

    private static List<String> fileToList(File file) {
        List<String> listFromFile = new ArrayList<>();
        try {
            String fileText = Files.readString(file.toPath());
            String[] arrayFromFile = fileText.split("\n");
            Arrays.sort(arrayFromFile);
            listFromFile = List.of(arrayFromFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listFromFile;
    }

    private static List<String> managerToList(FileBackedTaskManager fbtm) {
        List<String> listFromTaskManager = new ArrayList<>();
        if (fbtm.getAllTasks().size() + fbtm.getAllEpicTasks().size() + fbtm.getAllSubTasks().size() > 0) {
            listFromTaskManager.add(FileBackedTaskManager.HEADER.replace("\n", ""));
        }

        for (Task task: fbtm.getAllTasks()) {
            listFromTaskManager.add(taskToString(task).replace("\n", ""));
        }
        for (EpicTask epicTask: fbtm.getAllEpicTasks()) {
            listFromTaskManager.add(taskToString(epicTask).replace("\n", ""));
        }

        for (SubTask subTask: fbtm.getAllSubTasks()) {
            listFromTaskManager.add(taskToString(subTask).replace("\n", ""));
        }
        Collections.sort(listFromTaskManager);
        return listFromTaskManager;
    }

    private static String taskToString(Task task) {
        String parentTaskId = "";
        if (task.getTaskType() == TaskType.SUBTASK) {
            parentTaskId = String.valueOf(((SubTask) task).getParentTaskId());
        }

        return task.getId() + "," +
                task.getTaskType() + "," +
                task.getTitle() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                parentTaskId + "\n";
    }
}
