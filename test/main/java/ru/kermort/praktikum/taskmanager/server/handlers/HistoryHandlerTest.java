package ru.kermort.praktikum.taskmanager.server.handlers;

import com.google.gson.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.HttpTaskServer;
import ru.kermort.praktikum.taskmanager.manager.InMemoryTaskManager;
import ru.kermort.praktikum.taskmanager.manager.TaskManager;
import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryHandlerTest {
    private final TaskManager tm = new InMemoryTaskManager();
    private final HttpTaskServer hts = new HttpTaskServer(tm);
    private final Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void before() {
        tm.deleteAllTasks();
        tm.deleteAllEpicTasks();
        hts.start();
    }

    @AfterEach
    public void after() {
        hts.stop();
    }

    //Получение истории
    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        Task t1 = new Task("t1", "d t1");
        Task t2 = new Task("t2", "d t2");
        Task t3 = new Task("t3", "d t3");
        EpicTask e1 = new EpicTask("e1", "d e1");
        int t1Id = tm.addTask(t1);
        int t2Id = tm.addTask(t2);
        int t3Id = tm.addTask(t3);
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id);
        int st1Id = tm.addSubTask(st1);
        tm.getTask(t2Id);
        tm.getTask(t3Id);
        tm.getTask(t1Id);
        tm.getEpicTask(e1Id);
        tm.getSubTask(st1Id);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Не верный код ответа");

        JsonElement jElement = JsonParser.parseString(response.body());
        JsonArray jArray = jElement.getAsJsonArray();
        List<Task> history = new ArrayList<>();

        for (JsonElement je: jArray) {
            String type = je.getAsJsonObject().get("taskType").getAsString();
            switch (type) {
                case "TASK" -> history.add(gson.fromJson(je, Task.class));
                case "SUBTASK" -> history.add(gson.fromJson(je, SubTask.class));
                case "EPIC" -> history.add(gson.fromJson(je, EpicTask.class));
            }
        }

        assertTrue(history.get(0).equals(t2) && history.get(1).equals(t3) && history.get(2).equals(t1) &&
                history.get(3).equals(e1) && history.get(4).equals(st1),
                "История содержит список задач в неверном порядке");
    }

    @Test
    public void getEmptyHistoryTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Не верный код ответа");

        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(0, history.length, "История не пуста");
    }
}