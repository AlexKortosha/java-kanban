package com.yandex.app.serviсe;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.List;

public interface TaskManager {

    // РАЗДЕЛ TASK======================================================================================================
    List<Task> getAllTasks();

    Task addTask(Task task);

    void deleteAllTasks();

    void deleteTask(int id);

    void updateTask(Task updatedTask);

    Task findTaskById(int id);

    // РАЗДЕЛ SUBTASK===================================================================================================
    List<SubTask> getAllSubtasks();

    List<SubTask> getSubTasksByEpicId(int epicId);

    SubTask addSubTask(SubTask subTask);

    void deleteAllSubTask();

    void deleteSubtask(int id);

    void updateSubtask(SubTask updatedSubtask);

    SubTask findSubTaskById(int id);

    // РАЗДЕЛ EPIC======================================================================================================
    List<Epic> getAllEpics();

    Epic addEpic(Epic epic);

    void deleteAllEpic();

    void deleteEpic(int id);

    void updateEpic(Epic updatedEpic);

    Epic findEpicById(int id);

    // NEW FUNCTIONALITY================================================================================================

    List<Task> getHistory();
}
