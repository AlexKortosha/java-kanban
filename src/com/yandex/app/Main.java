package com.yandex.app;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Task;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.TaskStatus;

import com.yandex.app.serviсe.InMemoryTaskManager;
import com.yandex.app.serviсe.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();

        System.out.println("###СОЗДАНИЕ ЗАДАЧ###");

        Task task1 = manager.addTask(new Task(0,"Название", "Описание"));
        Task task2 = manager.addTask(new Task(0,"Название1", "Описание1"));


        Epic epic1 = manager.addEpic(new Epic(0,"Задача1", "Задача2"));

        SubTask subtask1 = manager.addSubTask(new SubTask(0,"Подзадача1:", "Описание подзадачи1", epic1.getId()));
        SubTask subtask2 = manager.addSubTask(new SubTask(0,"подзадача2:", "Описание подзадачи2",  epic1.getId()));


        Epic epic2 = manager.addEpic(new Epic(0,"Задача3", "" ));
        SubTask subtask3 = manager.addSubTask(new SubTask(0,"Подзадача3:", "Описание подзадачи3", epic2.getId()));


        //ПЕЧАТЬ ЗАДАЧ
        System.out.println("\n=== Все задачи ===");
        System.out.println("Обычные задачи: " + manager.getAllTasks());
        System.out.println("Эпики: " + manager.getAllEpics());
        System.out.println("Подзадачи: " + manager.getAllSubtasks());

        //СТАТУСЫ
        System.out.println("\n=== Изменение статусов ===");
        task1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task1);

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        subtask3.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask3);


        //ОБНОВЛЕНИЕ ЗАДАЧ
        System.out.println("\n=== Обновленные задачи ===");
        System.out.println("Обычные задачи: " + manager.getAllTasks());
        System.out.println("Эпики: " + manager.getAllEpics());
        System.out.println("Подзадачи: " + manager.getAllSubtasks());

        //УДАЛЕНИЕ
        System.out.println("\n=== Удаление задач ===");
        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic1.getId());

        System.out.println("\n=== Финальный список задач ===");
        System.out.println("Обычные задачи: " + manager.getAllTasks());
        System.out.println("Эпики: " + manager.getAllEpics());
        System.out.println("Подзадачи: " + manager.getAllSubtasks());


    }
}
