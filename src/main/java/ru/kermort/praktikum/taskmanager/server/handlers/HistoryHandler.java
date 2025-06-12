package ru.kermort.praktikum.taskmanager.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.kermort.praktikum.taskmanager.HttpTaskServer;
import ru.kermort.praktikum.taskmanager.manager.TaskManager;
import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager tm) {
        super(tm);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathArray = exchange.getRequestURI().toString().split("/");
        if (!pathArray[1].equals("history")) {
            sendBadRequest(exchange);
            return;
        }

        if (exchange.getRequestMethod().equals("GET")) {
            handleGetMethod(exchange, pathArray);
        } else {
            sendNotFound(exchange, "Метод не поддерживается");
        }
    }

    private void handleGetMethod(HttpExchange exchange, String[] pathArray) throws IOException {
        Gson gson = HttpTaskServer.getGson();

        if (pathArray.length == 2) {
            String jsonStr = gson.toJson(tm.getHistory());
            sendText(exchange, jsonStr, 200);
            return;
        }
        sendBadRequest(exchange);
    }
}