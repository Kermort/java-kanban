package ru.kermort.praktikum.taskmanager.manager;

import ru.kermort.praktikum.taskmanager.tasks.*;
import ru.kermort.praktikum.taskmanager.enums.TaskStatus;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class TaskManager {
    private int nextId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpicTasks() {
        epicTasks.clear();
        subTasks.clear();
    }

    public void deleteAllSubTasks() {
        for (EpicTask et: epicTasks.values()) {
            et.clearSubTasks();
            calculateEpicTaskStatus(et);
        }
        subTasks.clear();
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

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

    public void deleteSubTask(int id) {
        if (!subTasks.containsKey(id)) {
            return;
        }

        EpicTask parentTask = subTasks.get(id).getParentTask();
        parentTask.deleteSubTask(subTasks.get(id));
        subTasks.remove(id);
        calculateEpicTaskStatus(parentTask);
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public EpicTask getEpicTask(int id) {
        return epicTasks.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    private int getNextId() {
        return nextId++;
    }

    public int addTask(Task newTask) {
        newTask.setId(getNextId());
        tasks.put(newTask.getId(), newTask);
        return newTask.getId();
    }

    public int addSubTask(SubTask newSubTask) {
        newSubTask.setId(getNextId());
        subTasks.put(newSubTask.getId(), newSubTask);
        calculateEpicTaskStatus(newSubTask.getParentTask());
        return newSubTask.getId();
    }

    public int addEpicTask(EpicTask newEpicTask) {
        newEpicTask.setId(getNextId());
        epicTasks.put(newEpicTask.getId(), newEpicTask);
        return newEpicTask.getId();
    }

    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            return;
        }
        EpicTask parentTask = subTask.getParentTask();
        subTasks.put(subTask.getId(), subTask);
        parentTask.updateSubTask(subTask);
        calculateEpicTaskStatus(parentTask);
    }

    public void updateEpicTask(EpicTask epicTask) {
        if (!epicTasks.containsKey(epicTask.getId())) {
            return;
        }
        epicTasks.get(epicTask.getId()).setTitle(epicTask.getTitle());
        epicTasks.get(epicTask.getId()).setDescription(epicTask.getDescription());
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

}
