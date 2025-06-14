package ru.kermort.praktikum.taskmanager.server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.kermort.praktikum.taskmanager.enums.TaskType;
import ru.kermort.praktikum.taskmanager.exceptions.CrossTaskException;
import ru.kermort.praktikum.taskmanager.exceptions.NotFoundException;
import ru.kermort.praktikum.taskmanager.manager.TaskManager;
import ru.kermort.praktikum.taskmanager.tasks.Task;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager tm) {
        super(tm);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathArray = exchange.getRequestURI().toString().split("/");
        if (!pathArray[1].equals("tasks")) {
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
            String jsonStr = gson.toJson(tm.getAllTasks());
            sendText(exchange, jsonStr, 200);
            return;
        }

        if (pathArray.length == 3) {
            int id = Integer.parseInt(pathArray[2]);
            String jsonStr = gson.toJson(tm.getTask(id));
            sendText(exchange, jsonStr, 200);
            return;
        }
        sendBadRequest(exchange);
    }

    private void handlePostMethod(HttpExchange exchange, String[] pathArray) throws IOException,
            CrossTaskException, JsonSyntaxException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        if (task == null || !isValidTask(task)) {
            sendBadRequest(exchange);
            return;
        }

        if (pathArray.length == 2) {
            int id = tm.addTask(task);
            sendText(exchange, "Задача добавлена с id " + id, 201);
            return;
        }

        if (pathArray.length == 3) {
            int idFromPath = Integer.parseInt(pathArray[2]);
            if (task.getId() != idFromPath) {
                sendBadRequest(exchange);
                return;
            }
            tm.updateTask(task);
            sendText(exchange, "Задача обновлена", 201);
            return;
        }
        sendBadRequest(exchange);
    }

    private void handleDeleteMethod(HttpExchange exchange, String[] pathArray) throws IOException,
            NumberFormatException, NotFoundException {

        if (pathArray.length == 3) {
            int taskId = Integer.parseInt(pathArray[2]);
            tm.deleteTask(taskId);
            sendText(exchange, "", 200);
            return;
        }
        sendBadRequest(exchange);
    }

    private boolean isValidTask(Task task) {
        return task.getTitle() != null && task.getDescription() != null && task.getTaskType() == TaskType.TASK;
    }
}