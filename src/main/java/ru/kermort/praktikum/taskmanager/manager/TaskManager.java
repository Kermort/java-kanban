package ru.kermort.praktikum.taskmanager.manager;

import ru.kermort.praktikum.taskmanager.tasks.*;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    List<EpicTask> getAllEpicTasks();

    List<SubTask> getAllSubTasks();

    void deleteAllTasks();

    void deleteAllEpicTasks();

    void deleteAllSubTasks();

    void deleteTask(int id);

    void deleteEpicTask(int id);

    void deleteSubTask(int id);

    Task getTask(int id);

    EpicTask getEpicTask(int id);

    SubTask getSubTask(int id);

    int addTask(Task newTask);

    int addSubTask(SubTask newSubTask);

    int addEpicTask(EpicTask newEpicTask);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpicTask(EpicTask epicTask);

    List<Task> getHistory();


}
