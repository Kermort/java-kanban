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
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpicHandlerTest {
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

    //Получение всех эпиков
    @Test
    public void getEpicTasksTest() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        EpicTask e2 = new EpicTask("e2", "d e2");
        int e1Id = tm.addEpicTask(e1);
        int e2Id = tm.addEpicTask(e2);
        SubTask st1 = new SubTask("st1", "d st1", e1Id);
        SubTask st2 = new SubTask("st2", "d st2", e2Id, TEST_TIME, 15);
        tm.addSubTask(st1);
        tm.addSubTask(st2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Не верный код ответа");
        EpicTask[] epicTasks = gson.fromJson(response.body(), EpicTask[].class);

        assertTrue(e1.getTitle().equals(epicTasks[0].getTitle()) &&
                e1.getDescription().equals(epicTasks[0].getDescription()) &&
                e1.getSubTasksIds().equals(epicTasks[0].getSubTasksIds()) &&
                e1.getId() == epicTasks[0].getId(), "Эпики не совпадают");

        assertTrue(e2.getTitle().equals(epicTasks[1].getTitle()) &&
                e2.getDescription().equals(epicTasks[1].getDescription()) &&
                e2.getSubTasksIds().equals(epicTasks[1].getSubTasksIds()) &&
                e2.getId() == epicTasks[1].getId() &&
                e2.getStartTime().equals(epicTasks[1].getStartTime()) &&
                e2.getDuration().equals(epicTasks[1].getDuration()), "Эпики не совпадают");
    }

    //Получение эпика без времени и длительности
    @Test
    public void getEpicTaskTest1() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id);
        int st1Id = tm.addSubTask(st1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + e1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        EpicTask epicTask = gson.fromJson(response.body(), EpicTask.class);

        assertEquals(200, response.statusCode(), "Не верный код ответа");

        assertTrue(e1.getTitle().equals(epicTask.getTitle()) &&
                e1.getDescription().equals(epicTask.getDescription()) &&
                e1.getSubTasksIds().equals(epicTask.getSubTasksIds()) &&
                e1.getId() == epicTask.getId(), "Эпики не совпадают");
    }

    //Получение эпика с временем и длительностью
    @Test
    public void getEpicTaskTest2() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id, TEST_TIME, 15);
        int st1Id = tm.addSubTask(st1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + e1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        EpicTask epicTask = gson.fromJson(response.body(), EpicTask.class);

        assertEquals(200, response.statusCode(), "Не верный код ответа");

        assertTrue(e1.getTitle().equals(epicTask.getTitle()) &&
                e1.getDescription().equals(epicTask.getDescription()) &&
                e1.getSubTasksIds().equals(epicTask.getSubTasksIds()) &&
                e1.getStartTime().equals(epicTask.getStartTime()) &&
                e1.getDuration().equals(epicTask.getDuration()) &&
                e1.getId() == epicTask.getId(), "Эпики не совпадают");
    }

    //Попытка получения эпика, отсутствующего в менеджере
    @Test
    public void getEpicTaskNotFoundTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "При попытке получить эпик по несуществующему id должна быть ошибка");
    }

    //Получение подзадач эпика
    @Test
    public void getEpicSubtasksTest() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        SubTask st1 = new SubTask("st1", "d st1", e1Id);
        SubTask st2 = new SubTask("st2", "d st2", e1Id, TEST_TIME, 15);
        tm.addSubTask(st1);
        tm.addSubTask(st2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
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

    //Попытка получения подзадач эпика, отсутствующего в менеджере
    @Test
    public void getEpicSubtaskNotFoundTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/999/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(),
                "При попытке получить эпик по несуществующему id должна быть ошибка");
    }

    //Добавление эпика
    @Test
    public void postAddEpicTaskTest() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        String epicJson = gson.toJson(e1);
        e1.setId(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        EpicTask epicTaskFromManager = tm.getEpicTask(1);
        assertEquals(201, response.statusCode(), "Не верный код ответа");

        assertTrue(e1.getTitle().equals(epicTaskFromManager.getTitle()) &&
                e1.getDescription().equals(epicTaskFromManager.getDescription()) &&
                e1.getId() == epicTaskFromManager.getId(), "Эпики не совпадают");
    }

    //Обновление эпика
    @Test
    public void postUpdateEpicTaskTest() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);
        EpicTask e1upd = new EpicTask("e1upd", "d e1upd");
        e1upd.setId(1);
        String epicJson = gson.toJson(e1upd);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + e1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        EpicTask epicTaskFromManager = tm.getEpicTask(1);
        assertEquals(201, response.statusCode(), "Не верный код ответа");

        assertTrue(e1upd.getTitle().equals(epicTaskFromManager.getTitle()) &&
                e1upd.getDescription().equals(epicTaskFromManager.getDescription()) &&
                e1upd.getId() == epicTaskFromManager.getId(), "Эпики не совпадают");
    }

    //Удаление эпика
    @Test
    public void deleteEpicTaskTest() throws IOException, InterruptedException {
        EpicTask e1 = new EpicTask("e1", "d e1");
        int e1Id = tm.addEpicTask(e1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + e1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Не верный код ответа");

        assertEquals(0, tm.getAllEpicTasks().size(), "Задача не удалена");
    }
}