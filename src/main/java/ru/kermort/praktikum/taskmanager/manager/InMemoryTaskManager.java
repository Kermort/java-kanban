package ru.kermort.praktikum.taskmanager.manager;

import ru.kermort.praktikum.taskmanager.history.HistoryManager;
import ru.kermort.praktikum.taskmanager.tasks.*;
import ru.kermort.praktikum.taskmanager.enums.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpicTasks() {
        epicTasks.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (EpicTask et: epicTasks.values()) {
            et.clearSubTasks();
            calculateEpicTaskStatus(et);
        }
        subTasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicTask(int id) {
        if (!epicTasks.containsKey(id)) {
            return;
        }

        List<SubTask> childTasks = epicTasks.get(id).getSubTasks();
        for (SubTask subTask: childTasks) {
            subTasks.remove(subTask.getId());
        }
        epicTasks.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        if (!subTasks.containsKey(id)) {
            return;
        }

        EpicTask parentTask = subTasks.get(id).getParentTask();
        parentTask.deleteSubTask(subTasks.get(id));
        subTasks.remove(id);
        calculateEpicTaskStatus(parentTask);
    }

    @Override
    public Task getTask(int id) {
        if (!tasks.containsKey(id)) {
            return null;
        }
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public EpicTask getEpicTask(int id) {
        if (!epicTasks.containsKey(id)) {
            return null;
        }
        historyManager.add(epicTasks.get(id));
        return epicTasks.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        if (!subTasks.containsKey(id)) {
            return null;
        }
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public int addTask(Task newTask) {
        newTask.setId(getNextId());
        tasks.put(newTask.getId(), newTask);
        return newTask.getId();
    }

    @Override
    public int addSubTask(SubTask newSubTask) {
        newSubTask.setId(getNextId());
        subTasks.put(newSubTask.getId(), newSubTask);
        calculateEpicTaskStatus(newSubTask.getParentTask());
        return newSubTask.getId();
    }

    @Override
    public int addEpicTask(EpicTask newEpicTask) {
        newEpicTask.setId(getNextId());
        epicTasks.put(newEpicTask.getId(), newEpicTask);
        return newEpicTask.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            return;
        }
        EpicTask parentTask = subTask.getParentTask();
        subTasks.put(subTask.getId(), subTask);
        parentTask.updateSubTask(subTask);
        calculateEpicTaskStatus(parentTask);
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        if (!epicTasks.containsKey(epicTask.getId())) {
            return;
        }
        epicTasks.get(epicTask.getId()).setTitle(epicTask.getTitle());
        epicTasks.get(epicTask.getId()).setDescription(epicTask.getDescription());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void calculateEpicTaskStatus(EpicTask epicTask) {
        if (!epicTask.hasSubTasks()) {
            epicTask.setStatus(TaskStatus.NEW);
            return;
        }

        Set<TaskStatus> s = new HashSet<>();
        for (SubTask st: epicTask.getSubTasks()) {
            s.add(st.getStatus());
        }

        if (s.size() == 1 && s.contains(TaskStatus.NEW)) {
            epicTask.setStatus(TaskStatus.NEW);
            return;
        }

        if (s.size() == 1 && s.contains(TaskStatus.DONE)) {
            epicTask.setStatus(TaskStatus.DONE);
            return;
        }

        epicTask.setStatus(TaskStatus.IN_PROGRESS);
    }

    private int getNextId() {
        return nextId++;
    }

}
