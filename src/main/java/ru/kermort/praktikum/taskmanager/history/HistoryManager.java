package ru.kermort.praktikum.taskmanager.history;

import ru.kermort.praktikum.taskmanager.tasks.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}