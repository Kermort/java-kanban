package ru.kermort.praktikum.taskmanager.manager;

import ru.kermort.praktikum.taskmanager.enums.TaskStatus;
import ru.kermort.praktikum.taskmanager.enums.TaskType;
import ru.kermort.praktikum.taskmanager.exceptions.ManagerSaveException;
import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;
    public static final String HEADER = "id,type,name,status,description,startTime,duration,epic\n";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            StringBuilder sb = new StringBuilder();
            if (!getAllTasks().isEmpty() || !getAllEpicTasks().isEmpty() || !getAllSubTasks().isEmpty()) {
                sb.append(HEADER);
            }

            tasks.values().stream()
                    .map(this::toString)
                    .forEach(sb::append);

            epicTasks.values().stream()
                    .map(this::toString)
                    .forEach(sb::append);

            subTasks.values().stream()
                    .map(this::toString)
                    .forEach(sb::append);

            writer.write(sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Save error");
        }
    }

    @Override
    public int addTask(Task newTask) {
        int id = super.addTask(newTask);
        save();
        return id;
    }

    @Override
    public int addEpicTask(EpicTask newEpicTask) {
        int id = super.addEpicTask(newEpicTask);
        save();
        return id;
    }

    @Override
    public int addSubTask(SubTask newSubTask) {
        int id = super.addSubTask(newSubTask);
        save();
        return id;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpicTasks() {
        super.deleteAllEpicTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpicTask(int id) {
        super.deleteEpicTask(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        super.updateEpicTask(epicTask);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fbtm = new FileBackedTaskManager(file);
        String fileData;
        int maxId = 0;
        try {
            fileData = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ManagerSaveException("Попытка загрузить состояние менеджера из несуществующего файла: " + file);
        }
        String[] lines = fileData.split("\n");
        if (lines.length > 1) {
            for (int i = 1; i < lines.length; i++) {
                int id = fbtm.fromString(lines[i]);
                if (id > maxId) {
                    maxId = id;
                }
            }
        }
        fbtm.nextId = maxId + 1;

        return fbtm;
    }

    private int fromString(String value) {
        if (value == null || value.isBlank()) {
            return -1;
        }
        String[] taskData = value.split(",");
        int id = Integer.parseInt(taskData[0]);
        //[0]=id, [1]=TaskType, [2]=title, [3]=TaskStatus, [4]=description, [5]=startTime, [6]=duration, [7]=epicId
        switch (taskData[1]) {
            case "TASK" -> {
                Task newTask = new Task(taskData[2], taskData[4]);
                newTask.setStatus(TaskStatus.valueOf(taskData[3]));
                if (!taskData[6].equals("null")) {
                    newTask.setDuration(Long.parseLong(taskData[6]));
                }
                if (!taskData[5].equals("null")) {
                    newTask.setStartTime(LocalDateTime.parse(taskData[5], DATE_FORMATTER));
                    prioritizedTasks.add(newTask);
                }
                newTask.setId(id);
                tasks.put(newTask.getId(), newTask);
            }
            case "EPIC" -> {
                EpicTask newEpicTask = new EpicTask(taskData[2], taskData[4]);
                newEpicTask.setStatus(TaskStatus.valueOf(taskData[3]));
                newEpicTask.setId(id);
                epicTasks.put(newEpicTask.getId(), newEpicTask);
            }
            case "SUBTASK" -> {
                SubTask newSubTask = new SubTask(taskData[2], taskData[4], Integer.parseInt(taskData[7]));
                newSubTask.setStatus(TaskStatus.valueOf(taskData[3]));
                if (!taskData[6].equals("null")) {
                    newSubTask.setDuration(Long.parseLong(taskData[6]));
                }
                if (!taskData[5].equals("null")) {
                    newSubTask.setStartTime(LocalDateTime.parse(taskData[5], DATE_FORMATTER));
                    if (newSubTask.getDuration() != null) {
                        prioritizedTasks.add(newSubTask);
                    }
                }
                newSubTask.setId(id);
                int parentTaskId = Integer.parseInt(taskData[7]);
                EpicTask parentTask = epicTasks.get(parentTaskId);
                parentTask.addSubTaskId(id);
                subTasks.put(newSubTask.getId(), newSubTask);
                updateEpicTaskStartTime(epicTasks.get(newSubTask.getParentTaskId()), newSubTask);
                updateEpicTaskEndTime(epicTasks.get(newSubTask.getParentTaskId()), newSubTask);
                updateEpicTaskDuration(epicTasks.get(newSubTask.getParentTaskId()), newSubTask, Duration.ZERO);
            }
        }

        return id;
    }

    private String toString(Task task) {
        String parentTaskId = "null";
        if (task.getTaskType() == TaskType.SUBTASK) {
            parentTaskId = String.valueOf(((SubTask) task).getParentTaskId());
        }

        String startTime = "null";
        String duration = "null";
        if (task.getStartTime() != null) {
            startTime = task.getStartTime().format(DATE_FORMATTER);
        }
        if (task.getDuration() != null) {
            duration = String.valueOf(task.getDuration().toMinutes());
        }

        return task.getId() + "," +
                task.getTaskType() + "," +
                task.getTitle() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                startTime + "," +
                duration + "," +
                parentTaskId + "," + "\n";
    }

}
