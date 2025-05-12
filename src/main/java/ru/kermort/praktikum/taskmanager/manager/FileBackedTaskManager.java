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
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            StringBuilder sb = new StringBuilder();
            sb.append("id,type,name,status,description,epic\n");
            for (Map.Entry<Integer, Task> entry: tasks.entrySet()) {
                Task task = entry.getValue();
                sb.append(toString(task));
            }

            for (Map.Entry<Integer, EpicTask> entry: epicTasks.entrySet()) {
                EpicTask epicTask = entry.getValue();
                sb.append(toString(epicTask));
            }

            for (Map.Entry<Integer, SubTask> entry: subTasks.entrySet()) {
                SubTask subTask = entry.getValue();
                sb.append(toString(subTask));
            }

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
            return null;
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

        switch (taskData[1]) {
            case "TASK" -> {
                Task newTask = new Task(taskData[2], taskData[4]);
                newTask.setStatus(TaskStatus.valueOf(taskData[3]));
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
                SubTask newSubTask = new SubTask(taskData[2], taskData[4], Integer.parseInt(taskData[5]));
                newSubTask.setStatus(TaskStatus.valueOf(taskData[3]));
                newSubTask.setId(id);
                int parentTaskId = Integer.parseInt(taskData[5]);
                EpicTask parentTask = epicTasks.get(parentTaskId);
                parentTask.addSubTaskId(id);
                subTasks.put(newSubTask.getId(), newSubTask);
            }
        }

        return id;
    }

    public String toString(Task task) {
        String parentTaskId = "";
        if (task.getTaskType() == TaskType.SUBTASK) {
            parentTaskId = String.valueOf(((SubTask) task).getParentTaskId());
        }

        return task.getId() + "," +
                task.getTaskType() + "," +
                task.getTitle() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                parentTaskId + "\n";
    }

}
