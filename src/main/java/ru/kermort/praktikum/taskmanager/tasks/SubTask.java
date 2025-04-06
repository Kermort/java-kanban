package ru.kermort.praktikum.taskmanager.tasks;

import java.util.List;

public class SubTask extends Task {
    private final EpicTask epicTask;

    //конструктор для создания нового объекта
    public SubTask(String title, String description, EpicTask epicTask) {
        super(title, description);
        this.epicTask = epicTask;
        epicTask.addSubTask(this);
    }

    //конструктор для создания копии объекта
    public SubTask(SubTask subTask) {
        super(subTask);
        EpicTask copiedEpicTask = new EpicTask(subTask.getParentTask());
        List<SubTask> copiedSubTasks = copiedEpicTask.getSubTasks();
        for (int i = 0; i < copiedSubTasks.size(); i++) {
            if (copiedSubTasks.get(i).getId() == this.getId()) {
                copiedSubTasks.add(i, this);
                break;
            }
        }
        this.epicTask = copiedEpicTask;
    }

    //конструктор для создания копии объекта, вызываемый при создании копии эпика
    public SubTask(SubTask subTask, EpicTask epicTask) {
        super(subTask);
        EpicTask copiedEpicTask = new EpicTask(epicTask);
        List<SubTask> copiedSubTasks = copiedEpicTask.getSubTasks();
        for (int i = 0; i < copiedSubTasks.size(); i++) {
            if (copiedSubTasks.get(i).getId() == this.getId()) {
                copiedSubTasks.add(i, this);
                break;
            }
        }
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
