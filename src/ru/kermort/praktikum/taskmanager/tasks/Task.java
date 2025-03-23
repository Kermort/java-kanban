package ru.kermort.praktikum.taskmanager.tasks;

import java.util.Objects;
import ru.kermort.praktikum.taskmanager.enums.TaskStatus;

public class Task {
    private String title;
    private String description;
    private int id;
    private TaskStatus status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        status = TaskStatus.NEW;
        id = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String descriptionLength;
        if (description != null) {
            descriptionLength = "description.length=" + description.length();
        } else {
            descriptionLength = "description.length=0";
        }
        return "Task{" +
                "title='" + title + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", " + descriptionLength +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
