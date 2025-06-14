package ru.kermort.praktikum.taskmanager.manager;

import org.junit.jupiter.api.Test;
import ru.kermort.praktikum.taskmanager.enums.TaskStatus;
import ru.kermort.praktikum.taskmanager.exceptions.CrossTaskException;
import ru.kermort.praktikum.taskmanager.exceptions.NotFoundException;
import ru.kermort.praktikum.taskmanager.tasks.EpicTask;
import ru.kermort.praktikum.taskmanager.tasks.SubTask;
import ru.kermort.praktikum.taskmanager.tasks.Task;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T tm;
    protected final LocalDateTime TEST_TIME = LocalDateTime.of(2025, 6, 1, 10, 0);

    //a. Все подзадачи со статусом NEW.
    @Test
    public void calculateEpicStatusTest1() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st1 = new SubTask("st1", "d st1", etId);
        SubTask st2 = new SubTask("st2", "d st2", etId);
        SubTask st3 = new SubTask("st3", "d st3", etId);
        tm.addSubTask(st1);
        tm.addSubTask(st2);
        tm.addSubTask(st3);

        assertSame(et.getStatus(), TaskStatus.NEW, "статус эпика, в котором все подзадачи имеют статус NEW, рассчитан неверно");
    }

    //b. Все подзадачи со статусом DONE.
    @Test
    public void calculateEpicStatusTest2() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st1 = new SubTask("st1", "d st1", etId);
        SubTask st2 = new SubTask("st2", "d st2", etId);
        SubTask st3 = new SubTask("st3", "d st3", etId);
        tm.addSubTask(st1);
        tm.addSubTask(st2);
        tm.addSubTask(st3);
        st1.setStatus(TaskStatus.DONE);
        st2.setStatus(TaskStatus.DONE);
        st3.setStatus(TaskStatus.DONE);
        tm.updateSubTask(st1);
        tm.updateSubTask(st2);
        tm.updateSubTask(st3);

        assertSame(et.getStatus(), TaskStatus.DONE, "статус эпика, в котором все подзадачи имеют статус DONE, рассчитан неверно");
    }

    // c. Подзадачи со статусами NEW и DONE.
    @Test
    public void calculateEpicStatusTest3() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st1 = new SubTask("st1", "d st1", etId);
        SubTask st2 = new SubTask("st2", "d st2", etId);
        SubTask st3 = new SubTask("st3", "d st3", etId);
        tm.addSubTask(st1);
        tm.addSubTask(st2);
        tm.addSubTask(st3);
        st1.setStatus(TaskStatus.DONE);
        st2.setStatus(TaskStatus.DONE);
        tm.updateSubTask(st1);
        tm.updateSubTask(st2);

        assertSame(et.getStatus(), TaskStatus.IN_PROGRESS, "статус эпика, в котором подзадачи имеют статус NEW и DONE, рассчитан неверно");
    }

    //d. Подзадачи со статусом IN_PROGRESS.
    @Test
    public void calculateEpicStatusTest4() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st1 = new SubTask("st1", "d st1", etId);
        SubTask st2 = new SubTask("st2", "d st2", etId);
        SubTask st3 = new SubTask("st3", "d st3", etId);
        tm.addSubTask(st1);
        tm.addSubTask(st2);
        tm.addSubTask(st3);
        st1.setStatus(TaskStatus.IN_PROGRESS);
        st2.setStatus(TaskStatus.IN_PROGRESS);
        st3.setStatus(TaskStatus.IN_PROGRESS);
        tm.updateSubTask(st1);
        tm.updateSubTask(st2);
        tm.updateSubTask(st3);

        assertSame(et.getStatus(), TaskStatus.IN_PROGRESS, "статус эпика, в котором все подзадачи имеют статус IN_PROGRESS, рассчитан неверно");
    }

    @Test
    public void getAllTaskTest() {
        Task t1 = new Task("t1", "d t1");
        Task t2 = new Task("t2", "d t2");
        Task t3 = new Task("t3", "d t3");
        tm.addTask(t1);
        tm.addTask(t2);
        tm.addTask(t3);
        List<Task> allTasks = tm.getAllTasks();

        assertEquals(3, allTasks.size(), "метод getAllTasks вернул неверное количество задач");
    }

    @Test
    public void getAllEpicTasksTest() {
        EpicTask et1 = new EpicTask("et1", "d et1");
        EpicTask et2 = new EpicTask("et2", "d et2");
        EpicTask et3 = new EpicTask("et3", "d et3");
        tm.addEpicTask(et1);
        tm.addEpicTask(et2);
        tm.addEpicTask(et3);
        List<EpicTask> allEpicTasks = tm.getAllEpicTasks();

        assertEquals(3, allEpicTasks.size(), "метод getAllEpicTAsks вернул неверное количество эпиков");
    }

    @Test
    public void getAllSubTasksTest() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st1 = new SubTask("st1", "d st1", etId);
        SubTask st2 = new SubTask("st2", "d st2", etId);
        SubTask st3 = new SubTask("st3", "d st3", etId);
        tm.addSubTask(st1);
        tm.addSubTask(st2);
        tm.addSubTask(st3);
        List<SubTask> allSubTasks = tm.getAllSubTasks();

        assertEquals(3, allSubTasks.size(), "метод getAllSubTasks вернул неверное количество подзадач");
    }

    @Test
    public void deleteAllTasksTest() {
        Task t1 = new Task("t1", "d t1");
        Task t2 = new Task("t2", "d t2");
        Task t3 = new Task("t3", "d t3");
        tm.addTask(t1);
        tm.addTask(t2);
        tm.addTask(t3);
        tm.deleteAllTasks();
        List<Task> allTasks = tm.getAllTasks();

        assertEquals(0, allTasks.size(), "метод deleteAllTasks удалил не все задачи");
    }

    @Test
    public void deleteAllEpicTAsksTest() {
        EpicTask et1 = new EpicTask("et1", "d et1");
        EpicTask et2 = new EpicTask("et2", "d et2");
        EpicTask et3 = new EpicTask("et3", "d et3");
        int et1Id = tm.addEpicTask(et1);
        int et2Id = tm.addEpicTask(et2);
        int et3Id = tm.addEpicTask(et3);
        SubTask st1 = new SubTask("st1", "d st1", et1Id);
        SubTask st2 = new SubTask("st2", "d st2", et2Id);
        SubTask st3 = new SubTask("st3", "d st3", et2Id);
        tm.deleteAllEpicTasks();
        List<EpicTask> allEpicTasks = tm.getAllEpicTasks();
        List<SubTask> allSubTasks = tm.getAllSubTasks();

        assertEquals(0, allEpicTasks.size(), "метод deleteAllEpicTasks удалил не все эпики");
        assertEquals(0, allSubTasks.size(), "метод deleteAllEpicTasks удалил не все подзадачи");
    }

    @Test
    public void deleteAllSubTasksTest() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st1 = new SubTask("st1", "d st1", etId);
        SubTask st2 = new SubTask("st2", "d st2", etId);
        SubTask st3 = new SubTask("st3", "d st3", etId);
        tm.addSubTask(st1);
        tm.addSubTask(st2);
        tm.addSubTask(st3);
        tm.deleteAllSubTasks();
        List<SubTask> allSubTasks = tm.getAllSubTasks();
        List<Integer> subTasksIdsInEpic = tm.getEpicTask(etId).getSubTasksIds();

        assertEquals(0, allSubTasks.size(), "метод deleteAllSubTasks удалил не все подзадачи");
        assertEquals(0, subTasksIdsInEpic.size(), "метод deleteAllSubTasks не удалил id подзадач в эпике");
    }

    @Test
    public void deleteTaskTest() {
        Task t1 = new Task("t1", "d t1");
        int t1Id = tm.addTask(t1);
        tm.deleteTask(t1Id);

        assertThrows(NotFoundException.class, () -> tm.getTask(t1Id), "метод deleteTask не удалил задачу");
    }

    @Test
    public void deleteEpicTaskTest() {
        EpicTask et1 = new EpicTask("et1", "d et1");
        int et1Id = tm.addEpicTask(et1);
        SubTask st1 = new SubTask("st1", "d st1", et1Id);
        SubTask st2 = new SubTask("st2", "d st2", et1Id);
        tm.deleteEpicTask(et1Id);
        List<SubTask> allSubTasks = tm.getAllSubTasks();

        assertThrows(NotFoundException.class, () -> tm.getEpicTask(et1Id), "метод deleteEpicTask не удалил эпик");
        assertEquals(0, allSubTasks.size(), "метод deleteEpicTask не удалил связанную с эпиком подзадачу");
    }

    @Test
    public void deleteSubTaskTest() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st1 = new SubTask("st1", "d st1", etId);
        int st1Id = tm.addSubTask(st1);
        tm.deleteSubTask(st1Id);
        List<Integer> subTasksIdsInEpic = tm.getEpicTask(etId).getSubTasksIds();

        assertThrows(NotFoundException.class, () -> tm.getSubTask(st1Id), "метод deleteSubTask не удалил подзадачу");
        assertEquals(0, subTasksIdsInEpic.size(), "метод deleteSubTask не удалил id подзадачи в эпике");
    }

    @Test
    public void getTaskTest() {
        Task t = new Task("t", "d t");
        int tId = tm.addTask(t);
        Task taskFromManager = tm.getTask(tId);

        assertNotNull(taskFromManager, "метод getTask не вернул задачу");
        assertSame(t, taskFromManager, "метод getTask вернул неверную задачу");
    }

    @Test
    public void getEpicTaskTest() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        EpicTask epicTaskFromManager = tm.getEpicTask(etId);

        assertNotNull(epicTaskFromManager, "метод getEpicTask не вернул эпик");
        assertSame(et, epicTaskFromManager, "метод getEpicTask вернул неверный эпик");
    }

    @Test
    public void getSubTaskTest() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st = new SubTask("st", "d st", etId);
        int stId = tm.addSubTask(st);
        SubTask subTaskFromManager = tm.getSubTask(stId);

        assertNotNull(subTaskFromManager, "метод getSubTask не вупнул подзадачу");
        assertSame(st, subTaskFromManager, "метод getSubTask вернул неверную подзадачу");
    }

    @Test
    void addTaskTest() {
        Task task = new Task("t", "d t");
        int tId = tm.addTask(task);
        Task savedTask = tm.getTask(tId);

        assertNotNull(savedTask, "Задача не найдена по id.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статус новой задачи не NEW");
    }

    @Test
    void addTaskWithStartTimeAndDurationTest() {
        Task task = new Task("t", "d t", TEST_TIME, 10);
        int tId = tm.addTask(task);
        Task savedTask = tm.getTask(tId);

        assertNotNull(savedTask, "Задача не найдена по id.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статус новой задачи не NEW");
        assertEquals(TEST_TIME, savedTask.getStartTime());
        assertEquals(Duration.ofMinutes(10), savedTask.getDuration());
    }

    @Test
    void addEpicTaskTest() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);

        EpicTask savedTask = tm.getEpicTask(etId);

        assertNotNull(savedTask, "Эпик не найден по id.");
        assertEquals(et, savedTask, "Эпикии не совпадают.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статус нового эпика не NEW");
    }

    @Test
    void addSubTaskTest() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st = new SubTask("st", "d st", etId);
        int stId = tm.addSubTask(st);
        SubTask savedTask = tm.getSubTask(stId);

        assertNotNull(savedTask, "Подзадача не найдена по id.");
        assertEquals(st, savedTask, "Подзадачи не совпадают.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статус новой подзадачи не NEW");
    }

    @Test
    void addSubTaskWithTimeAndDurationTest() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st = new SubTask("st", "d st", etId, TEST_TIME, 10);
        int stId = tm.addSubTask(st);
        SubTask savedTask = tm.getSubTask(stId);

        assertNotNull(savedTask, "Подзадача не найдена по id.");
        assertEquals(st, savedTask, "Подзадачи не совпадают.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статус новой подзадачи не NEW");
        assertEquals(TEST_TIME, savedTask.getStartTime());
        assertEquals(Duration.ofMinutes(10), savedTask.getDuration());
    }

    @Test
    public void updateTaskTest() {
        Task t = new Task("t", "d t");
        int tId = tm.addTask(t);
        t.setTitle("t (updated)");
        t.setDescription("d t (updated)");
        t.setDuration(60);
        t.setStartTime(LocalDateTime.of(2025, 6, 1, 10, 0));
        t.setStatus(TaskStatus.IN_PROGRESS);
        tm.updateTask(t);
        Task savedTask = tm.getTask(tId);

        assertTrue(savedTask.getId() == tId
                && savedTask.getStartTime().equals(LocalDateTime.of(2025, 6, 1, 10, 0))
                && savedTask.getDuration().toMinutes() == 60
                && savedTask.getStatus() == TaskStatus.IN_PROGRESS
                && savedTask.getTitle().equals("t (updated)")
                && savedTask.getDescription().equals("d t (updated)"),
                "метод updateTask обновил зачачу неверно");
    }

    @Test
    public void updateSubTaskTest() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st = new SubTask("st", "d st", etId);
        int stId = tm.addSubTask(st);
        st.setTitle("st (updated)");
        st.setDescription("d st (updated)");
        st.setDuration(60);
        st.setStartTime(LocalDateTime.of(2025, 6, 1, 10, 0));
        st.setStatus(TaskStatus.IN_PROGRESS);
        tm.updateSubTask(st);
        SubTask savedSubTask = tm.getSubTask(stId);

        assertTrue(savedSubTask.getId() == stId
                        && savedSubTask.getStartTime().equals(LocalDateTime.of(2025, 6, 1, 10, 0))
                        && savedSubTask.getDuration().toMinutes() == 60
                        && savedSubTask.getStatus() == TaskStatus.IN_PROGRESS
                        && savedSubTask.getTitle().equals("st (updated)")
                        && savedSubTask.getDescription().equals("d st (updated)")
                        && savedSubTask.getParentTaskId() == etId,
                "метод updateSubTask обновил подзачачу неверно");
    }

    @Test
    public void updateEpicTaskTest() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st = new SubTask("st", "d st", etId);
        int stId = tm.addSubTask(st);
        et.setTitle("et (updated)");
        et.setDescription("d et (updated)");
        tm.updateSubTask(st);
        EpicTask savedEpicTask = tm.getEpicTask(etId);

        assertTrue(savedEpicTask.getId() == etId
                        && savedEpicTask.getTitle().equals("et (updated)")
                        && savedEpicTask.getDescription().equals("d et (updated)"),
                "метод updateEpicTask обновил подзачачу неверно");
    }

    @Test
    void taskFieldsShouldNotChangeAfterAddToManagerTest() {
        Task t = new Task("t", "d t");
        Task tInitialState = new Task(t);
        tInitialState.setId(1);
        int tId = tm.addTask(t);
        Task taskFromManager = tm.getTask(tId);

        assertTrue(tInitialState.getId() == taskFromManager.getId()
                        && tInitialState.getTitle().equals(taskFromManager.getTitle())
                        && tInitialState.getDescription().equals(taskFromManager.getDescription()),
                "некоторые поля задачи изменились при добавлении ее в менеджер");
    }

    @Test
    void epicTaskFieldsShouldNotChangeAfterAddToManagerTest() {
        EpicTask et = new EpicTask("et", "d et");
        EpicTask etInitialState = new EpicTask(et);
        etInitialState.setId(1);
        int etId = tm.addEpicTask(et);
        EpicTask epicTaskFromManager = tm.getEpicTask(etId);

        assertTrue(etInitialState.getId() == epicTaskFromManager.getId()
                        && etInitialState.getTitle().equals(epicTaskFromManager.getTitle())
                        && etInitialState.getDescription().equals(epicTaskFromManager.getDescription())
                        && etInitialState.getSubTasksIds().equals(epicTaskFromManager.getSubTasksIds()),
                "некоторые поля эпика изменились при добавлении ее в менеджер");
    }

    @Test
    void subTaskFieldsShouldNotChangeAfterAddToManagerTest() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st = new SubTask("st", "d st", etId);
        SubTask stInitialState = new SubTask(st);
        stInitialState.setId(2);
        int stId = tm.addSubTask(st);
        SubTask subTaskFromManager = tm.getSubTask(stId);

        assertTrue(stInitialState.getId() == subTaskFromManager.getId()
                        && stInitialState.getTitle().equals(subTaskFromManager.getTitle())
                        && stInitialState.getDescription().equals(subTaskFromManager.getDescription())
                        && stInitialState.getParentTaskId() == subTaskFromManager.getParentTaskId(),
                "некоторые поля подзадачи изменились при добавлении ее в менеджер");
    }

    @Test
    void nonExistentTasksTest() {
        assertThrows(NotFoundException.class, () -> tm.getTask(999), "менеджер возвращает задачу по id, который не существует");
        assertThrows(NotFoundException.class, () -> tm.getEpicTask(999), "менеджер возвращает эпик по id, который не существует");
        assertThrows(NotFoundException.class, () -> tm.getSubTask(999), "менеджер возвращает подзадачу по id, который не существует");
    }

    @Test
    void subTaskIdShouldBeRemovedFromEpicIfSubTaskRemoved() {
        int ep1Id = tm.addEpicTask(new EpicTask("et", "d et"));
        int st1Id = tm.addSubTask(new SubTask("st", "d st", ep1Id));
        tm.deleteSubTask(st1Id);

        assertFalse(tm.getEpicTask(ep1Id).getSubTasksIds().contains(st1Id),
                "в эпике остался id подзадачи после ее удаления");
    }

    @Test
    void changeStatusTest() {
        EpicTask ep1 = new EpicTask("et1", "d et1");
        int ep1Id = tm.addEpicTask(ep1);
        SubTask st11 = new SubTask("st11", "s st11", ep1Id);
        int st11Id = tm.addSubTask(st11);

        EpicTask ep2 = new EpicTask("et2", "d et2");
        int ep2Id = tm.addEpicTask(ep2);
        SubTask st21 = new SubTask("st21", "d st21", ep2Id);
        SubTask st22 = new SubTask("st22", "d st22", ep2.getId());
        int st21Id = tm.addSubTask(st21);
        int st22Id = tm.addSubTask(st22);

        st11.setStatus(TaskStatus.DONE);
        st21.setStatus(TaskStatus.DONE);
        tm.updateSubTask(st11);
        tm.updateSubTask(st21);

        assertEquals(TaskStatus.DONE, ep1.getStatus(), "Статус et1 не изменился на DONE");
        assertEquals(TaskStatus.IN_PROGRESS, ep2.getStatus(), "Статус et2 не изменился на IN_PROGRESS");

        tm.deleteEpicTask(ep2.getId());
        tm.deleteAllSubTasks();

        assertEquals(TaskStatus.NEW, ep1.getStatus(), "Статус et1 не изменился на NEW");
    }

    @Test
    public void forSubTaskParentEpicShouldBeInTaskManagerTest() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st = new SubTask("st", "d st", etId);
        int stId = tm.addSubTask(st);
        SubTask subTaskFromManager = tm.getSubTask(stId);
        EpicTask epicTaskFromManager = tm.getEpicTask(subTaskFromManager.getParentTaskId());

        assertNotNull(epicTaskFromManager, "в менеджере отсутствует связанный с подзадачей эпик");
    }

    @Test
    public void checkCrossTest() {
        EpicTask et = new EpicTask("et", "d et");
        Task baseTask = new Task("t", "d t", TEST_TIME, 60);
        Task tryTask1 = new Task("t1", "d t1", TEST_TIME.plusMinutes(30), 60);
        tm.addTask(baseTask);

        assertThrows(CrossTaskException.class, () -> tm.addTask(tryTask1),
                "вторая задача начинается до того, как закончена первая. должно быть выброшено исключение");

        Task tryTask2 = new Task("t2", "d t2", TEST_TIME.minusMinutes(50), 60);
        assertThrows(CrossTaskException.class, () -> tm.addTask(tryTask2),
                "вторая задача заканчивается ровно в то же время, когда начинается первая. должно быть выброшено исключение");

        Task tryTask3 = new Task("t3", "d t3", TEST_TIME, 30);
        assertThrows(CrossTaskException.class, () -> tm.addTask(tryTask3),
                "вторая задача начинается ровно в то же время, когда начинается первая. должно быть выброшено исключение");
    }

    @Test
    public void prioritizedTasksTest() {
        Task t1 = new Task("t1", "d t1", TEST_TIME, 10);
        Task t2 = new Task("t2", "d t2", TEST_TIME.plusHours(1), 10);
        Task t3 = new Task("t3", "d t3");
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st1 = new SubTask("st1", "d st1", etId, TEST_TIME.plusHours(2), 10);
        SubTask st2 = new SubTask("st2", "d st2", etId, TEST_TIME.plusHours(3), 10);
        SubTask st3 = new SubTask("st3", "d st3", etId);
        int st1Id = tm.addSubTask(st1);
        int st2Id = tm.addSubTask(st2);
        int st3Id = tm.addSubTask(st3);
        int t1Id = tm.addTask(t1);
        int t2Id = tm.addTask(t2);
        int t3Id = tm.addTask(t3);
        List<Task> prioritizedTasks = tm.getPrioritizedTasks();

        assertEquals(4, prioritizedTasks.size(),
                "в список отсортированных задач добавлена задача без времени начала");

        assertTrue(prioritizedTasks.get(0).equals(t1) &&
                prioritizedTasks.get(1).equals(t2) &&
                prioritizedTasks.get(2).equals(st1),
                "список отсортированных задач отсортирован неверно");

        tm.deleteTask(t1Id);
        assertEquals(3, tm.getPrioritizedTasks().size(),
                "при удалении задачи, она не удалена из списка отсортированных задач");

        tm.deleteSubTask(st1Id);
        assertEquals(2, tm.getPrioritizedTasks().size(),
                "при удалении подзадачи, она не удалена из списка отсортированных задач");

        st3.setStartTime(TEST_TIME.minusHours(1));
        st3.setDuration(10);
        tm.updateSubTask(st3);
        assertEquals(3, tm.getPrioritizedTasks().size(),
                "при добавлении начального времени в имеющуюся подзадачу, она не добавлена в отсортированный список");

        t3.setStartTime(TEST_TIME.plusHours(5));
        t3.setDuration(10);
        tm.updateTask(t3);
        assertEquals(4, tm.getPrioritizedTasks().size(),
                "при добавлении начального времени в задачу, она не добавлена в отсортированный список");

        tm.deleteAllTasks();
        assertEquals(2, tm.getPrioritizedTasks().size(),
                "при вызове метода deleteAllTasks, задачи не удалены из списка отсортированных задач");

        tm.deleteAllSubTasks();
        assertEquals(0, tm.getPrioritizedTasks().size(),
                "при вызове метода deleteAllSubTasks, подзадачи не удалены из списка отсортированных задач");
    }

    @Test
    public void updateEpicTimeAndDurationTest() {
        EpicTask et = new EpicTask("et", "d et");
        int etId = tm.addEpicTask(et);
        SubTask st1 = new SubTask("st1", "d st1", etId, TEST_TIME, 10);
        SubTask st2 = new SubTask("st2", "d st2", etId, TEST_TIME.plusHours(1), 15);
        SubTask st3 = new SubTask("st3", "d st3", etId, TEST_TIME.plusHours(2), 25);
        int st1Id = tm.addSubTask(st1);
        int st2Id = tm.addSubTask(st2);
        int st3Id = tm.addSubTask(st3);

        assertEquals(TEST_TIME, tm.getEpicTask(etId).getStartTime(),
                "Начальное время эпика установлено неверно");
        assertEquals(TEST_TIME.plusHours(2).plusMinutes(25), tm.getEpicTask(etId).getEndTime(),
                "Конечное время эпика рассчитывается неверно");
        assertEquals(Duration.ofMinutes(50), tm.getEpicTask(etId).getDuration(),
                "Длительность эпика установлена неверно");

        tm.deleteSubTask(st1Id);
        assertEquals(TEST_TIME.plusHours(1), tm.getEpicTask(etId).getStartTime(),
                "После удаления самой ранней подзадачи начальное время эпика рассчитано неверно");
        assertEquals(TEST_TIME.plusHours(2).plusMinutes(25), tm.getEpicTask(etId).getEndTime(),
                "После удаления самой ранней подзадачи конечное время эпика рассчитывается неверно");
        assertEquals(Duration.ofMinutes(40), tm.getEpicTask(etId).getDuration(),
                "После удаления самой ранней подзадачи длительность эпика рассчитана неверно");

        tm.deleteSubTask(st3Id);
        assertEquals(TEST_TIME.plusHours(1), tm.getEpicTask(etId).getStartTime(),
                "После удаления самой поздней подзадачи начальное время эпика рассчитано неверно");
        assertEquals(TEST_TIME.plusHours(1).plusMinutes(15), tm.getEpicTask(etId).getEndTime(),
                "После удаления самой поздней подзадачи конечное время эпика рассчитывается неверно");
        assertEquals(Duration.ofMinutes(15), tm.getEpicTask(etId).getDuration(),
                "После удаления самой поздней подзадачи длительность эпика рассчитана неверно");

        SubTask st4 = new SubTask("st4", "d st4", etId);
        int st4Id = tm.addSubTask(st4);
        assertEquals(TEST_TIME.plusHours(1), tm.getEpicTask(etId).getStartTime(),
                "После добавления подзадачи без начального времени и длительности начальное время эпика рассчитано неверно");
        assertEquals(TEST_TIME.plusHours(1).plusMinutes(15), tm.getEpicTask(etId).getEndTime(),
                "После добавления подзадачи без начального времени и длительности конечное время эпика рассчитывается неверно");
        assertEquals(Duration.ofMinutes(15), tm.getEpicTask(etId).getDuration(),
                "После добавления подзадачи без начального времени и длительности длительность эпика рассчитана неверно");

        tm.deleteSubTask(st4Id);
        assertEquals(TEST_TIME.plusHours(1), tm.getEpicTask(etId).getStartTime(),
                "После удаления подзадачи без начального времени и длительности начальное время эпика рассчитано неверно");
        assertEquals(TEST_TIME.plusHours(1).plusMinutes(15), tm.getEpicTask(etId).getEndTime(),
                "После удаления подзадачи без начального времени и длительности конечное время эпика рассчитывается неверно");
        assertEquals(Duration.ofMinutes(15), tm.getEpicTask(etId).getDuration(),
                "После удаления подзадачи без начального времени и длительности длительность эпика рассчитана неверно");
    }
}
