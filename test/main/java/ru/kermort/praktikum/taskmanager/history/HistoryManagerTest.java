package ru.kermort.praktikum.taskmanager.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryManagerTest {
    private HistoryManager hm;

    @BeforeEach
    public void init() {
        hm = new InMemoryHistoryManager();
    }

    @Test
    public void addTest() {
        Task t = new Task("t", "d t");
        hm.add(t);

        assertEquals(1, hm.getHistory().size(), "метод add не добавил задачу в историю");
    }

    @Test
    public void removeTest() {
        Task t = new Task("t", "d t");
        hm.add(t);
        hm.remove(t.getId());

        assertEquals(0, hm.getHistory().size(),"метод remove не удалил задачу из истории");
    }

    @Test
    public void getHistoryTest() {
        Task t1 = new Task("t1", "d t1");
        Task t2 = new Task("t2", "d t2");
        Task t3 = new Task("t3", "d t3");
        t1.setId(1);
        t2.setId(2);
        t3.setId(3);
        hm.add(t3);
        hm.add(t2);
        hm.add(t1);
        List<Task> history = hm.getHistory();

        assertTrue(history.get(0).equals(t3) && history.get(1).equals(t2) && history.get(2).equals(t1),
                "метод getHistory вернул список задач в неверном порядке");
    }

    @Test
    public void emptyHistoryTest() {
        assertTrue(hm.getHistory().isEmpty(), "вновь созданный менеджер истории не пустой");
    }

    @Test
    public void duplicatesTest() {
        Task t = new Task("t", " d t");
        hm.add(t);
        hm.add(t);

        assertEquals(1, hm.getHistory().size(), "при добвалении в историю задачи повторно прежняя версия не удалена");
    }

    @Test
    public void removeFirstElementFromHistoryTest() {
        Task t1 = new Task("t1", "d t1");
        Task t2 = new Task("t2", "d t2");
        Task t3 = new Task("t3", "d t3");
        t1.setId(1);
        t2.setId(2);
        t3.setId(3);
        hm.add(t3);
        hm.add(t2);
        hm.add(t1);
        hm.remove(t3.getId());
        List<Task> history = hm.getHistory();

        assertTrue(history.get(0).equals(t2) && history.get(1).equals(t1),
                "при удалении из истории первого элемента, он удален неверно");
    }

    @Test
    public void removeLastElementFromHistoryTest() {
        Task t1 = new Task("t1", "d t1");
        Task t2 = new Task("t2", "d t2");
        Task t3 = new Task("t3", "d t3");
        t1.setId(1);
        t2.setId(2);
        t3.setId(3);
        hm.add(t3);
        hm.add(t2);
        hm.add(t1);
        hm.remove(t1.getId());
        List<Task> history = hm.getHistory();

        assertTrue(history.get(0).equals(t3) && history.get(1).equals(t2),
                "при удалении из истории последнего элемента, он удален неверно");
    }

    @Test
    public void removeCenterElementFromHistoryTest() {
        Task t1 = new Task("t1", "d t1");
        Task t2 = new Task("t2", "d t2");
        Task t3 = new Task("t3", "d t3");
        t1.setId(1);
        t2.setId(2);
        t3.setId(3);
        hm.add(t3);
        hm.add(t2);
        hm.add(t1);
        hm.remove(t2.getId());
        List<Task> history = hm.getHistory();

        assertTrue(history.get(0).equals(t3) && history.get(1).equals(t1),
                "при удалении из истории среднего элемента, он удален неверно");
    }
}
