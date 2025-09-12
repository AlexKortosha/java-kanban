package com.yandex.app.service;

import com.yandex.app.exception.ManagerSaveException;
import com.yandex.app.model.Task;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.TaskType;
import com.yandex.app.model.TaskStatus;


import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private static final String HEADER_CSV = "id,type,name,status,description,epic";

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    //================================SAVE==============================================================================
    public void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER_CSV);
            writer.newLine();

            for (Task task : getAllTasks()) {
                writer.write(task.toString());
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(epic.toString());
                writer.newLine();
            }
            for (SubTask subTask : getAllSubtasks()) {
                writer.write(subTask.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("При сохранении файла возникла ошибка: " + file, e);
        }
    }

    //================================LOAD==============================================================================
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                Task task = fromString(line);

                String[] fields = line.split(",");
                TaskType type = TaskType.valueOf(fields[1]);

                switch (type) {
                    case EPIC:
                        manager.addEpicWithoutSaving((Epic) task);
                        break;
                    case SUBTASK:
                        manager.addSubTaskWithoutSaving((SubTask) task);
                        break;
                    case TASK:
                        manager.addTaskWithoutSaving(task);
                        break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке файла: " + file, e);
        }
        return manager;
    }

    //================================CREATE TASK FROM STRING===========================================================
    private static Task fromString(String value) {
        String[] fields = value.split(",", -1);
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                Task task = new Task(id, name, description);
                task.setStatus(status);
                return task;
            case EPIC:
                Epic epic = new Epic(id, name, description);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                SubTask subTask = new SubTask(id, name, description, epicId);
                subTask.setStatus(status);
                return subTask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    private void addTaskWithoutSaving(Task task) {
       super.addTask(task);
    }

    private void addEpicWithoutSaving(Epic epic) {
        super.addEpic(epic);
    }

    private void addSubTaskWithoutSaving(SubTask subTask) {
        super.addSubTask(subTask);
    }
}
