package ru.kermort.praktikum.taskmanager.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.HttpTaskServer;
import ru.kermort.praktikum.taskmanager.manager.InMemoryTaskManager;
import ru.kermort.praktikum.taskmanager.manager.TaskManager;
import ru.kermort.praktikum.taskmanager.tasks.Task;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerTest {
    private final TaskManager tm = new InMemoryTaskManager();
    private final HttpTaskServer hts = new HttpTaskServer(tm);
    private final Gson gson = HttpTaskServer.getGson();

    @Test
    public void jsonTypeAdaptersTest() {
        Task t1 = new Task("t1", "d t1");
        Task t2 = new Task("t2", "d t2", LocalDateTime.of(2024, 6, 1, 10, 0), 30);
        int t1Id = tm.addTask(t1);
        int t2Id = tm.addTask(t2);

        String t1Json = gson.toJson(t1);
        Task t1FromJson = gson.fromJson(t1Json, Task.class);
        assertTrue(t1.getId() == t1FromJson.getId()
                        && t1.getTitle().equals(t1FromJson.getTitle())
                        && t1.getDescription().equals(t1FromJson.getDescription())
                        && t1.getStatus() == t1FromJson.getStatus(),
                "сериализация/десериализация прошла неверно для задачи без начального времени и длительности");

        String t2Json = gson.toJson(t2);
        Task t2FromJson = gson.fromJson(t2Json, Task.class);
        assertTrue(t2.getId() == t2FromJson.getId()
                        && t2.getTitle().equals(t2FromJson.getTitle())
                        && t2.getDescription().equals(t2FromJson.getDescription())
                        && t2.getStatus() == t2FromJson.getStatus()
                        && t2.getStartTime().equals(t2FromJson.getStartTime())
                        && t2.getDuration().equals(t2FromJson.getDuration()),
                "сериализация/десериализация прошла неверно для задачи с начальным временем и длительности");
    }
}
