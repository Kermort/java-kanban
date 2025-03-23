package ru.kermort.praktikum.taskmanager;

import ru.kermort.praktikum.taskmanager.tasks.*;
import ru.kermort.praktikum.taskmanager.enums.TaskStatus;

import java.sql.SQLOutput;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        System.out.println("Создадаем менеджер задач...");
        TaskManager tm = new TaskManager();

        System.out.println("Создадем одну простую задачу...");
        Task ct1 = new Task("Заголовок задачи 1", "Описание задачи 1");
        System.out.println("Добавляем созданную задачу в менеджер..."); 
        tm.addCommonTask(ct1);

        System.out.println("Создаем еще одну простую задачу...");
        Task ct2 = new Task("Заголовок задачи 2", "Описание задачи 2");
        System.out.println("Добавляем вторую созданную задачу в менеджер...");
        tm.addCommonTask(ct2);

        System.out.println("Создаем эпик с одной подзадачей...");
        EpicTask ep1 = new EpicTask("Заголовок эпика 1", "Описание эпика 1");
        tm.addEpicTask(ep1);
        SubTask st11 = new SubTask("Заголовок подзадачи 1 эпика 1", "Описание подзадачи 1 эпика 1");
        tm.addSubTask(st11, ep1);



        System.out.println("Создаем эпик с двумя подзадачами...");
        EpicTask ep2 = new EpicTask("Заголовок эпика 2", "Описание эпика 2");
        tm.addEpicTask(ep2);
        SubTask st21 = new SubTask("Заголовок подзадачи 1 эпика 2", "Описание подзадачи 1 эпика 2");
        SubTask st22 = new SubTask("Заголовок подзадачи 2 эпика 2", "Описание подзадачи 2 эпика 2");
        tm.addSubTask(st21, ep2);
        tm.addSubTask(st22, ep2);


        System.out.println("=".repeat(100));

        System.out.println("Печатаем все задачи");
        for (Task task: tm.getAllTasksList()) {
            System.out.println(task + "\n*****");
        }
        System.out.println("=".repeat(100));

        System.out.println("Выполняем подзадачу 1 эпика 1 и подзадачу 1 эпика 2...");
        st11.setStatus(TaskStatus.DONE);
        st21.setStatus(TaskStatus.DONE);
        tm.updateSubTask(st11.getId(), st11);
        tm.updateSubTask(st21.getId(), st21);

        System.out.println("Печатаем все эпики (эпик 1 должен стать DONE, эпик 2 - IN_PROGRESS)");
        for (Task task: tm.getAllEpicTasks()) {
            System.out.println(task + "\n*****");
        }
        System.out.println("=".repeat(100));

        System.out.println("Удаляем подзадачу 2 из эпика 2");
        tm.deleteSubTask(st22.getId());
        System.out.println("Печатаем эпик 2 (эпик 2 должен стать DONE)");
        System.out.println(ep2);
        System.out.println("=".repeat(100));

        System.out.println("Удаляем обычную задачу 2");
        tm.deleteCommonTask(ct2.getId());
        System.out.println("Печатаем все обычные задачи");
        System.out.println(tm.getAllCommonTasks());
        System.out.println("=".repeat(100));

        System.out.println("Удалаяем эпик 2");
        tm.deleteEpicTask(ep2.getId());
        System.out.println("Печатаем все подзадачи (должна остаться только подзадача 1 эпика 1");
        System.out.println(tm.getAllSubTasks());
        System.out.println("=".repeat(100));

        System.out.println("Удаляем все оставшиеся подзадачи");
        tm.deleteAllSubTasks();
        System.out.println("Печатаем эпики (эпик 1 должен стать NEW)");
        System.out.println(tm.getAllEpicTasks());
        System.out.println("=".repeat(100));

        System.out.println("Удаляем все обычные задачи");
        tm.deleteAllCommonTasks();
        System.out.println("Добавляем подзадачу 3 к эпику 1");
        tm.addSubTask(new SubTask("Заголовок подзадачи 3 эпика 1", "Описание подзадачи 3 эпика 1"), ep1);
        System.out.println("Печатаем все, что осталось");
        for (Task task: tm.getAllTasksList()) {
            System.out.println(task + "\n*****");
        }
        System.out.println("=".repeat(100));

    }
}
