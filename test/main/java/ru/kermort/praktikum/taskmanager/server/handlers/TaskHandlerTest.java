package ru.kermort.praktikum.taskmanager.server.handlers;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.HttpTaskServer;
import ru.kermort.praktikum.taskmanager.manager.InMemoryTaskManager;
import ru.kermort.praktikum.taskmanager.manager.TaskManager;
import ru.kermort.praktikum.taskmanager.tasks.Task;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest {
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

    //Получение всех задач
    @Test
    public void getTasksTest() throws IOException, InterruptedException {
        Task t1 = new Task("t1", "d t1");
        Task t2 = new Task("t2", "d t2", TEST_TIME, 15);
        tm.addTask(t1);
        tm.addTask(t2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Не верный код ответа");

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertTrue(t1.getTitle().equals(tasks[0].getTitle()) &&
                t1.getDescription().equals(tasks[0].getDescription()) &&
                t1.getId() == tasks[0].getId(), "Задачи не совпадают");

        assertTrue(t2.getTitle().equals(tasks[1].getTitle()) &&
                t2.getDescription().equals(tasks[1].getDescription()) &&
                t2.getId() == tasks[1].getId() &&
                t2.getStartTime().equals(tasks[1].getStartTime()) &&
                t2.getDuration().equals(tasks[1].getDuration()), "Задачи не совпадают");
    }

    //Получение одной задачи без даты начала и длительности
    @Test
    public void getTaskTest1() throws IOException, InterruptedException {
        Task t1 = new Task("t1", "d t1");
        int t1Id = tm.addTask(t1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + t1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode(), "Не верный код ответа");

        assertTrue(t1.getTitle().equals(task.getTitle()) &&
                t1.getDescription().equals(task.getDescription()) &&
                t1.getId() == task.getId(), "Задачи не совпадают");
    }

    //Получение одной задачи с датой начала и длительностью
    @Test
    public void getTaskTest2() throws IOException, InterruptedException {
        Task t1 = new Task("t1", "d t1", TEST_TIME, 15);
        int t1Id = tm.addTask(t1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + t1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode(), "Не верный код ответа");

        assertTrue(t1.getTitle().equals(task.getTitle()) &&
                t1.getDescription().equals(task.getDescription()) &&
                t1.getId() == task.getId() &&
                t1.getStartTime().equals(task.getStartTime()) &&
                t1.getDuration().equals(task.getDuration()), "Задачи не совпадают");
    }

    //Попытка получения задачи, отсутствующей в менеджере
    @Test
    public void getTaskNotFoundTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "При попытке получить задачу по несуществующему id должна быть ошибка");
    }

    //Добавление задачи без даты начала и длительности
    @Test
    public void postAddTaskTest() throws IOException, InterruptedException {
        Task t1 = new Task("t1", "d t1");
        String taskJson = gson.toJson(t1);
        t1.setId(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFromManager = tm.getTask(1);
        assertEquals(201, response.statusCode(), "Не верный код ответа");

        assertTrue(t1.getTitle().equals(taskFromManager.getTitle()) &&
                t1.getDescription().equals(taskFromManager.getDescription()) &&
                t1.getId() == taskFromManager.getId(), "Задачи не совпадают");
    }

    //Обновление задачи с добавлением времени начала и длительности
    @Test
    public void postUpdateTaskTest() throws IOException, InterruptedException {
        Task t1 = new Task("t1", "d t1");
        int t1Id = tm.addTask(t1);
        Task t2 = new Task("t2", "d t2", TEST_TIME, 15);
        t2.setId(1);
        String taskJson = gson.toJson(t2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + t1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task taskFromManager = tm.getTask(1);
        assertEquals(201, response.statusCode(), "Не верный код ответа");

        assertFalse(t1.getTitle().equals(taskFromManager.getTitle()) ||
                t1.getDescription().equals(taskFromManager.getDescription()), "Задачи совпадают");

        assertTrue(taskFromManager.getStartTime().equals(TEST_TIME) &&
                taskFromManager.getDuration().equals(Duration.ofMinutes(15)), "В задаче не обновлено время и/или длительность");
    }

    //Попытка обновить задачу, передав в url id, который отличается от id в json
    @Test
    public void postUpdateTaskTest2() throws IOException, InterruptedException {
        Task t1 = new Task("t1", "d t1");
        Task t2 = new Task("t2", "d t2", TEST_TIME, 15);
        int t1Id = tm.addTask(t1);
        int t2Id = tm.addTask(t2);

        String taskJson = gson.toJson(t2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + t1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Если id задачи в url и в json не совпадают, должна быть ощибка");
    }

    //Попытка добавить задачу, пересекающуюся с существующей
    @Test
    public void postAddCrossTaskTest() throws IOException, InterruptedException {
        Task t1 = new Task("t1", "d t1", TEST_TIME, 15);
        Task t2 = new Task("t2", "d t2", TEST_TIME, 5);
        String taskJson = gson.toJson(t1);
        t1.setId(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        taskJson = gson.toJson(t2);
        t2.setId(2);

        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response2.statusCode(), "При пересекающихся задачах статус ответа должен быть 406");
        assertEquals(1, tm.getAllTasks().size(), "При пересечении задача не должна быть добавлена");
    }

    //Попытка обновить задачу таким образом, что она будет пересекаться с существующей
    @Test
    public void postUpdateCrossTaskTest() throws IOException, InterruptedException {
        Task t1 = new Task("t1", "d t1", TEST_TIME, 15);
        Task t2 = new Task("t2", "d t2");
        Task t2ed = new Task("t2", "d t2", TEST_TIME, 5);
        int t1Id = tm.addTask(t1);
        int t2Id = tm.addTask(t2);

        t2ed.setId(2);
        String taskJson = gson.toJson(t2ed);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + t2Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "При пересекающихся задачах статус ответа должен быть 406");
        Task taskFromManager = tm.getTask(2);
        assertTrue(taskFromManager.getTitle().equals(t2.getTitle()) &&
                taskFromManager.getDescription().equals(t2.getDescription()) &&
                taskFromManager.getStartTime() == null &&
                taskFromManager.getDuration() == null, "При пересечении задача не должна быть обновлена");
    }

    //Удаление задачи
    @Test
    public void deleteTaskTest() throws IOException, InterruptedException {
        Task t1 = new Task("t1", "d t1");
        int t1Id = tm.addTask(t1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + t1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Не верный код ответа");

        assertEquals(0, tm.getAllTasks().size(), "Задача не удалена");
    }

    //TODO переместить тест куда-нибудь в более подходящее место

}
