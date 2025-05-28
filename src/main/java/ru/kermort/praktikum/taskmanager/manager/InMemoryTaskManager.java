package ru.kermort.praktikum.taskmanager.manager;

import ru.kermort.praktikum.taskmanager.exceptions.CrossTaskException;
import ru.kermort.praktikum.taskmanager.history.HistoryManager;
import ru.kermort.praktikum.taskmanager.tasks.*;
import ru.kermort.praktikum.taskmanager.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, EpicTask> epicTasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>();

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.forEach((key, value) -> {
                    historyManager.remove(key);
                    if (value.hasTimeAndDuration()) {
                        prioritizedTasks.remove(value);
                    }
                });
        tasks.clear();
    }

    @Override
    public void deleteAllEpicTasks() {
        epicTasks.forEach((key, value) -> historyManager.remove(key));
        subTasks.forEach((key, value) -> {
                    historyManager.remove(key);
                    if (value.hasTimeAndDuration()) {
                        prioritizedTasks.remove(value);
                    }
                });
        epicTasks.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        epicTasks.values().forEach(et -> {
                    et.clearSubTasks();
                    calculateEpicTaskStatus(et);
                    calculateEpicTaskTimeAndDuration(et);
                });
        subTasks.forEach((key, value) -> {
                    historyManager.remove(key);
                    if (value.hasTimeAndDuration()) {
                        prioritizedTasks.remove(value);
                    }
                });
        subTasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.get(id).hasTimeAndDuration()) {
            prioritizedTasks.remove(tasks.get(id));
        }
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicTask(int id) {
        if (!epicTasks.containsKey(id)) {
            return;
        }

        epicTasks.get(id).getSubTasksIds()
                .forEach(stId -> {
                    if (subTasks.get(stId).hasTimeAndDuration()) {
                        prioritizedTasks.remove(subTasks.get(stId));
                    }
                    subTasks.remove(stId);
                    historyManager.remove(stId);
                });
        epicTasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        if (!subTasks.containsKey(id)) {
            return;
        }

        if (subTasks.get(id).hasTimeAndDuration()) {
            prioritizedTasks.remove(subTasks.get(id));
        }
        EpicTask parentTask = epicTasks.get(subTasks.get(id).getParentTaskId());
        parentTask.deleteSubTaskId(id);
        subTasks.remove(id);
        calculateEpicTaskStatus(parentTask);
        calculateEpicTaskTimeAndDuration(parentTask);
        historyManager.remove(id);
    }

    @Override
    public Task getTask(int id) {
        if (!tasks.containsKey(id)) {
            return null;
        }

        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public EpicTask getEpicTask(int id) {
        if (!epicTasks.containsKey(id)) {
            return null;
        }

        historyManager.add(epicTasks.get(id));
        return epicTasks.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        if (!subTasks.containsKey(id)) {
            return null;
        }

        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public int addTask(Task newTask) {
        if (checkCross(newTask)) {
            String message = "Невозможно добавить задачу с временем начала "
                    + newTask.getStartTime().toString()
                    + " и длительностью "
                    + newTask.getDuration().toMinutes()
                    + " минут, так как она пересекается с одной из имеющихся задач (подзадач)";
            throw new CrossTaskException(message);
        }
        newTask.setId(getNextId());
        tasks.put(newTask.getId(), newTask);
        if (newTask.hasTimeAndDuration()) {
            prioritizedTasks.add(newTask);
        }
        return newTask.getId();
    }

    @Override
    public int addSubTask(SubTask newSubTask) {
        if (!epicTasks.containsKey(newSubTask.getParentTaskId())) {
            return -1;
        }
        if (checkCross(newSubTask)) {
            String message = "Невозможно добавить подзадачу с временем начала "
                    + newSubTask.getStartTime().toString()
                    + " и длительностью "
                    + newSubTask.getDuration().toMinutes()
                    + " минут, так как она пересекается с одной из имеющихся задач (подзадач)";
            throw new CrossTaskException(message);
        }

        int id = getNextId();
        newSubTask.setId(id);
        subTasks.put(id, newSubTask);
        int parentTaskId = newSubTask.getParentTaskId();
        epicTasks.get(parentTaskId).addSubTaskId(id);
        calculateEpicTaskStatus(epicTasks.get(newSubTask.getParentTaskId()));
        updateEpicTaskStartTime(epicTasks.get(newSubTask.getParentTaskId()), newSubTask);
        updateEpicTaskEndTime(epicTasks.get(newSubTask.getParentTaskId()), newSubTask);
        updateEpicTaskDuration(epicTasks.get(newSubTask.getParentTaskId()), newSubTask, Duration.ZERO);
        if (newSubTask.hasTimeAndDuration()) {
            prioritizedTasks.add(newSubTask);
        }
        return newSubTask.getId();
    }

    @Override
    public int addEpicTask(EpicTask newEpicTask) {
        newEpicTask.setId(getNextId());
        epicTasks.put(newEpicTask.getId(), newEpicTask);
        return newEpicTask.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }
        if (checkCross(task)) {
            String message = "Невозможно установить время начала задачи  "
                    + task.getStartTime().toString()
                    + " и/или длительность "
                    + task.getDuration().toMinutes()
                    + " минут, так как она будет пересекается с одной из имеющихся задач (подзадач)";
            throw new CrossTaskException(message);
        }
        tasks.put(task.getId(), task);
        if (tasks.get(task.getId()).hasTimeAndDuration()) {
            prioritizedTasks.remove(task);
        }
        if (task.hasTimeAndDuration()) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            return;
        }

        if (checkCross(subTask)) {
            String message = "Невозможно установить время начала подзадачи  "
                    + subTask.getStartTime().toString()
                    + " и длительность "
                    + subTask.getDuration().toMinutes()
                    + " минут, так как она будет пересекается с одной из имеющихся задач (подзадач)";
            throw new CrossTaskException(message);
        }
        Duration oldDuration = subTasks.get(subTask.getId()).getDuration();
        subTasks.put(subTask.getId(), subTask);
        if (subTasks.get(subTask.getId()).hasTimeAndDuration()) {
            prioritizedTasks.remove(subTask);
        }
        if (subTask.hasTimeAndDuration()) {
            prioritizedTasks.add(subTask);
        }
        calculateEpicTaskStatus(epicTasks.get(subTask.getParentTaskId()));
        updateEpicTaskStartTime(epicTasks.get(subTask.getParentTaskId()), subTask);
        updateEpicTaskEndTime(epicTasks.get(subTask.getParentTaskId()), subTask);
        updateEpicTaskDuration(epicTasks.get(subTask.getParentTaskId()), subTask, oldDuration);
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        if (!epicTasks.containsKey(epicTask.getId())) {
            return;
        }
        epicTasks.get(epicTask.getId()).setTitle(epicTask.getTitle());
        epicTasks.get(epicTask.getId()).setDescription(epicTask.getDescription());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void calculateEpicTaskStatus(EpicTask epicTask) {
        if (!epicTask.hasSubTasks()) {
            epicTask.setStatus(TaskStatus.NEW);
            return;
        }

        Set<TaskStatus> s = epicTask.getSubTasksIds().stream()
                .map(stId -> subTasks.get(stId).getStatus())
                .collect(Collectors.toSet());

        if (s.size() == 1 && s.contains(TaskStatus.NEW)) {
            epicTask.setStatus(TaskStatus.NEW);
            return;
        }

        if (s.size() == 1 && s.contains(TaskStatus.DONE)) {
            epicTask.setStatus(TaskStatus.DONE);
            return;
        }

        epicTask.setStatus(TaskStatus.IN_PROGRESS);
    }

    /**
     * Полностью пересчитывает начальное и финальное время эпика, а также его длительность
     * @param epicTask эпик, для которого происходит рассчет
     */
    protected void calculateEpicTaskTimeAndDuration(EpicTask epicTask) {
        List<Integer> ids = epicTask.getSubTasksIds();
        List<SubTask> subTasks = ids.stream()
                .map(this.subTasks::get)
                .filter(st -> st.getStartTime() != null)
                .sorted()
                .toList();

        Duration duration = Duration.ofMinutes(
                ids.stream()
                        .map(this.subTasks::get)
                        .filter(st -> st.getDuration() != null)
                        .map(st -> st.getDuration().toMinutes())
                        .mapToLong(Long::longValue)
                        .sum()
        );

        if (!subTasks.isEmpty()) {
            epicTask.setStartTime(subTasks.getFirst().getStartTime());
            epicTask.setEndTime(subTasks.getLast().getEndTime());
            epicTask.setDuration(duration);
        }
    }

    /**
     * Обновление начального времени эпика (используется при добавлении или обновлении подзадачи)
     * @param epicTask эпик, начальное время которого нужно обновить
     * @param subTask добавляемая или обновляемая подзадача
     */
    protected void updateEpicTaskStartTime(EpicTask epicTask, SubTask subTask) {
        if (subTask.getStartTime() == null) {
            calculateEpicTaskTimeAndDuration(epicTask);
            return;
        }
        if (epicTask.getStartTime() == null || epicTask.getStartTime().isAfter(subTask.getStartTime())) {
            epicTask.setStartTime(subTask.getStartTime());
        }
    }

    /**
     * Обновление финального времени эпика (используется при добавлении или обновлении подзадачи)
     * @param epicTask эпик, финальное время которого нужно обновить
     * @param subTask добавляемая или обновляемая подзадача
     */
    protected void updateEpicTaskEndTime(EpicTask epicTask, SubTask subTask) {
        if (subTask.getEndTime() == null) {
            return;
        }
        if (epicTask.getEndTime() == null || epicTask.getEndTime().isBefore(subTask.getEndTime())) {
            epicTask.setEndTime(subTask.getEndTime());
        }
    }

    /**
     * Обновление длительности эпика (используется при добавлении или обновлении подзадачи)
     * @param epicTask эпик, длительность которого нужно обновить
     * @param subTask добавляемая или обновляемая подзадача
     */
    protected void updateEpicTaskDuration(EpicTask epicTask, SubTask subTask, Duration oldSubTaskDuration) {
        if (epicTask.getDuration() != null) {
            epicTask.setDuration(epicTask.getDuration().minus(oldSubTaskDuration));
            if (epicTask.getDuration().isZero()) {
                epicTask.setDuration(null);
            }
        }
        if (subTask.getDuration() == null) {
            return;
        }
        if (epicTask.getDuration() == null) {
            epicTask.setDuration(subTask.getDuration());
        } else {
            epicTask.setDuration(epicTask.getDuration().plus(subTask.getDuration()));
        }
    }

    public List<Task> getPrioritizedTasks() {
        return new LinkedList<>(prioritizedTasks);
    }

    /**
     * Проверка пересечения двух задач между собой
     * @param task1 задача 1
     * @param task2 задача 2
     * @return true - если время задач пересекается, false - если нет
     */
    private boolean checkCross(Task task1, Task task2) {
        if (!task1.hasTimeAndDuration() || !task2.hasTimeAndDuration()) {
            return false;
        }
        if (task1.getStartTime().isEqual(task2.getStartTime())) {
            return true;
        }
        LocalDateTime maxStart;
        LocalDateTime minEnd;
        if (task1.getStartTime().isBefore(task2.getStartTime())) {
            maxStart = task2.getStartTime();
            minEnd = task1.getEndTime();
        } else {
            maxStart = task1.getStartTime();
            minEnd = task2.getEndTime();
        }
        return !maxStart.isAfter(minEnd);
    }

    /**
     * Проверка пересечения задачи с хранящимися в менеджере
     * @param newTask задача для проверки
     * @return true - если задача пересекается с одной или более задачей в менеджере
     */
    private boolean checkCross(Task newTask) {
        if (!newTask.hasTimeAndDuration()) {
            return false;
        }
        return prioritizedTasks.stream()
                .filter(task -> newTask.getId() != task.getId())
                .anyMatch(task -> checkCross(task, newTask));
    }

    private int getNextId() {
        return nextId++;
    }
}
