package ru.kermort.praktikum.taskmanager.tasks;

public class SubTask extends Task {
    private final EpicTask epicTask;

    public SubTask(String title, String description, EpicTask epicTask) {
        super(title, description);
        this.epicTask = epicTask;
        epicTask.addSubTask(this);
    }

    public SubTask(SubTask subTask, EpicTask epicTask) {
        super(subTask);
        this.epicTask = epicTask;
    }

    public EpicTask getParentTask() {
        return epicTask;
    }

    @Override
    public String toString() {
        return "Sub" +
                (super.toString()).replace("}", ", ") +
                "принадлежит эпику с id=" + epicTask.getId()
                +"}";
    }
}
