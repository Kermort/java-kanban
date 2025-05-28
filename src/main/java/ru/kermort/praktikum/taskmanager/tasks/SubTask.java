package ru.kermort.praktikum.taskmanager.tasks;

import ru.kermort.praktikum.taskmanager.enums.TaskType;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private final int epicTaskId;

    public SubTask(String title, String description, int epicTaskId) {
        super(title, description);
        this.epicTaskId = epicTaskId;
        taskType = TaskType.SUBTASK;
    }

    public SubTask(String title, String description, int epicTaskId, LocalDateTime startTime, long minutes) {
        this(title, description, epicTaskId);
        this.startTime = startTime;
        duration = Duration.ofMinutes(minutes);
    }

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
                + "}";
    }
}
