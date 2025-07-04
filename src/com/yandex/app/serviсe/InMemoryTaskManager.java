package com.yandex.app.serviсe;

import com.yandex.app.model.Task;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }


    private int newId = 1;

    // РАЗДЕЛ TASK======================================================================================================

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
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
        tasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void updateTask(Task updatedTask) {
        tasks.computeIfPresent(updatedTask.getId(), (id, existingTask) -> updatedTask);
    }

    @Override
    public Task findTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    // РАЗДЕЛ SUBTASK===================================================================================================

    @Override
    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getSubTasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if(epic == null) {
            System.out.println("Эпик и индификатором " + epicId + " не найден" );
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
        if (subTask != null) {
            historyManager.add(subTask);
        }
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
            for (Integer subTaskId : epic.getSubTaskIds().keySet()) {
                subTasks.remove(subTaskId);
            }
        }
        epics.clear();
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubTaskIds().keySet()) {
                subTasks.remove(subtaskId);
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
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }
    //NEW FUNCTIONALITY=================================================================================================

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //вспомогательный методы============================================================================================
    private void validateAndSetId(Task task) {

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

}
