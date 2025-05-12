package ru.kermort.praktikum.taskmanager.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        if (fbtm.getAllTasks().size() == 0 && fbtm.getAllEpicTasks().size() == 0 && fbtm.getAllSubTasks().size() == 0) {
            return;
        }

        List<String> listFromTaskManager = new ArrayList<>();
        listFromTaskManager.add("id,type,name,status,description,epic");
        List<String> listFromFile = new ArrayList<>();
        for (Task task: fbtm.getAllTasks()) {
            listFromTaskManager.add(fbtm.toString(task).replace("\n", ""));
        }
        for (EpicTask epicTask: fbtm.getAllEpicTasks()) {
            listFromTaskManager.add(fbtm.toString(epicTask).replace("\n", ""));
        }

        for (SubTask subTask: fbtm.getAllSubTasks()) {
            listFromTaskManager.add(fbtm.toString(subTask).replace("\n", ""));
        }
        Collections.sort(listFromTaskManager);

        try {
            String fileText = Files.readString(fbtm.getFile().toPath());
            String[] arrayFromFile = fileText.split("\n");
            Arrays.sort(arrayFromFile);
            listFromFile = List.of(arrayFromFile);
            Files.delete(fbtm.getFile().toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(listFromFile, listFromTaskManager, "состояние менеджера отличается от записанного в файл");
    }

    @Test
    void saveAndLoadEmptyFileTest() {
        fbtm.deleteAllTasks();
        try {
            String fileText = Files.readString(fbtm.getFile().toPath());
            assertEquals(fileText, "id,type,name,status,description,epic\n",
                    "файл после сохранения пустого менеджера должен содержать только заголовок");
            File file = fbtm.getFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        fbtm = FileBackedTaskManager.loadFromFile(fbtm.getFile());
        assertEquals(fbtm.getAllTasks().size() + fbtm.getAllEpicTasks().size() + fbtm.getAllSubTasks().size(), 0,
                "при загрузке из пустого файла в менеджере не должно быть задач");

    }

    @Test
    void loadFromFileTest() {
        BufferedWriter writer = null;
        File file = null;
        try {
            file =  new File(File.createTempFile("test", "tmp").toString());
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("id,type,name,status,description,epic\n" +
                    "1,TASK,t1,NEW,d,\n" +
                    "2,TASK,t2,NEW,d,\n" +
                    "3,EPIC,ep1,NEW,d,\n" +
                    "4,EPIC,ep2,NEW,d,\n" +
                    "5,SUBTASK,st1,NEW,d,3\n" +
                    "6,SUBTASK,st2,NEW,d,3\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        fbtm = FileBackedTaskManager.loadFromFile(file);
        assertEquals(fbtm.getAllTasks().size(), 2, "в менеджер загружено неверное количество задач");
        assertEquals(fbtm.getAllEpicTasks().size(), 2, "в менеджер загружено неверное количество эпиков");
        assertEquals(fbtm.getAllSubTasks().size(), 2, "в менеджер загружено неверное количество подзадач");
    }
}
