package ru.kermort.praktikum.taskmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import ru.kermort.praktikum.taskmanager.manager.FileBackedTaskManager;
import ru.kermort.praktikum.taskmanager.manager.InMemoryTaskManager;
import ru.kermort.praktikum.taskmanager.manager.TaskManager;
import ru.kermort.praktikum.taskmanager.server.handlers.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager tm;
    private static HttpServer server;
    private static Gson gson;

    public HttpTaskServer(TaskManager tm) {
        this.tm = tm;
        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public static void main(String[] args) {
        HttpTaskServer hts = new HttpTaskServer(new InMemoryTaskManager());
        hts.start();
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/tasks", new TaskHandler(tm));
            server.createContext("/epics", new EpicHandler(tm));
            server.createContext("/subtasks", new SubTaskHandler(tm));
            server.createContext("/prioritized", new PrioritizedHandler(tm));
            server.createContext("/history", new HistoryHandler(tm));
            server.start();
            System.out.println("Сервер запущен на порту: " + PORT);
        } catch (IOException e) {
            System.out.println("Произошла ошибка при попытке запуска сервера");
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
            System.out.println("Сервер остановлен");
        }
    }

    public static Gson getGson() {
        return gson;
    }
}



