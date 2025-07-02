package com.yandex.app.servise;

import com.yandex.app.model.Task;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int newId = 1;

    //геттеры для списков tasks, epics, subTasks


    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<SubTask> getSubTasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if(epic == null) {
            System.out.println("Эпик и индификатором " + epicId + " не найден" );
        }
        return new ArrayList<>(epic.getSubTaskIds().values());
    }


    // РАЗДЕЛ TASK======================================================================================================
    public Task addTask(Task task) {
        validateAndSetId(task);                 // проверка на индификатор,
        tasks.put(task.getId(), task);
        return task;

    }


    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void updateTask(Task updatedTask) {
        tasks.computeIfPresent(updatedTask.getId(), (id, existingTask) -> updatedTask);
    }

    public Task findTaskById(int id) {
        Task task = tasks.get(id);
        return task;
    }


    // РАЗДЕЛ SUBTASK===================================================================================================
    public SubTask addSubTask(SubTask subTask) {
        validateAndSetId(subTask);
        if (!epics.containsKey(subTask.getEpicId())) {
            System.out.println("Epic not found");
            return null;
        } else {
            subTasks.put(subTask.getId(), subTask);
            epics.get(subTask.getEpicId()).getSubTaskIds().put(subTask.getId(), subTask);
            updateEpicStatus(subTask.getEpicId()); // обновление статуса эпика
            return subTask;
        }
    }

    public void deleteAllSubTask() {
        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubTaskIds().clear();
            epic.updateStatus();
        }
    }

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


    public SubTask findSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        return subTask;
    }

    // РАЗДЕЛ EPIC======================================================================================================
    public Epic addEpic(Epic epic) {
        validateAndSetId(epic);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void deleteAllEpic() {
        for (Epic epic : epics.values()) {
            for (Integer subTaskId : epic.getSubTaskIds().keySet()) {
                subTasks.remove(subTaskId);
            }
        }
        epics.clear();
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubTaskIds().keySet()) {
                subTasks.remove(subtaskId);
            }
        }
    }

    public void updateEpic(Epic updatedEpic) {
        epics.computeIfPresent(updatedEpic.getId(), (id, existing) -> {
            updatedEpic.getSubTaskIds().putAll(existing.getSubTaskIds());
            return updatedEpic;
        });
    }

    public Epic findEpicById(int id) {
        Epic epic = epics.get(id);
        return epic;
    }

    //вспомогательный методы===================================================================
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
