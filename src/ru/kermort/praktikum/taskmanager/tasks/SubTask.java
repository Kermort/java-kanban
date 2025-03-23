package ru.kermort.praktikum.taskmanager.tasks;

public class SubTask extends Task {
    private EpicTask epicTask;

    public SubTask(String title, String description) {
        super(title, description);
    }

    public void setParentTask(EpicTask epicTask) {
        this.epicTask = epicTask;
    }

    public EpicTask getParentTask() {
        return epicTask;
    }

    @Override
    public String toString() {
        return "Sub" + super.toString();
    }
}
