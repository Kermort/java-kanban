package ru.kermort.praktikum.taskmanager;

import ru.kermort.praktikum.taskmanager.manager.Managers;
import ru.kermort.praktikum.taskmanager.manager.TaskManager;
import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();
        int t1Id = tm.addTask(new Task("Task 1 title", "Task 1 description"));
        int t2Id = tm.addTask(new Task("Task 2 title", "Task 2 description"));
        int ep1Id = tm.addEpicTask(new EpicTask("Epic 1 title", "Epic 1 description"));
        int ep2Id = tm.addEpicTask(new EpicTask("Epic 2 title", "Epic 2 description"));
        int st1Id = tm.addSubTask(new SubTask("SubTask 1 title", "SubTask 1 description", ep1Id));
        int st2Id = tm.addSubTask(new SubTask("SubTask 2 title", "SubTask 2 description", ep1Id));
        int st3Id = tm.addSubTask(new SubTask("SubTask 3 title", "SubTask 3 description", ep1Id));

        tm.getTask(t1Id);
        System.out.println(tm.getHistory());
        tm.getEpicTask(ep2Id);
        System.out.println(tm.getHistory());
        tm.getTask(t2Id);
        System.out.println(tm.getHistory());
        tm.getEpicTask(ep1Id);
        System.out.println(tm.getHistory());
        tm.getSubTask(st2Id);
        System.out.println(tm.getHistory());
        tm.getSubTask(st1Id);
        System.out.println(tm.getHistory());
        tm.getSubTask(st3Id);
        System.out.println(tm.getHistory());
        tm.getTask(t1Id);
        System.out.println(tm.getHistory());
        tm.getTask(t1Id);
        System.out.println(tm.getHistory());
        tm.getTask(t2Id);
        System.out.println(tm.getHistory());
        tm.getSubTask(st1Id);
        System.out.println(tm.getHistory());
        tm.getTask(t1Id);
        System.out.println(tm.getHistory());
        tm.deleteTask(t1Id);
        System.out.println(tm.getHistory());
        tm.deleteEpicTask(ep1Id);
        System.out.println(tm.getHistory());
    }
}
