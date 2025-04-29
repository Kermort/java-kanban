package ru.kermort.praktikum.taskmanager.tasks;

public class SubTask extends Task {
    private final int epicTaskId;

    //конструктор для создания нового объекта
    public SubTask(String title, String description, int epicTaskId) {
        super(title, description);
        this.epicTaskId = epicTaskId;
    }

    //конструктор для создания копии объекта
    public SubTask(SubTask subTask) {
        super(subTask);
        epicTaskId = subTask.getParentTaskId();
    }

    public int getParentTaskId() {
        return epicTaskId;
    }

    @Override
    public String toString() {
        return "Sub" +
                (super.toString()).replace("}", ", ") +
                "принадлежит эпику с id=" + epicTaskId
                +"}";
    }
}
