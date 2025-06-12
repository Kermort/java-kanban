package ru.kermort.praktikum.taskmanager.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.kermort.praktikum.taskmanager.HttpTaskServer;
import ru.kermort.praktikum.taskmanager.exceptions.CrossTaskException;
import ru.kermort.praktikum.taskmanager.exceptions.NotFoundException;
import ru.kermort.praktikum.taskmanager.manager.TaskManager;
import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler {
    private final Gson gson = HttpTaskServer.getGson();

    public EpicHandler(TaskManager tm) {
        super(tm);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathArray = exchange.getRequestURI().toString().split("/");
        if (!pathArray[1].equals("epics")) {
            sendBadRequest(exchange);
            return;
        }

        try {
            switch (exchange.getRequestMethod()) {
                case "GET" -> handleGetMethod(exchange, pathArray);
                case "POST" -> handlePostMethod(exchange, pathArray);
                case "DELETE" -> handleDeleteMethod(exchange, pathArray);
                default -> sendNotFound(exchange, "Метод не поддерживается");
            }
        } catch (NumberFormatException | JsonSyntaxException e) {
            sendBadRequest(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (CrossTaskException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    private void handleGetMethod(HttpExchange exchange, String[] pathArray) throws IOException,
            NumberFormatException, NotFoundException {

        if (pathArray.length == 2) {
            String jsonStr = gson.toJson(tm.getAllEpicTasks());
            sendText(exchange, jsonStr, 200);
            return;
        }

        if (pathArray.length == 3) {
            int id = Integer.parseInt(pathArray[2]);
            String jsonStr = gson.toJson(tm.getEpicTask(id));
            sendText(exchange, jsonStr, 200);
            return;
        }

        if (pathArray.length == 4 && pathArray[3].equals("subtasks")) {
            int id = Integer.parseInt(pathArray[2]);
            String jsonStr = gson.toJson(tm.getEpicSubTasks(id));
            sendText(exchange, jsonStr, 200);
            return;
        }
        sendBadRequest(exchange);
    }

    private void handlePostMethod(HttpExchange exchange, String[] pathArray) throws IOException,
            CrossTaskException, JsonSyntaxException {

        if (pathArray.length == 2) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            int id;

            EpicTask epicTask = gson.fromJson(body, EpicTask.class);
            if (!isValidEpicTask(epicTask)) {
                sendBadRequest(exchange);
                return;
            }
            id = tm.addEpicTask(epicTask);
            sendText(exchange, "Эпик добавлен с id " + id, 201);
            return;
        }

        if (pathArray.length == 3) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            EpicTask epicTask = gson.fromJson(body, EpicTask.class);
            int idFromPath = Integer.parseInt(pathArray[2]);
            if (epicTask.getId() != idFromPath || !isValidEpicTask(epicTask)) {
                sendBadRequest(exchange);
                return;
            }
            tm.updateEpicTask(epicTask);
            sendText(exchange, "Эпик обновлен", 201);
            return;
        }
        sendBadRequest(exchange);
    }

    private void handleDeleteMethod(HttpExchange exchange, String[] pathArray) throws IOException,
            NumberFormatException, NotFoundException {

        if (pathArray.length == 3) {
            int id = Integer.parseInt(pathArray[2]);
            tm.deleteEpicTask(id);
            sendText(exchange, "", 200);
            return;
        }
        sendBadRequest(exchange);
    }

    private boolean isValidEpicTask(Task task) {
        return task.getTitle() != null && task.getDescription() != null;
    }
}