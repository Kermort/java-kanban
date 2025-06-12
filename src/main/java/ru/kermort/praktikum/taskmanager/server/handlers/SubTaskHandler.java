package ru.kermort.praktikum.taskmanager.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.kermort.praktikum.taskmanager.HttpTaskServer;
import ru.kermort.praktikum.taskmanager.exceptions.CrossTaskException;
import ru.kermort.praktikum.taskmanager.exceptions.NotFoundException;
import ru.kermort.praktikum.taskmanager.manager.TaskManager;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubTaskHandler extends BaseHttpHandler {
    public SubTaskHandler(TaskManager tm) {
        super(tm);
    }
    private final Gson gson = HttpTaskServer.getGson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathArray = exchange.getRequestURI().toString().split("/");
        if (!pathArray[1].equals("subtasks")) {
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

    private void handleGetMethod(HttpExchange exchange, String[] pathArray) throws IOException {
        if (pathArray.length == 2) {
            String jsonStr = gson.toJson(tm.getAllSubTasks());
            sendText(exchange, jsonStr, 200);
            return;
        }

        if (pathArray.length == 3) {
            int id = Integer.parseInt(pathArray[2]);
            String jsonStr = gson.toJson(tm.getSubTask(id));
            sendText(exchange, jsonStr, 200);
            return;
        }
        sendBadRequest(exchange);
    }

    private void handlePostMethod(HttpExchange exchange, String[] pathArray) throws IOException {
        if (pathArray.length == 2) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            int id;

            SubTask subTask = gson.fromJson(body, SubTask.class);
            if (!isValidSubTask(subTask)) {
                sendBadRequest(exchange);
                return;
            }
            id = tm.addSubTask(subTask);
            sendText(exchange, "Подзадача добавлена с id " + id, 201);
            return;
        }

        if (pathArray.length == 3) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            SubTask subTask = gson.fromJson(body, SubTask.class);
            int idFromPath = Integer.parseInt(pathArray[2]);
            if (subTask.getId() != idFromPath || !isValidSubTask(subTask)) {
                sendBadRequest(exchange);
                return;
            }
            tm.updateSubTask(subTask);
            sendText(exchange, "Подзадача обновлена", 201);
            return;
        }
        sendBadRequest(exchange);
    }

    private void handleDeleteMethod(HttpExchange exchange, String[] pathArray) throws IOException {
        if (pathArray.length == 3) {
            int id = Integer.parseInt(pathArray[2]);
            tm.deleteSubTask(id);
            sendText(exchange, "", 200);
            return;
        }
        sendBadRequest(exchange);
    }

    private boolean isValidSubTask(SubTask subTask) {
        return subTask.getTitle() != null && subTask.getDescription() != null && subTask.getParentTaskId() != 0;
    }
}