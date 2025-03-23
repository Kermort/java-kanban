package ru.kermort.praktikum.taskmanager.tasks;

import java.util.ArrayList;
import java.util.List;

public class EpicTask extends Task {
    private final List<SubTask> subTasks;

    public EpicTask(String title, String description) {
        super(title, description);
        subTasks = new ArrayList<>();
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    public boolean hasSubTasks() {
        return !subTasks.isEmpty();
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public void updateSubTask(SubTask subTask) {
        subTasks.remove(subTask);
        subTasks.add(subTask);
    }

    public void deleteSubTask(SubTask subTask) {
        subTasks.remove(subTask);
    }

    public void clearSubTasks() {
        subTasks.clear();
    }

    @Override
    public String toString() {
        String s = super.toString();
        return "Epic" + s + "\nКоличество подзадач: " + subTasks.size() + "\n" + subTasks + '}';
    }
}
