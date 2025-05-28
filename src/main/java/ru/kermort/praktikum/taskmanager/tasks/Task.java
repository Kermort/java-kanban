package ru.kermort.praktikum.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import ru.kermort.praktikum.taskmanager.enums.TaskStatus;
import ru.kermort.praktikum.taskmanager.enums.TaskType;

public class Task implements Comparable<Task> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    protected String title;
    protected String description;
    protected int id;
    protected TaskStatus status;
    protected TaskType taskType;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        status = TaskStatus.NEW;
        taskType = TaskType.TASK;
        id = 0;
    }

    public Task(String title, String description, LocalDateTime startTime, long minutes) {
        this(title, description);
        this.startTime = startTime;
        duration = Duration.ofMinutes(minutes);
    }

    public Task(Task task) {
        title = task.getTitle();
        description = task.getDescription();
        id = task.getId();
        status = task.getStatus();
        taskType = task.getTaskType();
        startTime = task.startTime;
        duration = task.duration;
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
        String startTime;
        if (this.startTime == null) {
            startTime = "null";
        } else {
            startTime = this.startTime.format(DATE_FORMATTER);
        }
        String duration;
        if (this.duration == null) {
            duration = "null";
        } else {
            duration = String.valueOf(this.duration);
        }
        return "Task{" +
                "title='" + title + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", description='" + description + "'" +
                ", startTime=" + startTime +
                ", duration=" + duration +
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(long minutes) {
        duration = Duration.ofMinutes(minutes);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    public boolean hasTimeAndDuration() {
        return startTime != null && duration != null;
    }

    @Override
    public int compareTo(Task otherTask) {
        return this.startTime.compareTo(otherTask.startTime);
    }
}
