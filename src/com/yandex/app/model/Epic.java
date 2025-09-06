package com.yandex.app.model;
import java.util.HashMap;

public class Epic extends Task {

    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public HashMap<Integer, SubTask> getSubTaskIds() {
        return subtasks;
    }

    public void addSubtask(SubTask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public void removeSubtask(int subtaskId) {
        subtasks.remove(subtaskId);
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (SubTask subtask : subtasks.values()) {
            if (subtask.getStatus() != TaskStatus.DONE) allDone = false;
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) anyInProgress = true;
        }

        if (allDone) {
            setStatus(TaskStatus.DONE);
        } else if (anyInProgress) {
            setStatus(TaskStatus.IN_PROGRESS);
        } else {
            setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public String toString() {
        return id + ","
                + TaskType.EPIC + ","
                + getName() + ","
                + getStatus() + ","
                + getDescription() + ",";
    }
}