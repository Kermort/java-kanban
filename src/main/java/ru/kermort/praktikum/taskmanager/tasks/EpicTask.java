package ru.kermort.praktikum.taskmanager.tasks;

import ru.kermort.praktikum.taskmanager.enums.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private final List<Integer> subTasksIds;
    private LocalDateTime endTime;

    public EpicTask(String title, String description) {
        super(title, description);
        subTasksIds = new ArrayList<>();
        taskType = TaskType.EPIC;
    }

    public EpicTask(EpicTask epicTask) {
        super(epicTask);
        subTasksIds = new ArrayList<>(epicTask.getSubTasksIds());
        endTime = epicTask.endTime;
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
