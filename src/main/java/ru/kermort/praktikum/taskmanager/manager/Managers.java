package ru.kermort.praktikum.taskmanager.manager;

import ru.kermort.praktikum.taskmanager.history.HistoryManager;
import ru.kermort.praktikum.taskmanager.history.InMemoryHistoryManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
