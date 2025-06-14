package ru.kermort.praktikum.taskmanager.server.handlers;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.HttpTaskServer;
import ru.kermort.praktikum.taskmanager.manager.InMemoryTaskManager;
import ru.kermort.praktikum.taskmanager.manager.TaskManager;
import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SubTaskHandlerTest {
    private final TaskManager tm = new InMemoryTaskManager();
    private final HttpTaskServer hts = new HttpTaskServer(tm);
    private final Gson gson = HttpTaskServer.getGson();
    private final LocalDateTime TEST_TIME = LocalDateTime.of(2025, 6, 1, 10, 0);

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

    //Получение всех подзадач
    @Test
    public void getSubTasksTest() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id);
        SubTask st2 = new SubTask("st2", "d st2", e1Id, TEST_TIME, 15);
        tm.addSubTask(st1);
        tm.addSubTask(st2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Не верный код ответа");
        SubTask[] subTasks = gson.fromJson(response.body(), SubTask[].class);

        assertTrue(st1.getTitle().equals(subTasks[0].getTitle()) &&
                st1.getDescription().equals(subTasks[0].getDescription()) &&
                st1.getParentTaskId() == subTasks[0].getParentTaskId() &&
                st1.getId() == subTasks[0].getId(), "Подзадачи не совпадают");

        assertTrue(st2.getTitle().equals(subTasks[1].getTitle()) &&
                st2.getDescription().equals(subTasks[1].getDescription()) &&
                st2.getParentTaskId() == subTasks[1].getParentTaskId() &&
                st2.getId() == subTasks[1].getId() &&
                st2.getStartTime().equals(subTasks[1].getStartTime()) &&
                st2.getDuration().equals(subTasks[1].getDuration()), "Подзадачи не совпадают");
    }

    //Получение подзадачи без времени начала и длительности
    @Test
    public void getSubTaskTest1() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id);
        int st1Id = tm.addSubTask(st1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + st1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask subTask = gson.fromJson(response.body(), SubTask.class);

        assertEquals(200, response.statusCode(), "Не верный код ответа");

        assertTrue(st1.getTitle().equals(subTask.getTitle()) &&
                st1.getDescription().equals(subTask.getDescription()) &&
                st1.getParentTaskId() == subTask.getParentTaskId() &&
                st1.getId() == subTask.getId(), "Задачи не совпадают");
    }

    //Получение подзадачи c временем начала и длительностью
    @Test
    public void getSubTaskTest2() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id, TEST_TIME, 15);
        int st1Id = tm.addSubTask(st1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + st1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask subTask = gson.fromJson(response.body(), SubTask.class);

        assertEquals(200, response.statusCode(), "Не верный код ответа");

        assertTrue(st1.getTitle().equals(subTask.getTitle()) &&
                st1.getDescription().equals(subTask.getDescription()) &&
                st1.getParentTaskId() == subTask.getParentTaskId() &&
                st1.getStartTime().equals(subTask.getStartTime()) &&
                st1.getDuration().equals(subTask.getDuration()) &&
                st1.getId() == subTask.getId(), "Задачи не совпадают");
    }

    //Попытка получения подзадачи, отсутствующей в менеджере
    @Test
    public void getSubTaskNotFoundTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "При попытке получить подзадачу по несуществующему id должна быть ошибка");
    }

    //Добавление подзадачи без даты начала и длительности
    @Test
    public void postAddSubTaskTest1() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id);
        String subTaskJson = gson.toJson(st1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask subTaskFromManager = tm.getSubTask(2);
        assertEquals(201, response.statusCode(), "Не верный код ответа");

        assertTrue(st1.getTitle().equals(subTaskFromManager.getTitle()) &&
                st1.getDescription().equals(subTaskFromManager.getDescription()) &&
                st1.getParentTaskId() == subTaskFromManager.getParentTaskId(),
                "Задачи не совпадают");
    }

    //Добавление подзадачи с датой начала и длительностью
    @Test
    public void postAddSubTaskTest2() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id, TEST_TIME, 15);
        String subTaskJson = gson.toJson(st1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask subTaskFromManager = tm.getSubTask(2);
        assertEquals(201, response.statusCode(), "Не верный код ответа");

        assertTrue(st1.getTitle().equals(subTaskFromManager.getTitle()) &&
                        st1.getDescription().equals(subTaskFromManager.getDescription()) &&
                st1.getParentTaskId() == subTaskFromManager.getParentTaskId(),
                "Задачи не совпадают");
    }

    //Попытка добавить подзадачу, пересекающуюся с существующей
    @Test
    public void postAddCrossSubTaskTest() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id, TEST_TIME, 15);
        SubTask st2 = new SubTask("st2", "d st2", e1Id, TEST_TIME, 5);
        tm.addSubTask(st1);
        String subTaskJson = gson.toJson(st2);
        st2.setId(3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "При пересекающихся задачах статус ответа должен быть 406");
        assertEquals(1, tm.getAllSubTasks().size(), "При пересечении задача не должна быть добавлена");
    }

    //Обновление подзадачи с добавлением времени начала и длительности
    @Test
    public void postUpdateSubTaskTest() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id);
        int st1Id = tm.addSubTask(st1);
        SubTask st1ed = new SubTask("st1ed", "d st1ed", e1Id, TEST_TIME, 15);
        st1ed.setId(2);
        String subTaskJson = gson.toJson(st1ed);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + st1ed.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        SubTask subTaskFromManager = tm.getSubTask(2);
        assertEquals(201, response.statusCode(), "Не верный код ответа");

        assertFalse(st1.getTitle().equals(subTaskFromManager.getTitle()) ||
                st1.getDescription().equals(subTaskFromManager.getDescription()), "Подзадачи совпадают");

        assertTrue(subTaskFromManager.getStartTime().equals(TEST_TIME) &&
                subTaskFromManager.getDuration().equals(Duration.ofMinutes(15)), "В подзадаче не обновлено время и/или длительность");
    }

    //Попытка обновить подзадачу, передав в url id, который отличается от id в json
    @Test
    public void postUpdateTaskTest2() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id);
        SubTask st2 = new SubTask("st2", "d st2", e1Id, TEST_TIME, 15);
        int st1Id = tm.addTask(st1);
        int st2Id = tm.addTask(st2);

        String taskJson = gson.toJson(st2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + st1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Если id подзадачи в url и в json не совпадают, должна быть ощибка");
    }

    //Попытка обновить подзадачу таким образом, что она будет пересекаться с существующей
    @Test
    public void postUpdateCrossTaskTest() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id, TEST_TIME, 15);
        SubTask st2 = new SubTask("st2", "d st2", e1Id);
        SubTask st2ed = new SubTask("st2", "d st2", e1Id, TEST_TIME, 5);
        int st1Id = tm.addSubTask(st1);
        int st2Id = tm.addSubTask(st2);

        st2ed.setId(st2Id);
        String subTaskJson = gson.toJson(st2ed);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + st2Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "При пересекающихся задачах статус ответа должен быть 406");
        SubTask subTaskFromManager = tm.getSubTask(st2Id);
        assertTrue(subTaskFromManager.getTitle().equals(st2.getTitle()) &&
                subTaskFromManager.getDescription().equals(st2.getDescription()) &&
                subTaskFromManager.getParentTaskId() == st2.getParentTaskId() &&
                subTaskFromManager.getStartTime() == null &&
                subTaskFromManager.getDuration() == null, "При пересечении задача не должна быть обновлена");
    }

    //Удаление подзадачи
    @Test
    public void postDeleteSubTaskTest() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id);
        int st1Id = tm.addSubTask(st1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + st1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Не верный код ответа");

        assertEquals(0, tm.getAllTasks().size(), "Подадача не удалена");
    }
}
