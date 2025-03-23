package ru.kermort.praktikum.taskmanager;

import ru.kermort.praktikum.taskmanager.tasks.*;
import ru.kermort.praktikum.taskmanager.enums.TaskStatus;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class TaskManager {
    private int nextId;
    private final Map<Integer, Task> commonTasks;
    private final Map<Integer, EpicTask> epicTasks;
    private final Map<Integer, SubTask> subTasks;

    public TaskManager() {
        nextId = 1;
        commonTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public List<Task> getAllTasksList() {
        ArrayList<Task> result = new ArrayList<>();
        result.addAll(commonTasks.values());
        result.addAll(epicTasks.values());
        result.addAll(subTasks.values());
        return result;
    }

    public List<Task> getAllCommonTasks() {
        return new ArrayList<>(commonTasks.values());
    }

    public List<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllCommonTasks() {
        commonTasks.clear();
    }

    public void deleteAllEpicTasks() {
        epicTasks.clear();
        subTasks.clear();
    }

    public void deleteAllSubTasks() {
        List<EpicTask> parentTasks = new ArrayList<>();
        for (SubTask st: subTasks.values()) {
            parentTasks.add(st.getParentTask());
        }
        for (EpicTask et: parentTasks) {
            et.clearSubTasks();
            calculateEpicTaskStatus(et);
        }
        subTasks.clear();
    }

    public void deleteAllTasks() {
        commonTasks.clear();
        epicTasks.clear();
        subTasks.clear();
    }

    public void deleteCommonTask(int id) {
        commonTasks.remove(id);
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
        subTasks.remove(id);
        parentTask.deleteSubTask(id);
        calculateEpicTaskStatus(parentTask);
    }

    public Task getCommonTask(int id) {
        return commonTasks.get(id);
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

    public void addCommonTask(Task newTask) {
        newTask.setId(getNextId());
        commonTasks.put(newTask.getId(), newTask);
    }

    public void addSubTask(SubTask newSubTask, EpicTask epicTask) {
        if (!epicTasks.containsKey(epicTask.getId())) {
            return;
        }

        newSubTask.setId(getNextId());
        subTasks.put(newSubTask.getId(), newSubTask);
        epicTask.addSubTask(newSubTask);
        newSubTask.setParentTask(epicTask);
        calculateEpicTaskStatus(epicTask);
    }

    public void addEpicTask(EpicTask newEpicTask) {
        newEpicTask.setId(getNextId());
        epicTasks.put(newEpicTask.getId(), newEpicTask);
    }

    public void updateCommonTask(int id, Task task) {
        if (!commonTasks.containsKey(id)) {
            return;
        }
        commonTasks.put(id, task);
    }

    public void updateSubTask(int id, SubTask subTask) {
        if (!subTasks.containsKey(id)) {
            return;
        }
        subTasks.put(id, subTask);
        calculateEpicTaskStatus(subTask.getParentTask());
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
