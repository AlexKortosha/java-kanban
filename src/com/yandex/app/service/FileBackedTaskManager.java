package com.yandex.app.service;

import com.yandex.app.model.Task;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.TaskType;
import com.yandex.app.model.TaskStatus;


import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }
    
    //================================SAVE==============================================================================
    public void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task: getAllTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic: getAllEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (SubTask subTask: getAllSubtasks()) {
                writer.write(toString(subTask));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

                if (task instanceof  Epic) {                //instanceof- для проверки экземпляра класса/наследника
                    manager.addEpic((Epic) task);
                }  else if (task instanceof SubTask) {
                    manager.addSubTask((SubTask) task);
                } else {
                    manager.addTask(task);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке файла: " + file, e);
        }
        return manager;
    }

    //================================CSV format========================================================================
    private static String toString(Task task) {
        StringBuilder strBuild = new StringBuilder();
        strBuild.append(task.getId()).append(",");
        if (task instanceof Epic) {
            strBuild.append(TaskType.EPIC);
        } else if (task instanceof SubTask) {
            strBuild.append(TaskType.SUBTASK);
        } else {
            strBuild.append(TaskType.TASK);
        }
        strBuild.append(",").append(task.getName());
        strBuild.append(",").append(task.getStatus());
        strBuild.append(",").append(task.getDescription());

        if (task instanceof SubTask) {
            strBuild.append(",").append(((SubTask) task).getEpicId());
        } else {
            strBuild.append(",");
        }
        return strBuild.toString();
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

    //==============================OVERRIDE METODS=====================================================================


    @Override
    public Task addTask(Task task) {
        Task t = super.addTask(task);
        save();
        return t;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        SubTask st = super.addSubTask(subTask);
        save();
        return st;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic e = super.addEpic(epic);
        save();
        return e;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateSubtask(SubTask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }
}
