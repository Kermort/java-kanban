package ru.kermort.praktikum.taskmanager.history;

import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static final int HISTORY_LIMIT = 10;
    private final List<Task> historyList = new LinkedList<>();

    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (historyList.size() == HISTORY_LIMIT) {
            historyList.remove(0);
            //historyList.removeFirst(); - не компилируется;
        }
        if (task instanceof EpicTask) {
            EpicTask epicTaskCopy = new EpicTask((EpicTask) task);
            historyList.add(epicTaskCopy);
        } else if (task instanceof SubTask) {
            SubTask subTaskCopy = new SubTask((SubTask) task);
            historyList.add(subTaskCopy);
        } else {
            Task taskCopy = new Task(task);
            historyList.add(taskCopy);
        }
    }

    public List<Task> getHistory() {
        return new LinkedList<>(historyList);
    }
}
