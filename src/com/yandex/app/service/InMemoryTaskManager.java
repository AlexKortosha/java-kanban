package com.yandex.app.service;

import com.yandex.app.model.Task;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }


    private int newId = 1;

    // РАЗДЕЛ TASK======================================================================================================

    @Override
    public List<Task> getAllTasks() {
        return List.copyOf(tasks.values());
    }

    @Override
    public Task addTask(Task task) {
        if (task == null) {
            return null;
        }

        Task taskToAdd = new Task(task.getId(), task.getName(), task.getDescription());
        taskToAdd.setStatus(task.getStatus());

        validateAndSetId(taskToAdd);
        tasks.put(taskToAdd.getId(), taskToAdd);
        return taskToAdd;
    }


    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void updateTask(Task updatedTask) {
        tasks.computeIfPresent(updatedTask.getId(), (id, existingTask) -> updatedTask);
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
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Эпик и индификатором " + epicId + " не найден");
        }
        return new ArrayList<>(epic.getSubTaskIds().values());
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        if (subTask == null) {
            return null;
        }

        if (subTask.getId() == subTask.getEpicId()) {
            return null;
        }


        if (!epics.containsKey(subTask.getEpicId())) {
            System.out.println("Epic not found");
            return null;
        }

        if (epics.containsKey(subTask.getId())) {
            return null;
        }

        validateAndSetId(subTask);
        subTasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).addSubtask(subTask);
        updateEpicStatus(subTask.getEpicId());
        return subTask;
    }

    @Override
    public void deleteAllSubTask() {
        for (Integer subTaskId : subTasks.keySet()) {
            historyManager.remove(subTaskId);
        }
        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubTaskIds().clear();
            epic.updateStatus();
        }
    }

    @Override
    public void deleteSubtask(int id) {
        SubTask subtask = subTasks.remove(id);
        if (subtask != null) {
            historyManager.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                epic.updateStatus();
            }

        }
    }

    @Override
    public void updateSubtask(SubTask updatedSubtask) {
        subTasks.computeIfPresent(updatedSubtask.getId(), (id, existing) -> {
            Epic epic = epics.get(updatedSubtask.getEpicId());

            if (epic == null) {
                System.out.println("Эпик не найден для " + id);
            }

            epic.getSubTaskIds().put(id, updatedSubtask);

            epic.updateStatus();

            return updatedSubtask;
        });
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

            for (Integer subtaskId : epic.getSubTaskIds().keySet()) {
                subTasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
        }
        epics.clear();
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {

            historyManager.remove(id);

            for (Integer subtaskId : epic.getSubTaskIds().keySet()) {
                subTasks.remove(subtaskId);
                historyManager.remove(subtaskId);
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
        ;

        if (task.getId() == 0) {
            task.setId(newId++);
        } else if (task.getId() >= newId) {
            newId = task.getId() + 1;
        }
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.updateStatus();
        }
    }
    //МЕТОДЫ РОДИТЕЛИ===================================================================================================

    protected Task addTaskParental(Task task) {
        validateAndSetId(task);
        tasks.put(task.getId(), task);
        return task;
    }

    protected Epic addEpicParental(Epic epic) {
        validateAndSetId(epic);
        epics.put(epic.getId(), epic);
        return  epic;
    }

    protected SubTask addSubTaskParental(SubTask subTask) {
        validateAndSetId(subTask);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Такой epic не найден: " + subTask.getEpicId());
        }
        subTasks.put(subTask.getEpicId(), subTask);
        epic.addSubtask(subTask);
        epic.updateStatus();
        return subTask;
    }
}
