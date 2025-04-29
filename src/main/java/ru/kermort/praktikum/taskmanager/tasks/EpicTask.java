package ru.kermort.praktikum.taskmanager.tasks;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private final List<Integer> subTasksIds;

    public EpicTask(String title, String description) {
        super(title, description);
        subTasksIds = new ArrayList<>();
    }

    public EpicTask(EpicTask epicTask) {
        super(epicTask);
        subTasksIds = new ArrayList<>(epicTask.getSubTasksIds());
    }

    public List<Integer> getSubTasksIds() {
        return new ArrayList<>(subTasksIds);
    }

    public boolean hasSubTasks() {
        return !subTasksIds.isEmpty();
    }

    public void addSubTaskId(Integer subTaskId) {
        subTasksIds.add(subTaskId);
    }

    public void deleteSubTaskId(Integer subTaskId) {
        subTasksIds.remove(subTaskId);
    }

    public void clearSubTasks() {
        subTasksIds.clear();
    }

    @Override
    public String toString() {
        String s = super.toString();
        return "Epic" + s + ". Количество подзадач: " + subTasksIds.size() + " id подзадач: " + subTasksIds + '}';
    }
}
