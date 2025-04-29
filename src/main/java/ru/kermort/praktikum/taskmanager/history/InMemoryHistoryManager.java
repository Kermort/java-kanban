package ru.kermort.praktikum.taskmanager.history;

import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyList = new HashMap<>();
    private Node first = null;
    private Node last = null;

    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (task instanceof EpicTask) {
            EpicTask epicTaskCopy = new EpicTask((EpicTask) task);
            if (historyList.containsKey(epicTaskCopy.getId())) {
                remove(epicTaskCopy.getId());
            }
            linkLast(epicTaskCopy);
        } else if (task instanceof SubTask) {
            SubTask subTaskCopy = new SubTask((SubTask) task);
            if (historyList.containsKey(subTaskCopy.getId())) {
                remove(subTaskCopy.getId());
            }
            linkLast(subTaskCopy);
        } else {
            Task taskCopy = new Task(task);
            if (historyList.containsKey(taskCopy.getId())) {
                remove(taskCopy.getId());
            }
            linkLast(taskCopy);
        }
    }

    public List<Task> getHistory() {
        return getTasks();
    }

    public void remove(int id) {
        if (historyList.containsKey(id)) {
            removeNode(historyList.get(id));
            historyList.remove(id);
        }
    }

    private void removeNode(Node node) {
        final Node leftNode = node.prev;
        final Node rightNode = node.next;
        if (leftNode != null) {
            leftNode.next = rightNode;
        } else {
            first = node.next;
        }
        if (rightNode != null) {
            rightNode.prev = leftNode;
        } else {
            last = node.prev;
        }
    }

    private void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        historyList.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> result = new ArrayList<>();
        Node n = first;
        while (n != null) {
            result.add(n.data);
            n = n.next;
        }
        return result;
    }

    static class Node {
        private final Task data;
        private Node next;
        private Node prev;

        private Node(Node prev, Task data, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }
}

