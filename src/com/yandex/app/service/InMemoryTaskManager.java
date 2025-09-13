package com.yandex.app.service;

import com.yandex.app.model.Task;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.TaskStatus;

import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator
                    .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getId)
    );


    private int newId = 1;

    // РАЗДЕЛ TASK======================================================================================================

    @Override
    public List<Task> getAllTasks() {
        return List.copyOf(tasks.values());
    }

    @Override
    public Task addTask(Task task) {
        if (task == null) return null;

        // Присвоение id перед созданием копии
        validateAndSetId(task);

        // Создаём новую задачу и копируем все поля
        Task taskToAdd = new Task(task.getId(), task.getName(), task.getDescription());
        taskToAdd.setStatus(task.getStatus());
        taskToAdd.setStartTime(task.getStartTime());
        taskToAdd.setDuration(task.getDuration());

        if (hasTimeConflict(taskToAdd)) {
            System.out.println("Ошибка: задача пересекается по времени с другой.");
            return null;
        }

        tasks.put(taskToAdd.getId(), taskToAdd);
        prioritizedTasks.add(taskToAdd);
        return taskToAdd;
    }


    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            prioritizedTasks.remove(removed);
            historyManager.remove(id);
        }
    }

    @Override
    public void updateTask(Task updatedTask) {

        if (hasTimeConflict(updatedTask)) {
            System.out.println("Ошибка: задача пересекается по времени с другой.");
            return;
        }

        Task oldTask = tasks.get(updatedTask.getId());
        if (oldTask != null) {
            prioritizedTasks.remove(oldTask);
            tasks.put(updatedTask.getId(), updatedTask);
            prioritizedTasks.add(updatedTask);
        }
    }

    @Override
    public Task findTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        historyManager.add(task);
        return task;
    }

    // РАЗДЕЛ SUBTASK===================================================================================================

    @Override
    public List<SubTask> getAllSubtasks() {
        return List.copyOf(subTasks.values());
    }

    @Override
    public List<SubTask> getSubTasksByEpicId(int epicId) {
       return Optional.ofNullable(epics.get(epicId))
               .map(e -> e.getSubTaskIds().values().stream().toList())
               .orElseGet(() -> {
                   System.out.println("Эпик с идентификатором " + epicId + " не найден");
                   return List.of();
               });
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        if (subTask == null) return null;
        if (subTask.getId() == subTask.getEpicId()) return null;
        if (!epics.containsKey(subTask.getEpicId())) {
            System.out.println("Epic not found");
            return null;
        }
        if (subTasks.containsKey(subTask.getId())) return null;

        validateAndSetId(subTask);

        SubTask subTaskToAdd = new SubTask(subTask.getId(), subTask.getName(), subTask.getDescription(), subTask.getEpicId());
        subTaskToAdd.setStatus(subTask.getStatus());
        subTaskToAdd.setStartTime(subTask.getStartTime());
        subTaskToAdd.setDuration(subTask.getDuration());

        if (hasTimeConflict(subTaskToAdd)) {
            System.out.println("Ошибка: подзадача пересекается по времени с другой.");
            return null;
        }

        subTasks.put(subTaskToAdd.getId(), subTaskToAdd);
        prioritizedTasks.add(subTaskToAdd);
        Epic epic = epics.get(subTaskToAdd.getEpicId());
        epic.addSubtask(subTaskToAdd);

        updateEpicStatus(epic.getId());
        updateEpicTime(epic.getId());

        return subTaskToAdd;
    }

    @Override
    public void deleteAllSubTask() {
        prioritizedTasks.removeAll(subTasks.values());
        Set<Integer> affectEpics = subTasks.values().stream()
                .map(SubTask::getEpicId)
                .collect(Collectors.toSet());

        subTasks.clear();

        for (int epicId : affectEpics) {
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.getSubTaskIds().clear();
                updateEpicStatus(epicId);
                updateEpicTime(epicId); // Добавлен вызов
            }
        }
    }

    @Override
    public void deleteSubtask(int id) {
        SubTask subtask = subTasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                epic.updateStatus();
                updateEpicTime(epic.getId());
            }

        }
    }

    @Override
    public void updateSubtask(SubTask updatedSubtask) {
        if (updatedSubtask == null) return;

        if (hasTimeConflict(updatedSubtask)) {
            System.out.println("Ошибка: подзадача пересекается по времени с другой.");
            return;
        }

        SubTask oldSubtask = subTasks.get(updatedSubtask.getId());
        if (oldSubtask != null) {
            prioritizedTasks.remove(oldSubtask);
            subTasks.put(updatedSubtask.getId(), updatedSubtask);
            prioritizedTasks.add(updatedSubtask);

            Epic epic = epics.get(updatedSubtask.getEpicId());
            if (epic != null) {
                epic.getSubTaskIds().put(updatedSubtask.getId(), updatedSubtask);
                updateEpicStatus(epic.getId());
                updateEpicTime(epic.getId());
            }
        }

    }


    @Override
    public SubTask findSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    // РАЗДЕЛ EPIC======================================================================================================

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }


    @Override
    public Epic addEpic(Epic epic) {
        validateAndSetId(epic);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void deleteAllEpic() {
        for (Epic epic : epics.values()) {

            historyManager.remove(epic.getId());

            for (Integer subId : epic.getSubTaskIds().keySet()) {
                SubTask sub = subTasks.remove(subId);
                if (sub != null) prioritizedTasks.remove(sub);
                historyManager.remove(subId);
            }
        }
        epics.clear();
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {

            historyManager.remove(id);

            for (Integer subId : epic.getSubTaskIds().keySet()) {
                SubTask sub = subTasks.remove(subId);
                if (sub != null) prioritizedTasks.remove(sub);
                historyManager.remove(subId);
            }
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        epics.computeIfPresent(updatedEpic.getId(), (id, existing) -> {
            updatedEpic.getSubTaskIds().putAll(existing.getSubTaskIds());
            return updatedEpic;
        });
    }

    @Override
    public Epic findEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    private void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<SubTask> epicSubTasks = getSubTasksByEpicId(epicId);
        epic.updateTime(epicSubTasks);
    }
    //NEW FUNCTIONALITY=================================================================================================

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //вспомогательный методы============================================================================================
    protected void validateAndSetId(Task task) {

        if (task == null) {
            System.out.println("Задача не может быть = null");
        }


        if (task.getId() == 0) {
            task.setId(newId++);
        } else if (task.getId() >= newId) {
            newId = task.getId() + 1;
        }
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<SubTask> subs = getSubTasksByEpicId(epicId);

        if (subs.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = subs.stream().allMatch(s -> s.getStatus() == TaskStatus.NEW);
        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = subs.stream().allMatch(s -> s.getStatus() == TaskStatus.DONE);
        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }

        // если есть хотя бы один IN_PROGRESS или смесь NEW + DONE → IN_PROGRESS
        epic.setStatus(TaskStatus.IN_PROGRESS);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
      return new ArrayList<>(prioritizedTasks);
    }

    private boolean hasTimeConflict(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }

        for (Task existing : getPrioritizedTasks()) {
            if (existing.getId() == newTask.getId()) continue;

            if (existing.getStartTime() != null && existing.getEndTime() != null) {
                boolean overlap = !(newTask.getEndTime().isBefore(existing.getStartTime())
                                 || newTask.getStartTime().isAfter(existing.getEndTime()));
                if (overlap) {
                    return  true;
                }
            }
        }
        return false;

    }
}
